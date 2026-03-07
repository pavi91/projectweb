package com.hotelreservation.service.impl;

import com.hotelreservation.entity.SeasonalPricing;
import com.hotelreservation.repository.SeasonalPricingRepository;
import com.hotelreservation.service.SeasonalPricingService;
import com.hotelreservation.strategy.IPricingStrategy;
import com.hotelreservation.strategy.SeasonalRateStrategy;
import com.hotelreservation.strategy.StandardRateStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SeasonalPricingServiceImpl - concrete implementation of SeasonalPricingService
 * Manages seasonal pricing CRUD and resolves the correct IPricingStrategy at runtime
 */
public class SeasonalPricingServiceImpl implements SeasonalPricingService {
    private static final Logger logger = LoggerFactory.getLogger(SeasonalPricingServiceImpl.class);

    private final SeasonalPricingRepository repository;

    public SeasonalPricingServiceImpl(SeasonalPricingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SeasonalPricing> getAllSeasons() {
        return repository.findAll();
    }

    @Override
    public List<SeasonalPricing> getActiveSeasons() {
        return repository.findActive();
    }

    @Override
    public SeasonalPricing getSeasonById(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public SeasonalPricing createSeason(SeasonalPricing pricing) {
        logger.info("Creating seasonal pricing: {} ({}x) from {} to {}",
                pricing.getSeasonName(), pricing.getMultiplier(), pricing.getStartDate(), pricing.getEndDate());
        return repository.save(pricing);
    }

    @Override
    public void updateSeason(SeasonalPricing pricing) {
        logger.info("Updating seasonal pricing id={}: {} ({}x)", pricing.getId(), pricing.getSeasonName(), pricing.getMultiplier());
        repository.update(pricing);
    }

    @Override
    public void deleteSeason(int id) {
        logger.info("Deleting seasonal pricing id={}", id);
        repository.delete(id);
    }

    /**
     * Resolves the correct pricing strategy for a given check-in date.
     * Queries the DB for an active season matching the date:
     *   - If found → SeasonalRateStrategy with the season's multiplier
     *   - If not found → StandardRateStrategy (1x base rate)
     */
    @Override
    public IPricingStrategy resolveStrategy(LocalDate checkInDate) {
        Optional<SeasonalPricing> season = repository.findByDate(checkInDate);

        if (season.isPresent()) {
            SeasonalPricing sp = season.get();
            logger.info("Seasonal pricing active for {}: '{}' with multiplier {}x",
                    checkInDate, sp.getSeasonName(), sp.getMultiplier());
            return new SeasonalRateStrategy(sp.getMultiplier());
        }

        logger.debug("No seasonal pricing for {} — using standard rate", checkInDate);
        return new StandardRateStrategy();
    }
}

