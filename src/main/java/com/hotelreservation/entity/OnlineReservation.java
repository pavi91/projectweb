package com.hotelreservation.entity;

import java.time.LocalDate;

/**
 * OnlineReservation - concrete reservation type for online bookings
 * Special behavior: tracks email confirmation status
 */
public class OnlineReservation extends Reservation {
    private boolean emailSent;

    public OnlineReservation() {
    }

    public OnlineReservation(String id, int guestId, int roomId, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        super(id, guestId, roomId, checkInDate, checkOutDate, totalAmount);
        this.emailSent = false;
        this.setPaymentMethod("ONLINE_GATEWAY");
    }

    @Override
    public String getReservationType() {
        return "ONLINE";
    }

    /**
     * Mark that confirmation email has been sent
     */
    public void markEmailSent() {
        this.emailSent = true;
    }

    /**
     * Check if confirmation email was sent
     * @return true if email sent, false otherwise
     */
    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    @Override
    public String toString() {
        return "OnlineReservation{" +
                "id='" + getId() + '\'' +
                ", guestId=" + getGuestId() +
                ", roomId=" + getRoomId() +
                ", status='" + getStatus() + '\'' +
                ", emailSent=" + emailSent +
                ", total=" + getTotalAmount() +
                '}';
    }
}

