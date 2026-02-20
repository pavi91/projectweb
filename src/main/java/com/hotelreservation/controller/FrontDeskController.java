package com.hotelreservation.controller;

import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.service.impl.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * FrontDeskController - handles receptionist check-in/check-out operations
 * Used by Receptionist actors for walk-in reservations, check-in, and check-out
 */
public class FrontDeskController {
    private static final Logger logger = LoggerFactory.getLogger(FrontDeskController.class);
    private BookingService bookingService;

    public FrontDeskController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Make a walk-in reservation
     * @param guestDTO guest details
     * @param roomId room ID
     * @param checkInDate check-in date (yyyy-MM-dd format)
     * @param checkOutDate check-out date (yyyy-MM-dd format)
     * @return ControllerResult with reservation details
     */
    public ControllerResult<ReservationDTO> makeWalkInReservation(GuestDTO guestDTO, int roomId, String checkInDate, String checkOutDate) {
        try {
            if (guestDTO == null) {
                return new ControllerResult<>(false, "Guest details required", null);
            }

            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            ReservationDTO reservation = bookingService.makeWalkInReservation(guestDTO, roomId, checkIn, checkOut);
            logger.info("Walk-in reservation created: {}", reservation.getId());

            return new ControllerResult<>(true, "Walk-in reservation created successfully", reservation);
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format in walk-in reservation", e);
            return new ControllerResult<>(false, "Invalid date format. Use yyyy-MM-dd", null);
        } catch (Exception e) {
            logger.error("Error creating walk-in reservation", e);
            return new ControllerResult<>(false, "Reservation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Check in a guest
     * @param reservationId the reservation ID
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> checkIn(String reservationId) {
        try {
            if (reservationId == null || reservationId.trim().isEmpty()) {
                return new ControllerResult<>(false, "Reservation ID required", false);
            }

            bookingService.checkIn(reservationId);
            logger.info("Guest checked in for reservation: {}", reservationId);

            return new ControllerResult<>(true, "Check-in successful", true);
        } catch (Exception e) {
            logger.error("Error during check-in", e);
            return new ControllerResult<>(false, "Check-in failed: " + e.getMessage(), false);
        }
    }

    /**
     * Check out a guest and generate final bill
     * @param reservationId the reservation ID
     * @return ControllerResult with bill/checkout details
     */
    public ControllerResult<ReservationDTO> checkOut(String reservationId) {
        try {
            if (reservationId == null || reservationId.trim().isEmpty()) {
                return new ControllerResult<>(false, "Reservation ID required", null);
            }

            ReservationDTO reservation = bookingService.checkOut(reservationId);
            logger.info("Guest checked out for reservation: {}", reservationId);

            return new ControllerResult<>(true, "Check-out successful", reservation);
        } catch (Exception e) {
            logger.error("Error during check-out", e);
            return new ControllerResult<>(false, "Check-out failed: " + e.getMessage(), null);
        }
    }

    /**
     * Get reservation details (for receptionist reference)
     * @param reservationId the reservation ID
     * @return ControllerResult with reservation details
     */
    public ControllerResult<ReservationDTO> getReservationDetails(String reservationId) {
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
            logger.error("Error retrieving reservation details", e);
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

