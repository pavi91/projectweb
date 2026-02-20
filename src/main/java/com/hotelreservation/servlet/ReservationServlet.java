package com.hotelreservation.servlet;

import com.hotelreservation.controller.ReservationController;
import com.hotelreservation.controller.ReservationController.ControllerResult;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.service.impl.BookingService;
import com.hotelreservation.service.impl.RoomServiceImpl;
import com.hotelreservation.repository.impl.UserDAOImpl;
import com.hotelreservation.repository.impl.RoomDAOImpl;
import com.hotelreservation.repository.impl.ReservationDAOImpl;
import com.hotelreservation.service.impl.UserServiceImpl;
import com.hotelreservation.service.impl.PaymentServiceImpl;
import com.hotelreservation.service.impl.ReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * ReservationServlet - Handles guest reservation operations
 *
 * URL Pattern: /reservation/*
 * - /reservation/search (GET/POST) - Search for available rooms
 * - /reservation/create (POST) - Create new reservation
 * - /reservation/list (GET) - View my reservations
 * - /reservation/cancel (POST) - Cancel reservation
 */
public class ReservationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServlet.class);
    private static final long serialVersionUID = 1L;

    private ReservationController controller;
    private RoomServiceImpl roomService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        roomService = new RoomServiceImpl(new RoomDAOImpl());

        BookingService bookingService = new BookingService(
            null, // OnlineResService - will be initialized in controller
            null, // WalkInResService - will be initialized in controller
            roomService,
            new PaymentServiceImpl(),
            new ReservationDAOImpl()
        );

        controller = new ReservationController(bookingService, roomService);
        logger.info("ReservationServlet initialized");
    }

    /**
     * GET: Display forms and lists
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        logger.debug("GET /reservation{} - Handling request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo == null || pathInfo.equals("/search")) {
                handleSearchForm(request, response);
            } else if (pathInfo.equals("/list")) {
                handleMyReservations(request, response);
            } else if (pathInfo.equals("/cancel")) {
                handleCancelForm(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * POST: Process form submissions
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        logger.debug("POST /reservation{} - Processing request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo == null || pathInfo.equals("/search")) {
                handleSearchResults(request, response);
            } else if (pathInfo.equals("/create")) {
                handleReservationCreate(request, response);
            } else if (pathInfo.equals("/cancel")) {
                handleReservationCancel(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Handle search form display
     */
    private void handleSearchForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying room search form");
        request.getRequestDispatcher("/jsp/guest/roomSearch.jsp").forward(request, response);
    }

    /**
     * Handle search results
     */
    private void handleSearchResults(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");

        logger.info("Searching rooms for dates: {} to {}", checkInStr, checkOutStr);

        ControllerResult<List<RoomDTO>> result = controller.searchRooms(checkInStr, checkOutStr);

        if (result.isSuccess()) {
            request.setAttribute("rooms", result.getData());
            request.setAttribute("checkIn", checkInStr);
            request.setAttribute("checkOut", checkOutStr);
            request.getRequestDispatcher("/jsp/guest/searchResults.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("/jsp/guest/roomSearch.jsp").forward(request, response);
        }
    }

    /**
     * Handle reservation creation
     */
    private void handleReservationCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Parse guest details
            GuestDTO guestDTO = new GuestDTO();
            guestDTO.setName(request.getParameter("name"));
            guestDTO.setNic(request.getParameter("nic"));
            guestDTO.setPhone(request.getParameter("phone"));
            guestDTO.setEmail(request.getParameter("email"));

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String checkIn = request.getParameter("checkIn");
            String checkOut = request.getParameter("checkOut");

            logger.info("Creating reservation for guest: {}, room: {}, dates: {} to {}",
                       guestDTO.getName(), roomId, checkIn, checkOut);

            ControllerResult<ReservationDTO> result = controller.makeReservation(guestDTO, roomId, checkIn, checkOut);

            if (result.isSuccess()) {
                request.setAttribute("reservation", result.getData());
                request.getRequestDispatcher("/jsp/guest/reservationConfirmation.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/guest/reservation.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid room ID format", e);
            request.setAttribute("error", "Invalid input data");
            request.getRequestDispatcher("/jsp/guest/roomSearch.jsp").forward(request, response);
        }
    }

    /**
     * Handle my reservations list
     */
    private void handleMyReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Displaying user's reservations");
        // TODO: Fetch reservations for logged-in guest
        request.getRequestDispatcher("/jsp/guest/myReservations.jsp").forward(request, response);
    }

    /**
     * Handle cancel form display
     */
    private void handleCancelForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationId = request.getParameter("reservationId");
        logger.debug("Displaying cancellation form for reservation: {}", reservationId);

        request.setAttribute("reservationId", reservationId);
        request.getRequestDispatcher("/jsp/guest/cancelReservation.jsp").forward(request, response);
    }

    /**
     * Handle reservation cancellation
     */
    private void handleReservationCancel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationId = request.getParameter("reservationId");
        logger.info("Cancelling reservation: {}", reservationId);

        ControllerResult<Boolean> result = controller.cancelReservation(reservationId);

        if (result.isSuccess()) {
            request.setAttribute("message", "Reservation cancelled successfully");
            request.getRequestDispatcher("/jsp/guest/cancelConfirmation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("reservationId", reservationId);
            request.getRequestDispatcher("/jsp/guest/cancelReservation.jsp").forward(request, response);
        }
    }
}

