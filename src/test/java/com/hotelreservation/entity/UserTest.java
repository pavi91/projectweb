package com.hotelreservation.entity;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for User entity
 * Tests password hashing, validation, getters/setters, and constructors.
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("admin", User.hashPassword("admin123"), "ADMIN");
    }

    // --- Constructor Tests ---

    @Test
    public void testDefaultConstructor() {
        User u = new User();
        assertNull(u.getUsername());
        assertNull(u.getPasswordHash());
        assertNull(u.getRole());
        assertEquals(0, u.getId());
    }

    @Test
    public void testThreeArgConstructor() {
        String hashed = User.hashPassword("pass123");
        User u = new User("testuser", hashed, "GUEST");
        assertEquals("testuser", u.getUsername());
        assertEquals(hashed, u.getPasswordHash());
        assertEquals("GUEST", u.getRole());
        assertTrue(u.getCreatedAt() > 0);
        assertTrue(u.getUpdatedAt() > 0);
    }

    @Test
    public void testFourArgConstructor() {
        String hashed = User.hashPassword("pass123");
        User u = new User(10, "john", hashed, "RECEPTIONIST");
        assertEquals(10, u.getId());
        assertEquals("john", u.getUsername());
        assertEquals("RECEPTIONIST", u.getRole());
    }

    // --- Password Hashing Tests ---

    @Test
    public void testHashPasswordProducesValidBCryptHash() {
        String hash = User.hashPassword("mypassword");
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(hash.length() > 50);
    }

    @Test
    public void testHashPasswordProducesDifferentHashesForSameInput() {
        String hash1 = User.hashPassword("samepassword");
        String hash2 = User.hashPassword("samepassword");
        // BCrypt uses random salt, so hashes should differ
        assertNotEquals(hash1, hash2);
    }

    // --- Password Validation Tests ---

    @Test
    public void testValidatePasswordCorrect() {
        assertTrue(user.validatePassword("admin123"));
    }

    @Test
    public void testValidatePasswordIncorrect() {
        assertFalse(user.validatePassword("wrongpassword"));
    }

    @Test
    public void testValidatePasswordNull() {
        assertFalse(user.validatePassword(null));
    }

    @Test
    public void testValidatePasswordEmpty() {
        assertFalse(user.validatePassword(""));
    }

    @Test
    public void testValidatePasswordWhenHashIsNull() {
        User u = new User();
        u.setPasswordHash(null);
        assertFalse(u.validatePassword("anything"));
    }

    // --- Getter/Setter Tests ---

    @Test
    public void testSetAndGetId() {
        user.setId(42);
        assertEquals(42, user.getId());
    }

    @Test
    public void testSetAndGetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    public void testSetAndGetRole() {
        user.setRole("GUEST");
        assertEquals("GUEST", user.getRole());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        long time = System.currentTimeMillis();
        user.setCreatedAt(time);
        assertEquals(time, user.getCreatedAt());
    }

    @Test
    public void testSetAndGetUpdatedAt() {
        long time = System.currentTimeMillis();
        user.setUpdatedAt(time);
        assertEquals(time, user.getUpdatedAt());
    }
}

