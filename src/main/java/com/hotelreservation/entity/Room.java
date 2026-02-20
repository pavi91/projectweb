package com.hotelreservation.entity;

/**
 * Room entity - represents a hotel room
 */
public class Room {
    private int id;
    private String number;
    private String type; // SINGLE, DOUBLE, SUITE
    private double basePrice;
    private String status; // AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE
    private boolean isClean;
    private long createdAt;
    private long updatedAt;

    public Room() {
    }

    public Room(String number, String type, double basePrice) {
        this.number = number;
        this.type = type;
        this.basePrice = basePrice;
        this.status = "AVAILABLE";
        this.isClean = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Room(int id, String number, String type, double basePrice, String status, boolean isClean) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.basePrice = basePrice;
        this.status = status;
        this.isClean = isClean;
    }

    /**
     * Mark room as clean
     */
    public void markClean() {
        this.isClean = true;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Mark room as dirty (needs cleaning)
     */
    public void markDirty() {
        this.isClean = false;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Check if room is available for reservation
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return "AVAILABLE".equals(this.status);
    }

    /**
     * Update room status
     * @param status new status
     */
    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        isClean = clean;
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
        return "Room{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", basePrice=" + basePrice +
                ", status='" + status + '\'' +
                '}';
    }
}

