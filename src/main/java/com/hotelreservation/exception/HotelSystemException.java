package com.hotelreservation.exception;

/**
 * Base exception for the hotel reservation system.
 * All custom exceptions should extend this class.
 */
public class HotelSystemException extends Exception {
    private String errorCode;
    private int statusCode;

    public HotelSystemException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public HotelSystemException(String message, Throwable cause) {
        super(message, cause);
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

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

