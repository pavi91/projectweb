package com.hotelreservation.controller;

import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.service.impl.BookingService;
import com.hotelreservation.service.impl.RoomServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * ReservationController - handles guest reservation requests
 * Used by Guest actors for online room search, reservation, and cancellation
 */
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private BookingService bookingService;
    private RoomServiceImpl roomService;

    public ReservationController(BookingService bookingService, RoomServiceImpl roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    /**
     * Search for available rooms
     * @param checkInDate check-in date (yyyy-MM-dd format)
     * @param checkOutDate check-out date (yyyy-MM-dd format)
     * @return ControllerResult with list of available rooms
     */
    public ControllerResult<List<RoomDTO>> searchRooms(String checkInDate, String checkOutDate) {
        try {
            if (checkInDate == null || checkOutDate == null) {
                return new ControllerResult<>(false, "Check-in and check-out dates required", null);
            }

            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            List<RoomDTO> availableRooms = roomService.getAvailableRooms(checkIn, checkOut);
            logger.info("Room search performed: {} rooms available for {} to {}",
                    availableRooms.size(), checkInDate, checkOutDate);

            return new ControllerResult<>(true, "Rooms found", availableRooms);
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format in room search", e);
            return new ControllerResult<>(false, "Invalid date format. Use yyyy-MM-dd", null);
        } catch (Exception e) {
            logger.error("Error searching rooms", e);
            return new ControllerResult<>(false, "Error searching rooms: " + e.getMessage(), null);
        }
    }

    /**
     * Make an online reservation
     * @param guestDTO guest details
     * @param roomId room ID
     * @param checkInDate check-in date (yyyy-MM-dd format)
     * @param checkOutDate check-out date (yyyy-MM-dd format)
     * @return ControllerResult with reservation details
     */
    public ControllerResult<ReservationDTO> makeReservation(GuestDTO guestDTO, int roomId, String checkInDate, String checkOutDate) {
        try {
            if (guestDTO == null) {
                return new ControllerResult<>(false, "Guest details required", null);
            }

            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            ReservationDTO reservation = bookingService.makeOnlineReservation(guestDTO, roomId, checkIn, checkOut);
            logger.info("Online reservation created: {}", reservation.getId());

            return new ControllerResult<>(true, "Reservation created successfully", reservation);
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format in reservation", e);
            return new ControllerResult<>(false, "Invalid date format. Use yyyy-MM-dd", null);
        } catch (Exception e) {
            logger.error("Error creating reservation", e);
            return new ControllerResult<>(false, "Reservation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Cancel a reservation
     * @param reservationId the reservation to cancel
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> cancelReservation(String reservationId) {
        try {
            if (reservationId == null || reservationId.trim().isEmpty()) {
                return new ControllerResult<>(false, "Reservation ID required", false);
            }

            boolean success = bookingService.cancelReservation(reservationId);
            if (success) {
                logger.info("Reservation cancelled: {}", reservationId);
                return new ControllerResult<>(true, "Reservation cancelled successfully", true);
            } else {
                return new ControllerResult<>(false, "Reservation cannot be cancelled", false);
            }
        } catch (Exception e) {
            logger.error("Error cancelling reservation", e);
            return new ControllerResult<>(false, "Cancellation failed: " + e.getMessage(), false);
        }
    }

    /**
     * Get reservation details
     * @param reservationId the reservation ID
     * @return ControllerResult with reservation details
     */
    public ControllerResult<ReservationDTO> getReservation(String reservationId) {
        try {
            if (reservationId == null || reservationId.trim().isEmpty()) {
                return new ControllerResult<>(false, "Reservation ID required", null);
            }

            ReservationDTO reservation = bookingService.getReservation(reservationId);
            if (reservation != null) {
                return new ControllerResult<>(true, "Reservation found", reservation);
            } else {
                return new ControllerResult<>(false, "Reservation not found", null);
            }
        } catch (Exception e) {
            logger.error("Error retrieving reservation", e);
            return new ControllerResult<>(false, "Error retrieving reservation: " + e.getMessage(), null);
        }
    }

    /**
     * Generic result wrapper for controller responses
     */
    public static class ControllerResult<T> {
        private boolean success;
        private String message;
        private T data;

        public ControllerResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }

        @Override
        public String toString() {
            return "ControllerResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

