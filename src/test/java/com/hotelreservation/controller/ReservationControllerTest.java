package com.hotelreservation.controller;

import com.hotelreservation.controller.ReservationController.ControllerResult;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.service.impl.BookingService;
import com.hotelreservation.service.impl.RoomServiceImpl;
import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationController
 * Tests room search, reservation creation, cancellation with mocked services.
 */
public class ReservationControllerTest {

    private ReservationController controller;
    private BookingService mockBookingService;
    private RoomServiceImpl mockRoomService;

    @Before
    public void setUp() {
        mockBookingService = mock(BookingService.class);
        mockRoomService = mock(RoomServiceImpl.class);
        controller = new ReservationController(mockBookingService, mockRoomService);
    }

    // =============================================
    //  Search Rooms Tests
    // =============================================

    @Test
    public void testSearchRoomsSuccess() {
        List<RoomDTO> rooms = Arrays.asList(
                new RoomDTO(1, "101", "SINGLE", 100.00, "AVAILABLE", true),
                new RoomDTO(2, "201", "DOUBLE", 175.00, "AVAILABLE", true)
        );
        when(mockRoomService.getAvailableRooms(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3)))
                .thenReturn(rooms);

        ControllerResult<List<RoomDTO>> result = controller.searchRooms("2026-04-01", "2026-04-03");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testSearchRoomsNoResults() {
        when(mockRoomService.getAvailableRooms(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        ControllerResult<List<RoomDTO>> result = controller.searchRooms("2026-12-25", "2026-12-26");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().size());
    }

    @Test
    public void testSearchRoomsNullDates() {
        ControllerResult<List<RoomDTO>> result = controller.searchRooms(null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
    }

    @Test
    public void testSearchRoomsInvalidDateFormat() {
        ControllerResult<List<RoomDTO>> result = controller.searchRooms("01-04-2026", "03-04-2026");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testSearchRoomsNullCheckIn() {
        ControllerResult<List<RoomDTO>> result = controller.searchRooms(null, "2026-04-03");

        assertFalse(result.isSuccess());
    }

    // =============================================
    //  Make Reservation Tests
    // =============================================

    @Test
    public void testMakeReservationSuccess() throws Exception {
        GuestDTO guest = new GuestDTO("John", "123V", "077");
        guest.setId(1);

        ReservationDTO expectedRes = new ReservationDTO("RES-001", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3), 200.00);
        expectedRes.setStatus("CONFIRMED");
        expectedRes.setReservationType("ONLINE");

        when(mockBookingService.makeOnlineReservation(
                any(GuestDTO.class), eq(1),
                eq(LocalDate.of(2026, 4, 1)), eq(LocalDate.of(2026, 4, 3))))
                .thenReturn(expectedRes);

        ControllerResult<ReservationDTO> result = controller.makeReservation(
                guest, 1, "2026-04-01", "2026-04-03");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("RES-001", result.getData().getId());
        assertEquals(200.00, result.getData().getTotalAmount(), 0.001);
    }

    @Test
    public void testMakeReservationNullGuest() {
        ControllerResult<ReservationDTO> result = controller.makeReservation(
                null, 1, "2026-04-01", "2026-04-03");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Guest details required"));
    }

    @Test
    public void testMakeReservationInvalidDate() {
        GuestDTO guest = new GuestDTO("John", "123V", "077");

        ControllerResult<ReservationDTO> result = controller.makeReservation(
                guest, 1, "invalid-date", "2026-04-03");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testMakeReservationServiceThrowsException() throws Exception {
        GuestDTO guest = new GuestDTO("John", "123V", "077");
        guest.setId(1);

        when(mockBookingService.makeOnlineReservation(
                any(GuestDTO.class), anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Room not available"));

        ControllerResult<ReservationDTO> result = controller.makeReservation(
                guest, 1, "2026-04-01", "2026-04-03");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Room not available"));
    }

    // =============================================
    //  Cancel Reservation Tests
    // =============================================

    @Test
    public void testCancelReservationSuccess() throws Exception {
        when(mockBookingService.cancelReservation("RES-001")).thenReturn(true);

        ControllerResult<Boolean> result = controller.cancelReservation("RES-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        assertEquals("Reservation cancelled successfully", result.getMessage());
    }

    @Test
    public void testCancelReservationFailed() throws Exception {
        when(mockBookingService.cancelReservation("RES-999")).thenReturn(false);

        ControllerResult<Boolean> result = controller.cancelReservation("RES-999");

        assertFalse(result.isSuccess());
    }

    @Test
    public void testCancelReservationNullId() {
        ControllerResult<Boolean> result = controller.cancelReservation(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Reservation ID required"));
    }

    @Test
    public void testCancelReservationEmptyId() {
        ControllerResult<Boolean> result = controller.cancelReservation("  ");

        assertFalse(result.isSuccess());
    }

    @Test
    public void testCancelReservationServiceThrows() throws Exception {
        when(mockBookingService.cancelReservation("RES-001"))
                .thenThrow(new RuntimeException("Already cancelled"));

        ControllerResult<Boolean> result = controller.cancelReservation("RES-001");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Already cancelled"));
    }

    // =============================================
    //  Get Reservation Tests
    // =============================================

    @Test
    public void testGetReservationSuccess() throws Exception {
        ReservationDTO res = new ReservationDTO("RES-001", 1, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3), 200.00);
        when(mockBookingService.getReservation("RES-001")).thenReturn(res);

        ControllerResult<ReservationDTO> result = controller.getReservation("RES-001");

        assertTrue(result.isSuccess());
        assertEquals("RES-001", result.getData().getId());
    }

    @Test
    public void testGetReservationNotFound() throws Exception {
        when(mockBookingService.getReservation("RES-999")).thenReturn(null);

        ControllerResult<ReservationDTO> result = controller.getReservation("RES-999");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    public void testGetReservationNullId() {
        ControllerResult<ReservationDTO> result = controller.getReservation(null);

        assertFalse(result.isSuccess());
    }

    // =============================================
    //  ControllerResult Tests
    // =============================================

    @Test
    public void testControllerResultToString() {
        ControllerResult<String> result = new ControllerResult<>(true, "OK", "data");
        String str = result.toString();
        assertTrue(str.contains("success=true"));
        assertTrue(str.contains("OK"));
    }
}

