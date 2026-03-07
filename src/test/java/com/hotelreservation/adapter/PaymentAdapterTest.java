package com.hotelreservation.adapter;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Adapter Pattern — POSAdapter and OnlineGatewayAdapter
 * Uses manual test doubles (subclass stubs) for external systems to avoid Mockito inner-class issues on JDK 21.
 */
public class PaymentAdapterTest {

    // =============================================
    //  Manual Test Doubles
    // =============================================

    /** A POS system stub that always authorizes successfully */
    static class SuccessPOS extends POSAdapter.ExternalPOSSystem {
        @Override
        public boolean authorize(double amount) { return true; }
    }

    /** A POS system stub that always declines */
    static class FailPOS extends POSAdapter.ExternalPOSSystem {
        @Override
        public boolean authorize(double amount) { return false; }
    }

    /** A POS system stub that throws an exception */
    static class ErrorPOS extends POSAdapter.ExternalPOSSystem {
        @Override
        public boolean authorize(double amount) { throw new RuntimeException("POS Hardware Error"); }
    }

    /** A bank portal stub that always processes successfully */
    static class SuccessPortal extends OnlineGatewayAdapter.SecureBankPortal {
        @Override
        public String generatePaymentLink(double amount) {
            return "https://secure-bank.com/pay?amount=" + amount + "&ref=TEST";
        }
        @Override
        public boolean processPaymentCallback(String paymentLink) { return true; }
    }

    /** A bank portal stub that fails at the callback stage */
    static class FailCallbackPortal extends OnlineGatewayAdapter.SecureBankPortal {
        @Override
        public String generatePaymentLink(double amount) {
            return "https://secure-bank.com/pay?ref=FAIL";
        }
        @Override
        public boolean processPaymentCallback(String paymentLink) { return false; }
    }

    /** A bank portal stub that throws an exception */
    static class ErrorPortal extends OnlineGatewayAdapter.SecureBankPortal {
        @Override
        public String generatePaymentLink(double amount) {
            throw new RuntimeException("Bank API Error");
        }
    }

    // =============================================
    //  POSAdapter Tests
    // =============================================

    @Test
    public void testPOSAdapterName() {
        POSAdapter adapter = new POSAdapter();
        assertEquals("POS_ADAPTER", adapter.getAdapterName());
    }

    @Test
    public void testPOSAdapterPaySuccess() {
        POSAdapter adapter = new POSAdapter(new SuccessPOS());
        boolean result = adapter.pay(150.00);

        assertTrue(result);
        assertNotNull(adapter.getLastTransactionId());
        assertTrue(adapter.getLastTransactionId().startsWith("POS_"));
    }

    @Test
    public void testPOSAdapterPayFailure() {
        POSAdapter adapter = new POSAdapter(new FailPOS());
        boolean result = adapter.pay(150.00);

        assertFalse(result);
        assertNull(adapter.getLastTransactionId());
    }

    @Test
    public void testPOSAdapterPayException() {
        POSAdapter adapter = new POSAdapter(new ErrorPOS());
        boolean result = adapter.pay(100.00);

        assertFalse(result);
    }

    @Test
    public void testPOSAdapterPaymentDetailsBeforePayment() {
        POSAdapter adapter = new POSAdapter(new SuccessPOS());
        String details = adapter.getPaymentDetails();
        assertTrue(details.contains("N/A"));
    }

    @Test
    public void testPOSAdapterPaymentDetailsAfterPayment() {
        POSAdapter adapter = new POSAdapter(new SuccessPOS());
        adapter.pay(100.00);

        String details = adapter.getPaymentDetails();
        assertTrue(details.contains("POS Terminal"));
        assertTrue(details.contains("POS_"));
    }

    // =============================================
    //  OnlineGatewayAdapter Tests
    // =============================================

    @Test
    public void testOnlineGatewayAdapterName() {
        OnlineGatewayAdapter adapter = new OnlineGatewayAdapter();
        assertEquals("ONLINE_GATEWAY_ADAPTER", adapter.getAdapterName());
    }

    @Test
    public void testOnlineGatewayPaySuccess() {
        OnlineGatewayAdapter adapter = new OnlineGatewayAdapter(new SuccessPortal());
        boolean result = adapter.pay(200.00);

        assertTrue(result);
        assertNotNull(adapter.getLastTransactionId());
        assertTrue(adapter.getLastTransactionId().startsWith("GW_"));
        assertNotNull(adapter.getLastPaymentLink());
    }

    @Test
    public void testOnlineGatewayPayCallbackFails() {
        OnlineGatewayAdapter adapter = new OnlineGatewayAdapter(new FailCallbackPortal());
        boolean result = adapter.pay(200.00);

        assertFalse(result);
    }

    @Test
    public void testOnlineGatewayPayException() {
        OnlineGatewayAdapter adapter = new OnlineGatewayAdapter(new ErrorPortal());
        boolean result = adapter.pay(100.00);

        assertFalse(result);
    }

    @Test
    public void testOnlineGatewayPaymentDetailsBeforePayment() {
        OnlineGatewayAdapter adapter = new OnlineGatewayAdapter(new SuccessPortal());
        String details = adapter.getPaymentDetails();
        assertTrue(details.contains("N/A"));
    }

    // =============================================
    //  Adapter Pattern Polymorphism Tests
    // =============================================

    @Test
    public void testAdapterPolymorphism() {
        IPaymentAdapter posAdapter = new POSAdapter(new SuccessPOS());
        IPaymentAdapter gatewayAdapter = new OnlineGatewayAdapter(new SuccessPortal());

        assertTrue(posAdapter.pay(100.00));
        assertTrue(gatewayAdapter.pay(100.00));
    }

    @Test
    public void testAdapterRuntimeSwap() {
        IPaymentAdapter adapter = new POSAdapter(new SuccessPOS());
        assertEquals("POS_ADAPTER", adapter.getAdapterName());
        assertTrue(adapter.pay(50.00));

        adapter = new OnlineGatewayAdapter(new SuccessPortal());
        assertEquals("ONLINE_GATEWAY_ADAPTER", adapter.getAdapterName());
        assertTrue(adapter.pay(50.00));
    }

    // =============================================
    //  External System Tests
    // =============================================

    @Test
    public void testExternalPOSSystemReverse() {
        POSAdapter.ExternalPOSSystem pos = new POSAdapter.ExternalPOSSystem();
        assertTrue(pos.reverse("TX-12345"));
    }

    @Test
    public void testSecureBankPortalGenerateLink() {
        OnlineGatewayAdapter.SecureBankPortal portal = new OnlineGatewayAdapter.SecureBankPortal();
        String link = portal.generatePaymentLink(250.00);
        assertNotNull(link);
        assertTrue(link.contains("250.0"));
    }

    @Test
    public void testSecureBankPortalRefund() {
        OnlineGatewayAdapter.SecureBankPortal portal = new OnlineGatewayAdapter.SecureBankPortal();
        assertTrue(portal.refund("TX-001", 100.00));
    }
}
