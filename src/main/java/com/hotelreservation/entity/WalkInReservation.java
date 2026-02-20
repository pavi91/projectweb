package com.hotelreservation.entity;

import java.time.LocalDate;

/**
 * WalkInReservation - concrete reservation type for walk-in bookings
 * Special behavior: tracks receipt printing status
 */
public class WalkInReservation extends Reservation {
    private boolean receiptPrinted;

    public WalkInReservation() {
    }

    public WalkInReservation(String id, int guestId, int roomId, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        super(id, guestId, roomId, checkInDate, checkOutDate, totalAmount);
        this.receiptPrinted = false;
        this.setPaymentMethod("POS");
    }

    @Override
    public String getReservationType() {
        return "WALK_IN";
    }

    /**
     * Mark that receipt has been printed
     */
    public void markReceiptPrinted() {
        this.receiptPrinted = true;
    }

    /**
     * Check if receipt was printed
     * @return true if receipt printed, false otherwise
     */
    public boolean isReceiptPrinted() {
        return receiptPrinted;
    }

    public void setReceiptPrinted(boolean receiptPrinted) {
        this.receiptPrinted = receiptPrinted;
    }

    @Override
    public String toString() {
        return "WalkInReservation{" +
                "id='" + getId() + '\'' +
                ", guestId=" + getGuestId() +
                ", roomId=" + getRoomId() +
                ", status='" + getStatus() + '\'' +
                ", receiptPrinted=" + receiptPrinted +
                ", total=" + getTotalAmount() +
                '}';
    }
}

