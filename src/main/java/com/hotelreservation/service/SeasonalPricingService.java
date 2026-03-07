package com.hotelreservation.service;

import com.hotelreservation.entity.SeasonalPricing;
import com.hotelreservation.strategy.IPricingStrategy;

import java.time.LocalDate;
import java.util.List;

/**
 * SeasonalPricingService - defines contract for seasonal pricing operations
 * Bridges the database-stored season config with the Strategy pattern
 */
public interface SeasonalPricingService {

    /**
     * Get all seasonal pricing entries
     * @return list of all entries
     */
    List<SeasonalPricing> getAllSeasons();

    /**
     * Get only active seasonal pricing entries
     * @return list of active entries
     */
    List<SeasonalPricing> getActiveSeasons();

    /**
     * Get a seasonal pricing entry by ID
     * @param id the entry ID
     * @return the entry, or null if not found
     */
    SeasonalPricing getSeasonById(int id);

    /**
     * Create a new seasonal pricing entry
     * @param pricing the entry to create
     * @return the saved entry with generated ID
     */
    SeasonalPricing createSeason(SeasonalPricing pricing);

    /**
     * Update an existing seasonal pricing entry
     * @param pricing the entry to update
     */
    void updateSeason(SeasonalPricing pricing);

    /**
     * Delete a seasonal pricing entry
     * @param id the entry ID to delete
     */
    void deleteSeason(int id);

    /**
     * Resolve the correct pricing strategy based on the check-in date.
     * If the check-in date falls within an active season, returns SeasonalRateStrategy
     * with the appropriate multiplier. Otherwise, returns StandardRateStrategy.
     *
     * @param checkInDate the reservation check-in date
     * @return the appropriate IPricingStrategy
     */
    IPricingStrategy resolveStrategy(LocalDate checkInDate);
}

