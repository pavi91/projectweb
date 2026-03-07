package com.hotelreservation.exception;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the custom exception hierarchy
 * Tests HotelSystemException, AuthenticationException, PaymentException, RoomNotAvailableException.
 */
public class ExceptionTest {

    // =============================================
    //  HotelSystemException Tests
    // =============================================

    @Test
    public void testHotelSystemExceptionMessage() {
        HotelSystemException ex = new HotelSystemException("System error");
        assertEquals("System error", ex.getMessage());
        assertEquals("SYSTEM_ERROR", ex.getErrorCode());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    public void testHotelSystemExceptionWithCause() {
        Exception cause = new RuntimeException("root cause");
        HotelSystemException ex = new HotelSystemException("Wrapped error", cause);
        assertEquals("Wrapped error", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertEquals("SYSTEM_ERROR", ex.getErrorCode());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    public void testHotelSystemExceptionWithErrorCode() {
        HotelSystemException ex = new HotelSystemException("Custom error", "CUSTOM_CODE");
        assertEquals("CUSTOM_CODE", ex.getErrorCode());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    public void testHotelSystemExceptionWithCodeAndStatus() {
        HotelSystemException ex = new HotelSystemException("Not found", "NOT_FOUND", 404);
        assertEquals("Not found", ex.getMessage());
        assertEquals("NOT_FOUND", ex.getErrorCode());
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    public void testHotelSystemExceptionFullConstructor() {
        Exception cause = new RuntimeException("root");
        HotelSystemException ex = new HotelSystemException("Error", "ERR_CODE", 503, cause);
        assertEquals("Error", ex.getMessage());
        assertEquals("ERR_CODE", ex.getErrorCode());
        assertEquals(503, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void testHotelSystemExceptionIsException() {
        HotelSystemException ex = new HotelSystemException("test");
        assertTrue(ex instanceof Exception);
    }

    // =============================================
    //  AuthenticationException Tests
    // =============================================

    @Test
    public void testAuthenticationExceptionMessage() {
        AuthenticationException ex = new AuthenticationException("Invalid credentials");
        assertEquals("Invalid credentials", ex.getMessage());
        assertEquals("AUTH_FAILED", ex.getErrorCode());
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    public void testAuthenticationExceptionWithCause() {
        Exception cause = new RuntimeException("BCrypt error");
        AuthenticationException ex = new AuthenticationException("Auth failed", cause);
        assertEquals("Auth failed", ex.getMessage());
        assertEquals("AUTH_FAILED", ex.getErrorCode());
        assertEquals(401, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void testAuthenticationExceptionInheritance() {
        AuthenticationException ex = new AuthenticationException("test");
        assertTrue(ex instanceof HotelSystemException);
        assertTrue(ex instanceof Exception);
    }

    // =============================================
    //  PaymentException Tests
    // =============================================

    @Test
    public void testPaymentExceptionMessage() {
        PaymentException ex = new PaymentException("Payment declined");
        assertEquals("Payment declined", ex.getMessage());
        assertEquals("PAYMENT_FAILED", ex.getErrorCode());
        assertEquals(402, ex.getStatusCode());
    }

    @Test
    public void testPaymentExceptionWithCause() {
        Exception cause = new RuntimeException("Gateway timeout");
        PaymentException ex = new PaymentException("Payment failed", cause);
        assertEquals("Payment failed", ex.getMessage());
        assertEquals("PAYMENT_FAILED", ex.getErrorCode());
        assertEquals(402, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void testPaymentExceptionInheritance() {
        PaymentException ex = new PaymentException("test");
        assertTrue(ex instanceof HotelSystemException);
    }

    // =============================================
    //  RoomNotAvailableException Tests
    // =============================================

    @Test
    public void testRoomNotAvailableExceptionMessage() {
        RoomNotAvailableException ex = new RoomNotAvailableException("No rooms");
        assertEquals("No rooms", ex.getMessage());
        assertEquals("ROOM_NOT_AVAILABLE", ex.getErrorCode());
        assertEquals(409, ex.getStatusCode());
    }

    @Test
    public void testRoomNotAvailableExceptionWithRoomNumber() {
        RoomNotAvailableException ex = new RoomNotAvailableException("already booked", "101");
        assertTrue(ex.getMessage().contains("Room 101"));
        assertTrue(ex.getMessage().contains("already booked"));
        assertEquals("ROOM_NOT_AVAILABLE", ex.getErrorCode());
        assertEquals(409, ex.getStatusCode());
    }

    @Test
    public void testRoomNotAvailableExceptionInheritance() {
        RoomNotAvailableException ex = new RoomNotAvailableException("test");
        assertTrue(ex instanceof HotelSystemException);
    }

    // =============================================
    //  Polymorphic Catch Tests
    // =============================================

    @Test
    public void testCatchAllHotelExceptions() {
        // All custom exceptions can be caught as HotelSystemException
        try {
            throw new AuthenticationException("auth error");
        } catch (HotelSystemException e) {
            assertEquals("AUTH_FAILED", e.getErrorCode());
            assertEquals(401, e.getStatusCode());
        }

        try {
            throw new PaymentException("pay error");
        } catch (HotelSystemException e) {
            assertEquals("PAYMENT_FAILED", e.getErrorCode());
            assertEquals(402, e.getStatusCode());
        }

        try {
            throw new RoomNotAvailableException("room error");
        } catch (HotelSystemException e) {
            assertEquals("ROOM_NOT_AVAILABLE", e.getErrorCode());
            assertEquals(409, e.getStatusCode());
        }
    }
}

