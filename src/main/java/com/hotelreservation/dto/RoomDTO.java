package com.hotelreservation.dto;

/**
 * RoomDTO - Data Transfer Object for Room
 */
public class RoomDTO {
    private int id;
    private String number;
    private String type;
    private double basePrice;
    private String status;
    private boolean isClean;

    public RoomDTO() {
    }

    public RoomDTO(String number, String type, double basePrice) {
        this.number = number;
        this.type = type;
        this.basePrice = basePrice;
        this.status = "AVAILABLE";
        this.isClean = true;
    }

    public RoomDTO(int id, String number, String type, double basePrice, String status, boolean isClean) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.basePrice = basePrice;
        this.status = status;
        this.isClean = isClean;
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

    @Override
    public String toString() {
        return "RoomDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", basePrice=" + basePrice +
                ", status='" + status + '\'' +
                '}';
    }
}

