package com.hotelreservation.exception;

/**
 * Exception thrown when authentication fails (invalid credentials, expired session, etc.)
 */
public class AuthenticationException extends HotelSystemException {
    public AuthenticationException(String message) {
        super(message, "AUTH_FAILED", 401);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.setStatusCode(401);
    }

    private void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private int statusCode = 401;
}

