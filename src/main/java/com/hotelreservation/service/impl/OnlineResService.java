package com.hotelreservation.service.impl;

import com.hotelreservation.entity.*;
import com.hotelreservation.repository.ReservationRepository;
import com.hotelreservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.UUID;

/**
 * OnlineResService - Concrete implementation of ReservationService for online reservations
 * Implements Factory Method pattern to create OnlineReservation instances
 * Special behavior: sends confirmation emails
 */
public class OnlineResService extends ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(OnlineResService.class);
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public OnlineResService(ReservationRepository reservationRepository) {
        super(reservationRepository);
    }

    /**
     * Factory Method implementation - creates OnlineReservation
     * @param guest the guest making the reservation
     * @param room the room being reserved
     * @param totalAmount the total reservation amount
     * @return OnlineReservation instance
     */
    @Override
    protected Reservation createReservation(Guest guest, Room room, double totalAmount) {
        String reservationId = "ONL_" + UUID.randomUUID().toString().substring(0, 8);

        OnlineReservation reservation = new OnlineReservation(
            reservationId,
            guest.getId(),
            room.getId(),
            checkInDate,
            checkOutDate,
            totalAmount
        );

        logger.info("Created online reservation: {}", reservationId);
        return reservation;
    }

    /**
     * Set check-in and check-out dates for the reservation
     * @param checkIn check-in date
     * @param checkOut check-out date
     */
    public void setReservationDates(LocalDate checkIn, LocalDate checkOut) {
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
    }

    /**
     * Send confirmation email to guest
     * @param guest the guest to send email to
     * @return true if email sent successfully
     */
    public boolean sendConfirmationEmail(Guest guest, Reservation reservation) {
        try {
            if (guest.getEmail() == null || guest.getEmail().isEmpty()) {
                logger.warn("No email address for guest: {}", guest.getName());
                return false;
            }

            // In production, this would integrate with actual email service
            logger.info("Sending confirmation email to: {}", guest.getEmail());
            logger.info("Reservation ID: {}, Amount: {}", reservation.getId(), reservation.getTotalAmount());

            // Mark email as sent in reservation if it's an OnlineReservation
            if (reservation instanceof OnlineReservation) {
                ((OnlineReservation) reservation).markEmailSent();
                reservationRepository.update(reservation);
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to send confirmation email", e);
            return false;
        }
    }

    /**
     * Send payment receipt email
     * @param guest the guest to send receipt to
     * @param reservation the reservation
     * @return true if email sent successfully
     */
    public boolean sendPaymentReceipt(Guest guest, Reservation reservation) {
        try {
            if (guest.getEmail() == null || guest.getEmail().isEmpty()) {
                logger.warn("No email address for guest: {}", guest.getName());
                return false;
            }

            logger.info("Sending payment receipt to: {}", guest.getEmail());
            logger.info("Amount charged: {}", reservation.getTotalAmount());

            return true;
        } catch (Exception e) {
            logger.error("Failed to send payment receipt", e);
            return false;
        }
    }

    /**
     * Send cancellation email
     * @param guest the guest to notify
     * @param reservation the cancelled reservation
     * @param refundAmount the refund amount
     * @return true if email sent successfully
     */
    public boolean sendCancellationEmail(Guest guest, Reservation reservation, double refundAmount) {
        try {
            if (guest.getEmail() == null || guest.getEmail().isEmpty()) {
                logger.warn("No email address for guest: {}", guest.getName());
                return false;
            }

            logger.info("Sending cancellation email to: {}", guest.getEmail());
            logger.info("Reservation ID: {}, Refund amount: {}", reservation.getId(), refundAmount);

            return true;
        } catch (Exception e) {
            logger.error("Failed to send cancellation email", e);
            return false;
        }
    }

    @Override
    protected int calculateNights(Guest guest) {
        if (checkInDate != null && checkOutDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 1;
    }

    @Override
    public String getReservationType() {
        return "ONLINE";
    }

    @Override
    public String toString() {
        return "OnlineResService{" +
                "type=ONLINE" +
                ", pricingStrategy=" + (pricingStrategy != null ? pricingStrategy.getStrategyName() : "NONE") +
                '}';
    }
}

