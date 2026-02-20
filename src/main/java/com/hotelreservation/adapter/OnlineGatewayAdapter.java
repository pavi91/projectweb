package com.hotelreservation.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OnlineGatewayAdapter - Adapter for Online Payment Gateway (e.g., Stripe, PayPal)
 * Adapts to external online payment portal for online reservations
 */
public class OnlineGatewayAdapter implements IPaymentAdapter {
    private static final Logger logger = LoggerFactory.getLogger(OnlineGatewayAdapter.class);
    private SecureBankPortal bankPortal;
    private String lastPaymentLink;
    private String lastTransactionId;

    public OnlineGatewayAdapter() {
        // In real implementation, this would be injected or initialized from config
        this.bankPortal = new SecureBankPortal();
    }

    public OnlineGatewayAdapter(SecureBankPortal bankPortal) {
        this.bankPortal = bankPortal;
    }

    @Override
    public boolean pay(double amount) {
        try {
            logger.info("Processing online gateway payment for amount: {}", amount);

            // Generate payment link
            String paymentLink = bankPortal.generatePaymentLink(amount);
            this.lastPaymentLink = paymentLink;

            logger.info("Payment link generated: {}", paymentLink);

            // In real system, user would be redirected to this link
            // Here we simulate the payment callback
            boolean success = bankPortal.processPaymentCallback(paymentLink);

            if (success) {
                lastTransactionId = "GW_" + System.currentTimeMillis();
                logger.info("Online payment successful. Transaction ID: {}", lastTransactionId);
                return true;
            } else {
                logger.warn("Online payment failed for amount: {}", amount);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error processing online gateway payment", e);
            return false;
        }
    }

    @Override
    public String getAdapterName() {
        return "ONLINE_GATEWAY_ADAPTER";
    }

    @Override
    public String getPaymentDetails() {
        return "Online Gateway | Transaction ID: " + (lastTransactionId != null ? lastTransactionId : "N/A");
    }

    public String getLastPaymentLink() {
        return lastPaymentLink;
    }

    public String getLastTransactionId() {
        return lastTransactionId;
    }

    /**
     * Mock secure bank portal for testing
     * In production, this would be actual payment gateway API integration
     */
    public static class SecureBankPortal {
        private static final Logger logger = LoggerFactory.getLogger(SecureBankPortal.class);

        /**
         * Generate a payment link for the given amount
         * @param amount the amount to create payment link for
         * @return payment URL/link
         */
        public String generatePaymentLink(double amount) {
            String link = "https://secure-bank.com/pay?amount=" + amount + "&ref=" + System.nanoTime();
            logger.debug("Bank Portal: Generated payment link: {}", link);
            return link;
        }

        /**
         * Process payment callback from payment link
         * @param paymentLink the payment link to process
         * @return true if payment successful
         */
        public boolean processPaymentCallback(String paymentLink) {
            logger.debug("Bank Portal: Processing callback for link: {}", paymentLink);
            // Simulate payment processing (success rate ~98%)
            return Math.random() > 0.02;
        }

        /**
         * Refund a payment
         * @param transactionId the transaction to refund
         * @param amount the amount to refund
         * @return true if refund successful
         */
        public boolean refund(String transactionId, double amount) {
            logger.debug("Bank Portal: Refunding transaction: {}, Amount: {}", transactionId, amount);
            return true;
        }
    }
}

