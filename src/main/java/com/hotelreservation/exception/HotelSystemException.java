package com.hotelreservation.exception;

/**
 * Base exception for the hotel reservation system.
 * All custom exceptions should extend this class.
 *
 * Provides structured error information including:
 * - errorCode: machine-readable code (e.g., AUTH_FAILED, PAYMENT_FAILED, ROOM_NOT_AVAILABLE)
 * - statusCode: HTTP-style status code for the error (401, 402, 409, 500, etc.)
 */
public class HotelSystemException extends Exception {
    private String errorCode;
    private int statusCode;

    public HotelSystemException(String message) {
        super(message);
        this.errorCode = "SYSTEM_ERROR";
        this.statusCode = 500;
    }

    public HotelSystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SYSTEM_ERROR";
        this.statusCode = 500;
    }

    public HotelSystemException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 500;
    }

    public HotelSystemException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public HotelSystemException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

