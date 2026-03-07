package com.hotelreservation.strategy;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Strategy Pattern — IPricingStrategy implementations
 * Tests StandardRateStrategy and SeasonalRateStrategy pricing calculations.
 */
public class PricingStrategyTest {

    // =============================================
    //  StandardRateStrategy Tests
    // =============================================

    @Test
    public void testStandardRateCalculation() {
        IPricingStrategy strategy = new StandardRateStrategy();
        double total = strategy.calculateTotal(3, 100.00);
        assertEquals(300.00, total, 0.001);
    }

    @Test
    public void testStandardRateOneNight() {
        IPricingStrategy strategy = new StandardRateStrategy();
        assertEquals(100.00, strategy.calculateTotal(1, 100.00), 0.001);
    }

    @Test
    public void testStandardRateZeroNights() {
        IPricingStrategy strategy = new StandardRateStrategy();
        assertEquals(0.00, strategy.calculateTotal(0, 100.00), 0.001);
    }

    @Test
    public void testStandardRateWithDoubleRoom() {
        IPricingStrategy strategy = new StandardRateStrategy();
        assertEquals(350.00, strategy.calculateTotal(2, 175.00), 0.001);
    }

    @Test
    public void testStandardRateWithSuiteRoom() {
        IPricingStrategy strategy = new StandardRateStrategy();
        assertEquals(900.00, strategy.calculateTotal(3, 300.00), 0.001);
    }

    @Test
    public void testStandardRateStrategyName() {
        IPricingStrategy strategy = new StandardRateStrategy();
        assertEquals("STANDARD_RATE", strategy.getStrategyName());
    }

    @Test
    public void testStandardRateToString() {
        StandardRateStrategy strategy = new StandardRateStrategy();
        assertNotNull(strategy.toString());
    }

    // =============================================
    //  SeasonalRateStrategy Tests
    // =============================================

    @Test
    public void testSeasonalRateWithPeakMultiplier() {
        IPricingStrategy strategy = new SeasonalRateStrategy(1.5);
        // 3 nights × $100 × 1.5 = $450
        assertEquals(450.00, strategy.calculateTotal(3, 100.00), 0.001);
    }

    @Test
    public void testSeasonalRateWithDiscountMultiplier() {
        IPricingStrategy strategy = new SeasonalRateStrategy(0.85);
        // 2 nights × $200 × 0.85 = $340
        assertEquals(340.00, strategy.calculateTotal(2, 200.00), 0.001);
    }

    @Test
    public void testSeasonalRateWithDoubleMultiplier() {
        IPricingStrategy strategy = new SeasonalRateStrategy(2.0);
        // 1 night × $100 × 2.0 = $200
        assertEquals(200.00, strategy.calculateTotal(1, 100.00), 0.001);
    }

    @Test
    public void testSeasonalRateWithOneMultiplier() {
        // Multiplier of 1.0 should behave like standard rate
        IPricingStrategy strategy = new SeasonalRateStrategy(1.0);
        assertEquals(300.00, strategy.calculateTotal(3, 100.00), 0.001);
    }

    @Test
    public void testSeasonalRateZeroNights() {
        IPricingStrategy strategy = new SeasonalRateStrategy(1.5);
        assertEquals(0.00, strategy.calculateTotal(0, 100.00), 0.001);
    }

    @Test
    public void testSeasonalRateStrategyName() {
        IPricingStrategy strategy = new SeasonalRateStrategy(1.5);
        assertEquals("SEASONAL_RATE", strategy.getStrategyName());
    }

    @Test
    public void testSeasonalRateGetMultiplier() {
        SeasonalRateStrategy strategy = new SeasonalRateStrategy(1.5);
        assertEquals(1.5, strategy.getSeasonMultiplier(), 0.001);
    }

    @Test
    public void testSeasonalRateSetMultiplier() {
        SeasonalRateStrategy strategy = new SeasonalRateStrategy(1.0);
        strategy.setSeasonMultiplier(1.8);
        assertEquals(1.8, strategy.getSeasonMultiplier(), 0.001);
        // Verify calculation uses new multiplier
        assertEquals(180.00, strategy.calculateTotal(1, 100.00), 0.001);
    }

    @Test
    public void testSeasonalRateToString() {
        SeasonalRateStrategy strategy = new SeasonalRateStrategy(1.5);
        String str = strategy.toString();
        assertTrue(str.contains("1.5"));
    }

    // =============================================
    //  Strategy Pattern Polymorphism Tests
    // =============================================

    @Test
    public void testStrategySwapAtRuntime() {
        IPricingStrategy strategy = new StandardRateStrategy();
        double standardTotal = strategy.calculateTotal(2, 100.00);
        assertEquals(200.00, standardTotal, 0.001);

        // Swap strategy at runtime — simulates seasonal pricing activation
        strategy = new SeasonalRateStrategy(1.5);
        double seasonalTotal = strategy.calculateTotal(2, 100.00);
        assertEquals(300.00, seasonalTotal, 0.001);
    }

    @Test
    public void testDifferentStrategiesDifferentResults() {
        IPricingStrategy standard = new StandardRateStrategy();
        IPricingStrategy seasonal = new SeasonalRateStrategy(1.3);

        double standardResult = standard.calculateTotal(5, 175.00);
        double seasonalResult = seasonal.calculateTotal(5, 175.00);

        assertEquals(875.00, standardResult, 0.001);
        assertEquals(1137.50, seasonalResult, 0.001);
        assertTrue(seasonalResult > standardResult);
    }
}

