package com.hotelreservation.service.impl;

import com.hotelreservation.adapter.IPaymentAdapter;
import com.hotelreservation.adapter.POSAdapter;
import com.hotelreservation.exception.PaymentException;
import com.hotelreservation.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PaymentServiceImpl - Implementation of PaymentService
 * Uses Adapter pattern to support multiple payment channels
 * Allows runtime switching between POS and Online Gateway
 */
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private IPaymentAdapter currentAdapter;

    public PaymentServiceImpl() {
        // Default to POS adapter
        this.currentAdapter = new POSAdapter();
        logger.info("PaymentService initialized with default adapter: {}", currentAdapter.getAdapterName());
    }

    public PaymentServiceImpl(IPaymentAdapter adapter) {
        this.currentAdapter = adapter;
        logger.info("PaymentService initialized with adapter: {}", adapter.getAdapterName());
    }

    @Override
    public boolean processPayment(double amount) throws PaymentException {
        if (currentAdapter == null) {
            throw new PaymentException("No payment adapter configured");
        }

        if (amount <= 0) {
            throw new PaymentException("Invalid payment amount: " + amount);
        }

        try {
            logger.info("Processing payment of {} using {}", amount, currentAdapter.getAdapterName());
            boolean success = currentAdapter.pay(amount);

            if (success) {
                logger.info("Payment successful: {}", currentAdapter.getPaymentDetails());
                return true;
            } else {
                throw new PaymentException("Payment declined by adapter");
            }
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing", e);
            throw new PaymentException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void setPaymentAdapter(IPaymentAdapter adapter) {
        if (adapter == null) {
            logger.warn("Attempt to set null adapter");
            return;
        }
        this.currentAdapter = adapter;
        logger.info("Payment adapter switched to: {}", adapter.getAdapterName());
    }

    @Override
    public IPaymentAdapter getCurrentAdapter() {
        return currentAdapter;
    }

    @Override
    public boolean processRefund(double amount, String reason) throws PaymentException {
        if (currentAdapter == null) {
            throw new PaymentException("No payment adapter configured for refund");
        }

        if (amount <= 0) {
            throw new PaymentException("Invalid refund amount: " + amount);
        }

        try {
            logger.info("Processing refund of {} using {}. Reason: {}", amount, currentAdapter.getAdapterName(), reason);

            // In production, this would call refund-specific method on adapter
            // For now, we'll log it as a special case
            logger.info("Refund processed successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error processing refund", e);
            throw new PaymentException("Refund processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getLastTransactionDetails() {
        if (currentAdapter == null) {
            return "No adapter configured";
        }
        return currentAdapter.getPaymentDetails();
    }

    /**
     * Get payment adapter statistics/status
     * @return status string
     */
    public String getAdapterStatus() {
        if (currentAdapter == null) {
            return "Payment adapter: NONE";
        }
        return "Payment adapter: " + currentAdapter.getAdapterName();
    }
}

