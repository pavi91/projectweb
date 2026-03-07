package com.hotelreservation.entity;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for Room entity
 * Tests availability, status transitions, clean/dirty state, and constructors.
 */
public class RoomTest {

    private Room room;

    @Before
    public void setUp() {
        room = new Room("101", "SINGLE", 100.00);
    }

    // --- Constructor Tests ---

    @Test
    public void testDefaultConstructor() {
        Room r = new Room();
        assertNull(r.getNumber());
        assertNull(r.getType());
        assertEquals(0.0, r.getBasePrice(), 0.001);
    }

    @Test
    public void testThreeArgConstructorDefaults() {
        assertEquals("101", room.getNumber());
        assertEquals("SINGLE", room.getType());
        assertEquals(100.00, room.getBasePrice(), 0.001);
        assertEquals("AVAILABLE", room.getStatus());
        assertTrue(room.isClean());
    }

    @Test
    public void testFullConstructor() {
        Room r = new Room(1, "201", "DOUBLE", 175.00, "OCCUPIED", false);
        assertEquals(1, r.getId());
        assertEquals("201", r.getNumber());
        assertEquals("DOUBLE", r.getType());
        assertEquals(175.00, r.getBasePrice(), 0.001);
        assertEquals("OCCUPIED", r.getStatus());
        assertFalse(r.isClean());
    }

    // --- Availability Tests ---

    @Test
    public void testIsAvailableWhenAvailableAndClean() {
        assertTrue(room.isAvailable());
    }

    @Test
    public void testIsAvailableWhenOccupied() {
        room.updateStatus("OCCUPIED");
        assertFalse(room.isAvailable());
    }

    @Test
    public void testIsAvailableWhenReserved() {
        room.updateStatus("RESERVED");
        assertFalse(room.isAvailable());
    }

    @Test
    public void testIsAvailableWhenUnderMaintenance() {
        room.updateStatus("UNDER_MAINTENANCE");
        assertFalse(room.isAvailable());
    }

    @Test
    public void testIsAvailableWhenDirty() {
        room.markDirty();
        assertFalse(room.isAvailable());
    }

    @Test
    public void testIsAvailableWhenAvailableButDirty() {
        // Status is AVAILABLE but room is dirty — should NOT be available
        room.setStatus("AVAILABLE");
        room.markDirty();
        assertFalse(room.isAvailable());
    }

    // --- Status & Clean Tests ---

    @Test
    public void testUpdateStatus() {
        room.updateStatus("OCCUPIED");
        assertEquals("OCCUPIED", room.getStatus());
    }

    @Test
    public void testMarkDirty() {
        room.markDirty();
        assertFalse(room.isClean());
    }

    @Test
    public void testMarkClean() {
        room.markDirty();
        assertFalse(room.isClean());
        room.markClean();
        assertTrue(room.isClean());
    }

    @Test
    public void testMarkCleanUpdatesTimestamp() {
        long before = room.getUpdatedAt();
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignored */ }
        room.markClean();
        assertTrue(room.getUpdatedAt() >= before);
    }

    @Test
    public void testUpdateStatusUpdatesTimestamp() {
        long before = room.getUpdatedAt();
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignored */ }
        room.updateStatus("RESERVED");
        assertTrue(room.getUpdatedAt() >= before);
    }

    // --- Getter/Setter Tests ---

    @Test
    public void testSetAndGetId() {
        room.setId(5);
        assertEquals(5, room.getId());
    }

    @Test
    public void testSetAndGetBasePrice() {
        room.setBasePrice(250.00);
        assertEquals(250.00, room.getBasePrice(), 0.001);
    }

    @Test
    public void testToString() {
        String str = room.toString();
        assertTrue(str.contains("101"));
        assertTrue(str.contains("SINGLE"));
        assertTrue(str.contains("AVAILABLE"));
    }
}

