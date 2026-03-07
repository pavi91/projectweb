package com.hotelreservation.service.impl;

import com.hotelreservation.adapter.OnlineGatewayAdapter;
import com.hotelreservation.adapter.POSAdapter;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.entity.Guest;
import com.hotelreservation.entity.Reservation;
import com.hotelreservation.entity.Room;
import com.hotelreservation.exception.PaymentException;
import com.hotelreservation.exception.RoomNotAvailableException;
import com.hotelreservation.mapper.GuestMapper;
import com.hotelreservation.mapper.RoomMapper;
import com.hotelreservation.repository.GuestRepository;
import com.hotelreservation.repository.ReservationRepository;
import com.hotelreservation.repository.RoomRepository;
import com.hotelreservation.service.PaymentService;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.service.SeasonalPricingService;
import com.hotelreservation.strategy.IPricingStrategy;
import com.hotelreservation.strategy.StandardRateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * BookingService - Facade pattern implementation
 * Orchestrates the complete booking workflow by coordinating multiple services
 * Controllers interact only with this facade, not individual services
 */
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private OnlineResService onlineResService;
    private WalkInResService walkInResService;
    private RoomService roomService;
    private PaymentService paymentService;
    private ReservationRepository reservationRepository;
    private GuestRepository guestRepository;
    private SeasonalPricingService seasonalPricingService;

    public BookingService(
            OnlineResService onlineResService,
            WalkInResService walkInResService,
            RoomService roomService,
            PaymentService paymentService,
            ReservationRepository reservationRepository,
            GuestRepository guestRepository) {
        this(onlineResService, walkInResService, roomService, paymentService, reservationRepository, guestRepository, null);
    }

    public BookingService(
            OnlineResService onlineResService,
            WalkInResService walkInResService,
            RoomService roomService,
            PaymentService paymentService,
            ReservationRepository reservationRepository,
            GuestRepository guestRepository,
            SeasonalPricingService seasonalPricingService) {
        this.onlineResService = onlineResService;
        this.walkInResService = walkInResService;
        this.roomService = roomService;
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.seasonalPricingService = seasonalPricingService;
    }

    /**
     * Make an online reservation for a guest
     * Workflow: Validate → Map DTOs → Create Reservation → Process Payment → Send Email
     * @param guestDTO guest details
     * @param roomId room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return ReservationDTO with confirmation details
     * @throws Exception if reservation fails
     */
    public ReservationDTO makeOnlineReservation(GuestDTO guestDTO, int roomId, LocalDate checkIn, LocalDate checkOut) throws Exception {
        logger.info("Starting online reservation for guest: {}, Room: {}", guestDTO.getName(), roomId);

        try {
            // Validate input
            validateReservationInput(guestDTO, roomId, checkIn, checkOut);

            // Get room details
            Room room = getRoomEntity(roomId);

            // Resolve guest from DB by NIC, or create a new guest record
            Guest guest = resolveOrCreateGuest(guestDTO);

            // Set dates for reservation service
            onlineResService.setReservationDates(checkIn, checkOut);
            onlineResService.setPricingStrategy(resolveStrategyForDate(checkIn));

            // Process booking (creates and saves reservation)
            Reservation reservation = onlineResService.processBooking(guest, room);

            if (reservation == null) {
                throw new Exception("Failed to create reservation");
            }

            // Process payment via Online Gateway (card payment)
            paymentService.setPaymentAdapter(new OnlineGatewayAdapter());
            double totalAmount = reservation.getTotalAmount();
            boolean paymentSuccess = paymentService.processPayment(totalAmount);
            if (!paymentSuccess) {
                // Rollback reservation on payment failure
                reservation.cancel();
                reservationRepository.update(reservation);
                throw new PaymentException("Payment declined");
            }

            // Update room status to RESERVED
            room.updateStatus("RESERVED");
            roomService.updateRoomStatus(roomId, "RESERVED");

            // Send confirmation email
            onlineResService.sendConfirmationEmail(guest, reservation);

            logger.info("Online reservation completed successfully: {}", reservation.getId());
            return mapToDTO(reservation, guest, room);
        } catch (Exception e) {
            logger.error("Error during online reservation", e);
            throw e;
        }
    }

    /**
     * Make a walk-in reservation for a guest
     * Workflow: Validate → Map DTOs → Create Reservation → Process Payment (POS) → Print Receipt
     * @param guestDTO guest details
     * @param roomId room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return ReservationDTO with confirmation details
     * @throws Exception if reservation fails
     */
    public ReservationDTO makeWalkInReservation(GuestDTO guestDTO, int roomId, LocalDate checkIn, LocalDate checkOut) throws Exception {
        logger.info("Starting walk-in reservation for guest: {}, Room: {}", guestDTO.getName(), roomId);

        try {
            // Validate input
            validateReservationInput(guestDTO, roomId, checkIn, checkOut);

            // Get room details
            Room room = getRoomEntity(roomId);

            // Resolve guest from DB by NIC, or create a new guest record
            Guest guest = resolveOrCreateGuest(guestDTO);

            // Set dates for reservation service
            walkInResService.setReservationDates(checkIn, checkOut);
            walkInResService.setPricingStrategy(resolveStrategyForDate(checkIn));

            // Process booking (creates and saves reservation)
            Reservation reservation = walkInResService.processBooking(guest, room);

            // Process payment via POS terminal
            paymentService.setPaymentAdapter(new POSAdapter());
            double totalAmount = reservation.getTotalAmount();
            boolean paymentSuccess = paymentService.processPayment(totalAmount);
            if (!paymentSuccess) {
                // Rollback reservation on payment failure
                reservation.cancel();
                reservationRepository.update(reservation);
                throw new PaymentException("Payment declined at POS terminal");
            }

            // Update room status to RESERVED
            room.updateStatus("RESERVED");
            roomService.updateRoomStatus(roomId, "RESERVED");

            // Print receipt at POS terminal
            walkInResService.printReservationReceipt(guest, reservation, room);

            logger.info("Walk-in reservation completed successfully: {}", reservation.getId());
            return mapToDTO(reservation, guest, room);
        } catch (Exception e) {
            logger.error("Error during walk-in reservation", e);
            throw e;
        }
    }

    /**
     * Check in a guest for a reservation
     * Updates room status to OCCUPIED
     * @param reservationId the reservation ID
     * @throws Exception if check-in fails
     */
    public void checkIn(String reservationId) throws Exception {
        logger.info("Checking in guest for reservation: '{}' (length={})", reservationId, reservationId != null ? reservationId.length() : "null");

        try {
            // DEBUG: Direct SQL test to bypass DAO layer
            try (java.sql.Connection conn = com.hotelreservation.persistence.DatabaseConnection.getInstance().getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id, status FROM reservations WHERE id = ?")) {
                ps.setString(1, reservationId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        logger.info("DEBUG DIRECT SQL: Found reservation '{}' with status '{}'", rs.getString("id"), rs.getString("status"));
                    } else {
                        logger.error("DEBUG DIRECT SQL: NO ROW found for id='{}'", reservationId);
                        // Also check total count
                        try (java.sql.PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM reservations")) {
                            try (java.sql.ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    logger.error("DEBUG DIRECT SQL: Total reservations in table = {}", rs2.getInt("cnt"));
                                }
                            }
                        }
                    }
                }
            } catch (Exception dbg) {
                logger.error("DEBUG DIRECT SQL failed", dbg);
            }

            java.util.Optional<Reservation> found = reservationRepository.findById(reservationId);
            logger.info("findById result for '{}': present={}", reservationId, found.isPresent());

            Reservation reservation = found
                    .orElseThrow(() -> new Exception("Reservation not found: " + reservationId));

            // Confirm and check in
            reservation.checkIn();
            reservationRepository.update(reservation);

            // Update room status
            roomService.updateRoomStatus(reservation.getRoomId(), "OCCUPIED");

            logger.info("Check-in successful for reservation: {}", reservationId);
        } catch (Exception e) {
            logger.error("Error during check-in", e);
            throw e;
        }
    }

    /**
     * Check out a guest and generate final bill
     * Updates room status to AVAILABLE
     * @param reservationId the reservation ID
     * @return ReservationDTO with final bill
     * @throws Exception if check-out fails
     */
    public ReservationDTO checkOut(String reservationId) throws Exception {
        logger.info("Checking out guest for reservation: {}", reservationId);

        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new Exception("Reservation not found: " + reservationId));

            // Check out and finalize
            reservation.checkOut();
            reservationRepository.update(reservation);

            // Update room: set AVAILABLE but mark as DIRTY — needs cleaning by maintenance before re-booking
            roomService.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");
            roomService.markRoomDirty(reservation.getRoomId());

            logger.info("Check-out successful for reservation: {}", reservationId);

            // Return reservation details with bill (use getRoomFromService — no availability check needed)
            Room room = getRoomFromService(reservation.getRoomId());
            if (room == null) {
                throw new Exception("Room not found for reservation: " + reservationId);
            }
            ReservationDTO dto = new ReservationDTO(
                reservation.getId(),
                reservation.getGuestId(),
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getTotalAmount()
            );
            dto.setStatus(reservation.getStatus());
            dto.setRoom(RoomMapper.toDTO(room));
            return dto;
        } catch (Exception e) {
            logger.error("Error during check-out", e);
            throw e;
        }
    }

    /**
     * Cancel a reservation and process refund
     * @param reservationId the reservation ID to cancel
     * @return true if cancellation successful
     * @throws Exception if cancellation fails
     */
    public boolean cancelReservation(String reservationId) throws Exception {
        logger.info("Cancelling reservation: {}", reservationId);

        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new Exception("Reservation not found: " + reservationId));

            if ("CANCELLED".equals(reservation.getStatus())) {
                logger.warn("Reservation already cancelled: {}", reservationId);
                return false;
            }

            // Update reservation status
            reservation.cancel();
            reservationRepository.update(reservation);

            // Update room status back to AVAILABLE if it wasn't occupied
            if (!"CHECKED_OUT".equals(reservation.getStatus())) {
                roomService.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");
            }

            // Refunds are handled offline — no automatic refund processing
            logger.info("Reservation cancelled successfully: {}. Refund to be handled offline.", reservationId);
            return true;
        } catch (Exception e) {
            logger.error("Error cancelling reservation", e);
            throw e;
        }
    }

    /**
     * Get reservation details
     * @param reservationId the reservation ID
     * @return ReservationDTO if found
     */
    public ReservationDTO getReservation(String reservationId) {
        try {
            return reservationRepository.findById(reservationId)
                    .map(this::mapToDTO)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error retrieving reservation: {}", reservationId, e);
            return null;
        }
    }

    /**
     * Get all reservations
     * @return list of all ReservationDTOs
     */
    public List<ReservationDTO> getAllReservations() {
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            List<ReservationDTO> dtos = new java.util.ArrayList<>();
            for (Reservation reservation : reservations) {
                dtos.add(mapToDTO(reservation));
            }
            logger.info("Retrieved {} reservations", dtos.size());
            return dtos;
        } catch (Exception e) {
            logger.error("Error retrieving all reservations", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get all reservations for a specific guest
     * @param guestId the guest ID
     * @return list of ReservationDTOs for the guest
     */
    public List<ReservationDTO> getReservationsByGuest(int guestId) {
        try {
            List<Reservation> reservations = reservationRepository.findByGuest(guestId);
            List<ReservationDTO> dtos = new java.util.ArrayList<>();
            for (Reservation reservation : reservations) {
                dtos.add(mapToDTO(reservation));
            }
            logger.info("Retrieved {} reservations for guest {}", dtos.size(), guestId);
            return dtos;
        } catch (Exception e) {
            logger.error("Error retrieving reservations for guest: {}", guestId, e);
            return new java.util.ArrayList<>();
        }
    }

    // ===================== Helper Methods =====================

    /**
     * Resolve the correct pricing strategy for the given check-in date.
     * Delegates to SeasonalPricingService if available; falls back to StandardRateStrategy.
     * This is where the Strategy pattern comes alive — the strategy is chosen at runtime
     * based on admin-configured seasons stored in the database.
     *
     * @param checkInDate the reservation check-in date
     * @return the appropriate IPricingStrategy
     */
    private IPricingStrategy resolveStrategyForDate(LocalDate checkInDate) {
        if (seasonalPricingService != null) {
            IPricingStrategy strategy = seasonalPricingService.resolveStrategy(checkInDate);
            logger.info("Resolved pricing strategy for {}: {}", checkInDate, strategy.getStrategyName());
            return strategy;
        }
        logger.debug("No SeasonalPricingService configured — using StandardRateStrategy");
        return new StandardRateStrategy();
    }

    /**
     * Resolve an existing guest from the DB by NIC, or create a new guest record.
     * This ensures the Guest entity always has a valid DB id for foreign-key references.
     */
    private Guest resolveOrCreateGuest(GuestDTO guestDTO) throws Exception {
        // If the servlet already resolved the guest from the session, use that ID directly
        if (guestDTO.getId() > 0) {
            java.util.Optional<Guest> byId = guestRepository.findById(guestDTO.getId());
            if (byId.isPresent()) {
                logger.info("Using session-resolved guest: id={}, NIC={}", byId.get().getId(), byId.get().getNic());
                return byId.get();
            }
            logger.warn("Guest ID {} from session not found in DB, falling back to NIC lookup", guestDTO.getId());
        }

        // Try to find existing guest by NIC
        if (guestDTO.getNic() != null && !guestDTO.getNic().trim().isEmpty()) {
            java.util.Optional<Guest> existing = guestRepository.findByNic(guestDTO.getNic());
            if (existing.isPresent()) {
                logger.info("Found existing guest by NIC {}: id={}", guestDTO.getNic(), existing.get().getId());
                return existing.get();
            }
        }

        // No existing guest found — create a new one
        Guest newGuest = GuestMapper.toEntity(guestDTO);
        Guest saved = guestRepository.save(newGuest);
        if (saved == null || saved.getId() <= 0) {
            throw new Exception("Failed to create guest record for NIC: " + guestDTO.getNic());
        }
        logger.info("Created new guest record: id={}, NIC={}", saved.getId(), saved.getNic());
        return saved;
    }

    private void validateReservationInput(GuestDTO guestDTO, int roomId, LocalDate checkIn, LocalDate checkOut) throws Exception {
        if (guestDTO == null) {
            throw new IllegalArgumentException("Guest details required");
        }
        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid room ID");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates required");
        }
        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    private Room getRoomEntity(int roomId) throws RoomNotAvailableException {
        try {
            Room room = getRoomFromService(roomId);
            if (room == null) {
                throw new RoomNotAvailableException("Room not found", String.valueOf(roomId));
            }
            if (!room.isAvailable()) {
                throw new RoomNotAvailableException("Room is not available", room.getNumber());
            }
            return room;
        } catch (RoomNotAvailableException e) {
            throw e;
        } catch (Exception e) {
            throw new RoomNotAvailableException("Error retrieving room: " + e.getMessage());
        }
    }

    private Room getRoomFromService(int roomId) {
        RoomDTO dto = roomService.getRoomById(roomId);
        return dto != null ? RoomMapper.toEntity(dto) : null;
    }

    private ReservationDTO mapToDTO(Reservation reservation, Guest guest, Room room) {
        ReservationDTO dto = new ReservationDTO(
            reservation.getId(),
            reservation.getGuestId(),
            reservation.getRoomId(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getTotalAmount()
        );
        dto.setStatus(reservation.getStatus());
        dto.setReservationType(reservation.getReservationType());
        dto.setPaymentMethod(reservation.getPaymentMethod());
        dto.setGuest(GuestMapper.toDTO(guest));
        dto.setRoom(RoomMapper.toDTO(room));
        return dto;
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO(
            reservation.getId(),
            reservation.getGuestId(),
            reservation.getRoomId(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getTotalAmount()
        );
        dto.setStatus(reservation.getStatus());
        dto.setReservationType(reservation.getReservationType());
        dto.setPaymentMethod(reservation.getPaymentMethod());
        return dto;
    }
}

