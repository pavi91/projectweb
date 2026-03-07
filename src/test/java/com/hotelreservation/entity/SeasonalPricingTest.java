package com.hotelreservation.entity;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for SeasonalPricing entity
 * Tests containsDate, overlapsWithStay, and constructors.
 */
public class SeasonalPricingTest {

    // --- Constructor Tests ---

    @Test
    public void testDefaultConstructor() {
        SeasonalPricing sp = new SeasonalPricing();
        assertNull(sp.getSeasonName());
        assertEquals(0.0, sp.getMultiplier(), 0.001);
    }

    @Test
    public void testFourArgConstructor() {
        SeasonalPricing sp = new SeasonalPricing("Peak Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.5);
        assertEquals("Peak Summer", sp.getSeasonName());
        assertEquals(LocalDate.of(2026, 7, 1), sp.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 31), sp.getEndDate());
        assertEquals(1.5, sp.getMultiplier(), 0.001);
        assertTrue(sp.isActive()); // default active
    }

    @Test
    public void testFullConstructor() {
        SeasonalPricing sp = new SeasonalPricing(1, "Christmas",
                LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 5), 1.8, false);
        assertEquals(1, sp.getId());
        assertEquals("Christmas", sp.getSeasonName());
        assertFalse(sp.isActive());
        assertEquals(1.8, sp.getMultiplier(), 0.001);
    }

    // --- containsDate Tests ---

    @Test
    public void testContainsDateWithinRange() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.containsDate(LocalDate.of(2026, 7, 15)));
    }

    @Test
    public void testContainsDateOnStartDate() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.containsDate(LocalDate.of(2026, 7, 1)));
    }

    @Test
    public void testContainsDateOnEndDate() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.containsDate(LocalDate.of(2026, 8, 31)));
    }

    @Test
    public void testContainsDateBeforeRange() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertFalse(sp.containsDate(LocalDate.of(2026, 6, 30)));
    }

    @Test
    public void testContainsDateAfterRange() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertFalse(sp.containsDate(LocalDate.of(2026, 9, 1)));
    }

    @Test
    public void testContainsDateWhenInactive() {
        SeasonalPricing sp = new SeasonalPricing(1, "Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3, false);
        // Date is within range but season is inactive — should return false
        assertFalse(sp.containsDate(LocalDate.of(2026, 7, 15)));
    }

    // --- overlapsWithStay Tests ---

    @Test
    public void testOverlapsWithStayFullyContained() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.overlapsWithStay(
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15)));
    }

    @Test
    public void testOverlapsWithStayPartialOverlapStart() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.overlapsWithStay(
                LocalDate.of(2026, 6, 25), LocalDate.of(2026, 7, 5)));
    }

    @Test
    public void testOverlapsWithStayPartialOverlapEnd() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertTrue(sp.overlapsWithStay(
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void testOverlapsWithStayNoOverlapBefore() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertFalse(sp.overlapsWithStay(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 30)));
    }

    @Test
    public void testOverlapsWithStayNoOverlapAfter() {
        SeasonalPricing sp = new SeasonalPricing("Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3);
        assertFalse(sp.overlapsWithStay(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 15)));
    }

    @Test
    public void testOverlapsWithStayWhenInactive() {
        SeasonalPricing sp = new SeasonalPricing(1, "Summer",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.3, false);
        assertFalse(sp.overlapsWithStay(
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15)));
    }

    // --- Setter Tests ---

    @Test
    public void testSetActive() {
        SeasonalPricing sp = new SeasonalPricing("Test",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 1.0);
        assertTrue(sp.isActive());
        sp.setActive(false);
        assertFalse(sp.isActive());
    }

    @Test
    public void testSetMultiplier() {
        SeasonalPricing sp = new SeasonalPricing("Test",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 1.0);
        sp.setMultiplier(2.0);
        assertEquals(2.0, sp.getMultiplier(), 0.001);
    }

    @Test
    public void testToString() {
        SeasonalPricing sp = new SeasonalPricing("Peak",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), 1.5);
        String str = sp.toString();
        assertTrue(str.contains("Peak"));
        assertTrue(str.contains("1.5"));
    }
}

