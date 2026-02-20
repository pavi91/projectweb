package com.hotelreservation.strategy;

/**
 * IPricingStrategy - Strategy pattern interface for dynamic pricing
 * Allows runtime switching between different pricing models
 */
public interface IPricingStrategy {

    /**
     * Calculate total price for a reservation
     * @param nights number of nights
     * @param baseRate base price per night
     * @return total price for the stay
     */
    double calculateTotal(int nights, double baseRate);

    /**
     * Get strategy name for logging/identification
     * @return strategy name
     */
    String getStrategyName();
}

