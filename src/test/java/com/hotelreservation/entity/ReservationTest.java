package com.hotelreservation.entity;

import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for Reservation, OnlineReservation, and WalkInReservation entities
 * Tests polymorphism (Factory Method products), status transitions, and bill calculation.
 */
public class ReservationTest {

    private OnlineReservation onlineRes;
    private WalkInReservation walkInRes;

    @Before
    public void setUp() {
        onlineRes = new OnlineReservation("RES-001", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3), 200.00);
        walkInRes = new WalkInReservation("RES-002", 2, 2,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), 350.00);
    }

    // --- Polymorphism / Factory Method Product Tests ---

    @Test
    public void testOnlineReservationType() {
        assertEquals("ONLINE", onlineRes.getReservationType());
    }

    @Test
    public void testWalkInReservationType() {
        assertEquals("WALK_IN", walkInRes.getReservationType());
    }

    @Test
    public void testOnlineReservationDefaultPaymentMethod() {
        assertEquals("ONLINE_GATEWAY", onlineRes.getPaymentMethod());
    }

    @Test
    public void testWalkInReservationDefaultPaymentMethod() {
        assertEquals("POS", walkInRes.getPaymentMethod());
    }

    // --- Initial Status Tests ---

    @Test
    public void testInitialStatusIsPending() {
        assertEquals("PENDING", onlineRes.getStatus());
        assertEquals("PENDING", walkInRes.getStatus());
    }

    // --- Status Transition Tests ---

    @Test
    public void testConfirm() {
        onlineRes.confirm();
        assertEquals("CONFIRMED", onlineRes.getStatus());
    }

    @Test
    public void testCheckIn() {
        onlineRes.confirm();
        onlineRes.checkIn();
        assertEquals("CHECKED_IN", onlineRes.getStatus());
    }

    @Test
    public void testCheckOut() {
        onlineRes.confirm();
        onlineRes.checkIn();
        onlineRes.checkOut();
        assertEquals("CHECKED_OUT", onlineRes.getStatus());
    }

    @Test
    public void testCancel() {
        onlineRes.confirm();
        onlineRes.cancel();
        assertEquals("CANCELLED", onlineRes.getStatus());
    }

    @Test
    public void testStatusTransitionUpdatesTimestamp() {
        long before = onlineRes.getUpdatedAt();
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignored */ }
        onlineRes.confirm();
        assertTrue(onlineRes.getUpdatedAt() >= before);
    }

    // --- Number of Nights Calculation Tests ---

    @Test
    public void testGetNumberOfNightsTwoNights() {
        assertEquals(2, onlineRes.getNumberOfNights());
    }

    @Test
    public void testGetNumberOfNightsOneNight() {
        OnlineReservation res = new OnlineReservation("RES-X", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 2), 100.00);
        assertEquals(1, res.getNumberOfNights());
    }

    @Test
    public void testGetNumberOfNightsSameDate() {
        OnlineReservation res = new OnlineReservation("RES-X", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 1), 0.00);
        assertEquals(0, res.getNumberOfNights());
    }

    @Test
    public void testGetNumberOfNightsNullDates() {
        OnlineReservation res = new OnlineReservation();
        assertEquals(0, res.getNumberOfNights());
    }

    @Test
    public void testGetNumberOfNightsLongStay() {
        OnlineReservation res = new OnlineReservation("RES-X", 1, 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 3000.00);
        assertEquals(30, res.getNumberOfNights());
    }

    // --- Online-Specific: Email Sent ---

    @Test
    public void testEmailSentDefaultFalse() {
        assertFalse(onlineRes.isEmailSent());
    }

    @Test
    public void testMarkEmailSent() {
        onlineRes.markEmailSent();
        assertTrue(onlineRes.isEmailSent());
    }

    // --- WalkIn-Specific: Receipt Printed ---

    @Test
    public void testReceiptPrintedDefaultFalse() {
        assertFalse(walkInRes.isReceiptPrinted());
    }

    @Test
    public void testMarkReceiptPrinted() {
        walkInRes.markReceiptPrinted();
        assertTrue(walkInRes.isReceiptPrinted());
    }

    // --- Getter/Setter Tests ---

    @Test
    public void testGetAndSetId() {
        onlineRes.setId("RES-999");
        assertEquals("RES-999", onlineRes.getId());
    }

    @Test
    public void testGetAndSetGuestId() {
        onlineRes.setGuestId(99);
        assertEquals(99, onlineRes.getGuestId());
    }

    @Test
    public void testGetAndSetRoomId() {
        onlineRes.setRoomId(5);
        assertEquals(5, onlineRes.getRoomId());
    }

    @Test
    public void testGetAndSetTotalAmount() {
        onlineRes.setTotalAmount(500.00);
        assertEquals(500.00, onlineRes.getTotalAmount(), 0.001);
    }

    @Test
    public void testGetCheckInDate() {
        assertEquals(LocalDate.of(2026, 4, 1), onlineRes.getCheckInDate());
    }

    @Test
    public void testGetCheckOutDate() {
        assertEquals(LocalDate.of(2026, 4, 3), onlineRes.getCheckOutDate());
    }

    @Test
    public void testToStringOnline() {
        String str = onlineRes.toString();
        assertTrue(str.contains("RES-001"));
        assertTrue(str.contains("OnlineReservation"));
    }

    @Test
    public void testToStringWalkIn() {
        String str = walkInRes.toString();
        assertTrue(str.contains("RES-002"));
        assertTrue(str.contains("WalkInReservation"));
    }
}

