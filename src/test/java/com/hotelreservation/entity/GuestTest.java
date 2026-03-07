package com.hotelreservation.entity;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for Guest entity
 * Tests constructors, updateContact, and getters/setters.
 */
public class GuestTest {

    private Guest guest;

    @Before
    public void setUp() {
        guest = new Guest("John Doe", "200112345678", "0771234567");
    }

    // --- Constructor Tests ---

    @Test
    public void testDefaultConstructor() {
        Guest g = new Guest();
        assertNull(g.getName());
        assertNull(g.getNic());
        assertNull(g.getPhone());
        assertEquals(0, g.getId());
    }

    @Test
    public void testThreeArgConstructor() {
        assertEquals("John Doe", guest.getName());
        assertEquals("200112345678", guest.getNic());
        assertEquals("0771234567", guest.getPhone());
        assertTrue(guest.getCreatedAt() > 0);
    }

    @Test
    public void testFullConstructor() {
        Guest g = new Guest(5, "Jane Smith", "199987654321", "0712345678", "jane@email.com", "123 Street");
        assertEquals(5, g.getId());
        assertEquals("Jane Smith", g.getName());
        assertEquals("199987654321", g.getNic());
        assertEquals("0712345678", g.getPhone());
        assertEquals("jane@email.com", g.getEmail());
        assertEquals("123 Street", g.getAddress());
    }

    // --- Business Method Tests ---

    @Test
    public void testUpdateContact() {
        guest.updateContact("0777777777", "new@email.com");
        assertEquals("0777777777", guest.getPhone());
        assertEquals("new@email.com", guest.getEmail());
    }

    @Test
    public void testUpdateContactWithNull() {
        guest.updateContact(null, null);
        assertNull(guest.getPhone());
        assertNull(guest.getEmail());
    }

    // --- Getter/Setter Tests ---

    @Test
    public void testSetAndGetId() {
        guest.setId(100);
        assertEquals(100, guest.getId());
    }

    @Test
    public void testSetAndGetUserId() {
        guest.setUserId(5);
        assertEquals(5, guest.getUserId());
    }

    @Test
    public void testSetAndGetAddress() {
        guest.setAddress("456 Main Road, Galle");
        assertEquals("456 Main Road, Galle", guest.getAddress());
    }

    @Test
    public void testSetAndGetEmail() {
        guest.setEmail("guest@hotel.com");
        assertEquals("guest@hotel.com", guest.getEmail());
    }

    @Test
    public void testToString() {
        String str = guest.toString();
        assertTrue(str.contains("John Doe"));
        assertTrue(str.contains("200112345678"));
    }
}

