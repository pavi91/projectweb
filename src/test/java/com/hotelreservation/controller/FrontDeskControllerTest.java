package com.hotelreservation.controller;

import com.hotelreservation.controller.FrontDeskController.ControllerResult;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.service.impl.BookingService;
import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FrontDeskController
 * Tests walk-in reservation, check-in, and check-out with mocked BookingService.
 */
public class FrontDeskControllerTest {

    private FrontDeskController controller;
    private BookingService mockBookingService;

    @Before
    public void setUp() {
        mockBookingService = mock(BookingService.class);
        controller = new FrontDeskController(mockBookingService);
    }

    // =============================================
    //  Walk-In Reservation Tests
    // =============================================

    @Test
    public void testMakeWalkInReservationSuccess() throws Exception {
        GuestDTO guest = new GuestDTO("Walk-In Guest", "998877V", "0712345678");
        ReservationDTO expected = new ReservationDTO("RES-W01", 1, 2,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 2), 175.00);
        expected.setReservationType("WALK_IN");
        expected.setStatus("CONFIRMED");

        when(mockBookingService.makeWalkInReservation(
                any(GuestDTO.class), eq(2),
                eq(LocalDate.of(2026, 4, 1)), eq(LocalDate.of(2026, 4, 2))))
                .thenReturn(expected);

        ControllerResult<ReservationDTO> result = controller.makeWalkInReservation(
                guest, 2, "2026-04-01", "2026-04-02");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("RES-W01", result.getData().getId());
        assertEquals(175.00, result.getData().getTotalAmount(), 0.001);
    }

    @Test
    public void testMakeWalkInReservationNullGuest() {
        ControllerResult<ReservationDTO> result = controller.makeWalkInReservation(
                null, 1, "2026-04-01", "2026-04-02");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Guest details required"));
    }

    @Test
    public void testMakeWalkInReservationInvalidDate() {
        GuestDTO guest = new GuestDTO("Test", "123V", "077");

        ControllerResult<ReservationDTO> result = controller.makeWalkInReservation(
                guest, 1, "not-a-date", "2026-04-02");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testMakeWalkInReservationServiceError() throws Exception {
        GuestDTO guest = new GuestDTO("Test", "123V", "077");
        when(mockBookingService.makeWalkInReservation(
                any(GuestDTO.class), anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("No rooms available"));

        ControllerResult<ReservationDTO> result = controller.makeWalkInReservation(
                guest, 1, "2026-04-01", "2026-04-02");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("No rooms available"));
    }

    // =============================================
    //  Check-In Tests
    // =============================================

    @Test
    public void testCheckInSuccess() throws Exception {
        doNothing().when(mockBookingService).checkIn("RES-001");

        ControllerResult<Boolean> result = controller.checkIn("RES-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        verify(mockBookingService).checkIn("RES-001");
    }

    @Test
    public void testCheckInNullId() {
        ControllerResult<Boolean> result = controller.checkIn(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Reservation ID required"));
    }

    @Test
    public void testCheckInEmptyId() {
        ControllerResult<Boolean> result = controller.checkIn("");

        assertFalse(result.isSuccess());
    }

    @Test
    public void testCheckInServiceError() throws Exception {
        doThrow(new RuntimeException("Reservation not found"))
                .when(mockBookingService).checkIn("RES-999");

        ControllerResult<Boolean> result = controller.checkIn("RES-999");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Reservation not found"));
    }

    // =============================================
    //  Check-Out Tests
    // =============================================

    @Test
    public void testCheckOutSuccess() throws Exception {
        ReservationDTO expectedRes = new ReservationDTO("RES-001", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3), 200.00);
        expectedRes.setStatus("CHECKED_OUT");

        when(mockBookingService.checkOut("RES-001")).thenReturn(expectedRes);

        ControllerResult<ReservationDTO> result = controller.checkOut("RES-001");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("CHECKED_OUT", result.getData().getStatus());
        assertEquals(200.00, result.getData().getTotalAmount(), 0.001);
    }

    @Test
    public void testCheckOutNullId() {
        ControllerResult<ReservationDTO> result = controller.checkOut(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Reservation ID required"));
    }

    @Test
    public void testCheckOutEmptyId() {
        ControllerResult<ReservationDTO> result = controller.checkOut("  ");

        assertFalse(result.isSuccess());
    }

    @Test
    public void testCheckOutServiceError() throws Exception {
        when(mockBookingService.checkOut("RES-001"))
                .thenThrow(new RuntimeException("Not checked in"));

        ControllerResult<ReservationDTO> result = controller.checkOut("RES-001");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Not checked in"));
    }
}

