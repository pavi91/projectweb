package com.hotelreservation.adapter;

/**
 * IPaymentAdapter - Adapter pattern interface for payment processing
 * Allows abstraction of different payment channel implementations
 */
public interface IPaymentAdapter {

    /**
     * Process payment through the adapter's payment channel
     * @param amount the amount to process
     * @return true if payment successful, false otherwise
     */
    boolean pay(double amount);

    /**
     * Get the adapter's name/type
     * @return adapter name
     */
    String getAdapterName();

    /**
     * Get additional payment details/status
     * @return payment details string
     */
    String getPaymentDetails();
}

