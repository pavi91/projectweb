package com.hotelreservation.entity;

import java.time.LocalDate;

/**
 * SeasonalPricing entity - represents a pricing season with a date range and multiplier.
 * Used by the Strategy pattern to determine whether SeasonalRateStrategy should be applied.
 */
public class SeasonalPricing {
    private int id;
    private String seasonName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double multiplier;
    private boolean active;

    public SeasonalPricing() {}

    public SeasonalPricing(int id, String seasonName, LocalDate startDate, LocalDate endDate, double multiplier, boolean active) {
        this.id = id;
        this.seasonName = seasonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiplier = multiplier;
        this.active = active;
    }

    public SeasonalPricing(String seasonName, LocalDate startDate, LocalDate endDate, double multiplier) {
        this.seasonName = seasonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiplier = multiplier;
        this.active = true;
    }

    /**
     * Check if a given date falls within this season's date range
     * @param date the date to check
     * @return true if the date is within this season
     */
    public boolean containsDate(LocalDate date) {
        return active && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Check if a reservation's date range overlaps with this season
     * @param checkIn reservation check-in date
     * @param checkOut reservation check-out date
     * @return true if any part of the stay overlaps with this season
     */
    public boolean overlapsWithStay(LocalDate checkIn, LocalDate checkOut) {
        return active && !checkOut.isBefore(startDate) && !checkIn.isAfter(endDate);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSeasonName() { return seasonName; }
    public void setSeasonName(String seasonName) { this.seasonName = seasonName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "SeasonalPricing{" +
                "id=" + id +
                ", seasonName='" + seasonName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", multiplier=" + multiplier +
                ", active=" + active +
                '}';
    }
}

