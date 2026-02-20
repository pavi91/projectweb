package com.hotelreservation.strategy;

/**
 * StandardRateStrategy - Concrete pricing strategy for standard rates
 * Simple calculation: nights * baseRate
 */
public class StandardRateStrategy implements IPricingStrategy {

    @Override
    public double calculateTotal(int nights, double baseRate) {
        return nights * baseRate;
    }

    @Override
    public String getStrategyName() {
        return "STANDARD_RATE";
    }

    @Override
    public String toString() {
        return "StandardRateStrategy{}";
    }
}

