package com.hotelreservation.service;

import com.hotelreservation.adapter.IPaymentAdapter;
import com.hotelreservation.exception.PaymentException;

/**
 * PaymentService interface - defines contract for payment processing
 * Uses Adapter pattern for flexible payment channel integration
 */
public interface PaymentService {

    /**
     * Process a payment through the current adapter
     * @param amount the amount to process
     * @return true if payment successful, false otherwise
     * @throws PaymentException if payment processing fails
     */
    boolean processPayment(double amount) throws PaymentException;

    /**
     * Set the payment adapter (allows runtime switching between POS and Online Gateway)
     * @param adapter the payment adapter to use
     */
    void setPaymentAdapter(IPaymentAdapter adapter);

    /**
     * Get the current payment adapter
     * @return the current adapter
     */
    IPaymentAdapter getCurrentAdapter();

    /**
     * Process a refund for a reservation
     * @param amount the amount to refund
     * @param reason the reason for refund
     * @return true if refund successful
     * @throws PaymentException if refund fails
     */
    boolean processRefund(double amount, String reason) throws PaymentException;

    /**
     * Get last transaction details
     * @return transaction details string
     */
    String getLastTransactionDetails();
}

