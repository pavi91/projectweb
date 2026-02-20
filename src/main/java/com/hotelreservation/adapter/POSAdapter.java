package com.hotelreservation.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POSAdapter - Adapter for Point-of-Sale (POS) terminal payment processing
 * Adapts to external POS system interface for walk-in reservations
 */
public class POSAdapter implements IPaymentAdapter {
    private static final Logger logger = LoggerFactory.getLogger(POSAdapter.class);
    private ExternalPOSSystem posSystem;
    private String lastTransactionId;

    public POSAdapter() {
        // In real implementation, this would be injected or initialized from config
        this.posSystem = new ExternalPOSSystem();
    }

    public POSAdapter(ExternalPOSSystem posSystem) {
        this.posSystem = posSystem;
    }

    @Override
    public boolean pay(double amount) {
        try {
            logger.info("Processing POS payment for amount: {}", amount);
            boolean success = posSystem.authorize(amount);

            if (success) {
                lastTransactionId = "POS_" + System.currentTimeMillis();
                logger.info("POS payment successful. Transaction ID: {}", lastTransactionId);
                return true;
            } else {
                logger.warn("POS payment failed for amount: {}", amount);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error processing POS payment", e);
            return false;
        }
    }

    @Override
    public String getAdapterName() {
        return "POS_ADAPTER";
    }

    @Override
    public String getPaymentDetails() {
        return "POS Terminal | Transaction ID: " + (lastTransactionId != null ? lastTransactionId : "N/A");
    }

    public String getLastTransactionId() {
        return lastTransactionId;
    }

    /**
     * Mock external POS system for testing
     * In production, this would be actual hardware integration
     */
    public static class ExternalPOSSystem {
        private static final Logger logger = LoggerFactory.getLogger(ExternalPOSSystem.class);

        /**
         * Authorize payment through POS terminal
         * @param amount the amount to authorize
         * @return true if authorized, false otherwise
         */
        public boolean authorize(double amount) {
            logger.debug("POS Terminal: Authorizing amount: {}", amount);
            // Simulate POS terminal processing (success rate ~99%)
            return Math.random() > 0.01;
        }

        /**
         * Reverse a transaction
         * @param transactionId the transaction to reverse
         * @return true if reversal successful
         */
        public boolean reverse(String transactionId) {
            logger.debug("POS Terminal: Reversing transaction: {}", transactionId);
            return true;
        }
    }
}

