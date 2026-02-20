package com.hotelreservation.entity;

import java.time.LocalDate;

/**
 * Abstract Reservation entity - base class for all reservation types
 * Uses polymorphism to support different reservation behaviors
 */
public abstract class Reservation {
    private String id;
    private int guestId;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    private String paymentMethod; // POS, ONLINE_GATEWAY
    private long createdAt;
    private long updatedAt;

    protected Reservation() {
    }

    protected Reservation(String id, int guestId, int roomId, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        this.id = id;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Abstract method for reservation type identification
     * @return the type of reservation (ONLINE or WALK_IN)
     */
    public abstract String getReservationType();

    /**
     * Confirm the reservation
     */
    public void confirm() {
        this.status = "CONFIRMED";
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Mark reservation as checked in
     */
    public void checkIn() {
        this.status = "CHECKED_IN";
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Mark reservation as checked out
     */
    public void checkOut() {
        this.status = "CHECKED_OUT";
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Cancel the reservation
     */
    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Calculate number of nights
     * @return number of nights between check-in and check-out
     */
    public int getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", guestId=" + guestId +
                ", roomId=" + roomId +
                ", status='" + status + '\'' +
                ", type='" + getReservationType() + '\'' +
                ", total=" + totalAmount +
                '}';
    }
}

