package com.hotelreservation.service.impl;

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
import com.hotelreservation.repository.ReservationRepository;
import com.hotelreservation.repository.RoomRepository;
import com.hotelreservation.service.PaymentService;
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

    public BookingService(
            OnlineResService onlineResService,
            WalkInResService walkInResService,
            RoomService roomService,
            PaymentService paymentService,
            ReservationRepository reservationRepository) {
        this.onlineResService = onlineResService;
        this.walkInResService = walkInResService;
        this.roomService = roomService;
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
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

            // Map DTOs to entities
            Guest guest = GuestMapper.toEntity(guestDTO);

            // Set dates for reservation service
            onlineResService.setReservationDates(checkIn, checkOut);
            onlineResService.setPricingStrategy(new StandardRateStrategy());

            // Process booking (creates and saves reservation)
            Reservation reservation = onlineResService.processBooking(guest, room);

            // Process payment
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

            // Map DTOs to entities
            Guest guest = GuestMapper.toEntity(guestDTO);

            // Set dates for reservation service
            walkInResService.setReservationDates(checkIn, checkOut);
            walkInResService.setPricingStrategy(new StandardRateStrategy());

            // Process booking (creates and saves reservation)
            Reservation reservation = walkInResService.processBooking(guest, room);

            // Process payment via POS
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
        logger.info("Checking in guest for reservation: {}", reservationId);

        try {
            Reservation reservation = reservationRepository.findById(reservationId)
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

            // Update room status to AVAILABLE
            roomService.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");
            roomService.markRoomClean(reservation.getRoomId());

            logger.info("Check-out successful for reservation: {}", reservationId);

            // Return reservation details with bill
            Room room = getRoomEntity(reservation.getRoomId());
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

            // Process refund
            try {
                paymentService.processRefund(reservation.getTotalAmount(), "Reservation cancelled");
            } catch (PaymentException e) {
                logger.warn("Error processing refund for cancellation", e);
            }

            logger.info("Reservation cancelled successfully: {}", reservationId);
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

    // ===================== Helper Methods =====================

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

