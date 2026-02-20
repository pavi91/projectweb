package com.hotelreservation.exception;

/**
 * Exception thrown when payment processing fails
 */
public class PaymentException extends HotelSystemException {
    public PaymentException(String message) {
        super(message, "PAYMENT_FAILED", 402);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}

