package com.hotelreservation.controller;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.service.PaymentService;
import com.hotelreservation.service.ReportService;
import com.hotelreservation.service.UserService;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminController
 * Tests staff creation, report generation, payment adapter configuration with mocked services.
 */
public class AdminControllerTest {

    private AdminController controller;
    private UserService mockUserService;
    private ReportService mockReportService;
    private PaymentService mockPaymentService;

    @Before
    public void setUp() {
        mockUserService = mock(UserService.class);
        mockReportService = mock(ReportService.class);
        mockPaymentService = mock(PaymentService.class);
        controller = new AdminController(mockUserService, mockReportService, mockPaymentService);
    }

    // =============================================
    //  Staff Account Creation Tests
    // =============================================

    @Test
    public void testCreateStaffAccountSuccess() throws Exception {
        doNothing().when(mockUserService).createUser(any(UserDTO.class));

        UserDTO staffDTO = new UserDTO("newstaff", "staff123", "RECEPTIONIST");

        // Use ReservationController.ControllerResult since AdminController uses it
        var result = controller.createStaffAccount(staffDTO);

        assertTrue(result.isSuccess());
        verify(mockUserService).createUser(any(UserDTO.class));
    }

    @Test
    public void testCreateStaffAccountNullDTO() {
        var result = controller.createStaffAccount(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("User details required"));
    }

    @Test
    public void testCreateStaffAccountEmptyUsername() {
        UserDTO dto = new UserDTO("", "pass123", "RECEPTIONIST");
        var result = controller.createStaffAccount(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void testCreateStaffAccountEmptyPassword() {
        UserDTO dto = new UserDTO("user", "", "RECEPTIONIST");
        var result = controller.createStaffAccount(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void testCreateStaffAccountEmptyRole() {
        UserDTO dto = new UserDTO("user", "pass123", "");
        var result = controller.createStaffAccount(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Role cannot be empty"));
    }

    @Test
    public void testCreateStaffAccountInvalidRole() {
        UserDTO dto = new UserDTO("user", "pass123", "ADMIN");
        var result = controller.createStaffAccount(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid role"));
    }

    @Test
    public void testCreateStaffAccountServiceError() throws Exception {
        doThrow(new IllegalArgumentException("Username already exists"))
                .when(mockUserService).createUser(any(UserDTO.class));

        UserDTO dto = new UserDTO("existing", "pass123", "RECEPTIONIST");
        var result = controller.createStaffAccount(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Username already exists"));
    }

    // =============================================
    //  Report Generation Tests
    // =============================================

    @Test
    public void testGenerateRevenueReport() {
        when(mockReportService.getRevenueStats()).thenReturn("Total Revenue: $5000");

        var result = controller.generateRevenueReport();

        assertTrue(result.isSuccess());
        assertEquals("Total Revenue: $5000", result.getData());
    }

    @Test
    public void testGenerateRevenueReportError() {
        when(mockReportService.getRevenueStats()).thenThrow(new RuntimeException("DB error"));

        var result = controller.generateRevenueReport();

        assertFalse(result.isSuccess());
    }

    @Test
    public void testGenerateOccupancyReport() {
        when(mockReportService.getOccupancyStats()).thenReturn("Occupancy: 75%");

        var result = controller.generateOccupancyReport();

        assertTrue(result.isSuccess());
        assertEquals("Occupancy: 75%", result.getData());
    }

    @Test
    public void testGenerateComprehensiveReport() {
        when(mockReportService.getComprehensiveReport()).thenReturn("Full report data");

        var result = controller.generateComprehensiveReport();

        assertTrue(result.isSuccess());
        assertEquals("Full report data", result.getData());
    }

    @Test
    public void testGenerateCancellationReport() {
        when(mockReportService.getCancellationStats()).thenReturn("Cancellations: 5");

        var result = controller.generateCancellationReport();

        assertTrue(result.isSuccess());
        assertEquals("Cancellations: 5", result.getData());
    }

    // =============================================
    //  Payment Adapter Configuration Tests
    // =============================================

    @Test
    public void testConfigurePaymentAdapterPOS() {
        var result = controller.configurePaymentAdapter("POS");

        assertTrue(result.isSuccess());
        verify(mockPaymentService).setPaymentAdapter(any());
    }

    @Test
    public void testConfigurePaymentAdapterOnlineGateway() {
        var result = controller.configurePaymentAdapter("ONLINE_GATEWAY");

        assertTrue(result.isSuccess());
        verify(mockPaymentService).setPaymentAdapter(any());
    }

    @Test
    public void testConfigurePaymentAdapterNull() {
        var result = controller.configurePaymentAdapter(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Adapter type required"));
    }

    @Test
    public void testConfigurePaymentAdapterEmpty() {
        var result = controller.configurePaymentAdapter("");

        assertFalse(result.isSuccess());
    }
}

