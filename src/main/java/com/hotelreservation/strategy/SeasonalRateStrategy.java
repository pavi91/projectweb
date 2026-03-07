package com.hotelreservation.strategy;

/**
 * SeasonalRateStrategy - Concrete pricing strategy for seasonal rates
 * Applies a multiplier to base rate: nights * baseRate * seasonMultiplier
 * Used for peak season pricing (e.g., holidays, weekends)
 */
public class SeasonalRateStrategy implements IPricingStrategy {
    private double seasonMultiplier;

    public SeasonalRateStrategy(double seasonMultiplier) {
        this.seasonMultiplier = seasonMultiplier;
    }

    @Override
    public double calculateTotal(int nights, double baseRate) {
        return nights * baseRate * seasonMultiplier;
    }

    @Override
    public String getStrategyName() {
        return "SEASONAL_RATE";
    }


    public void setSeasonMultiplier(double multiplier) {
        this.seasonMultiplier = multiplier;
    }

    public double getSeasonMultiplier() {
        return seasonMultiplier;
    }

    @Override
    public String toString() {
        return "SeasonalRateStrategy{" +
                "multiplier=" + seasonMultiplier +
                '}';
    }
}
