package com.hotelreservation.repository;

import com.hotelreservation.entity.SeasonalPricing;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SeasonalPricingRepository - defines contract for seasonal pricing data access
 */
public interface SeasonalPricingRepository {

    /**
     * Find a seasonal pricing entry by ID
     * @param id the entry ID
     * @return Optional containing the entry if found
     */
    Optional<SeasonalPricing> findById(int id);

    /**
     * Find all seasonal pricing entries
     * @return list of all entries
     */
    List<SeasonalPricing> findAll();

    /**
     * Find all active seasonal pricing entries
     * @return list of active entries
     */
    List<SeasonalPricing> findActive();

    /**
     * Find the active season that applies to a given check-in date
     * @param checkInDate the check-in date to match
     * @return Optional containing the matching season if found
     */
    Optional<SeasonalPricing> findByDate(LocalDate checkInDate);

    /**
     * Save a new seasonal pricing entry
     * @param pricing the entry to save
     * @return the saved entry with generated ID
     */
    SeasonalPricing save(SeasonalPricing pricing);

    /**
     * Update an existing seasonal pricing entry
     * @param pricing the entry to update
     */
    void update(SeasonalPricing pricing);

    /**
     * Delete a seasonal pricing entry by ID
     * @param id the entry ID to delete
     */
    void delete(int id);
}

