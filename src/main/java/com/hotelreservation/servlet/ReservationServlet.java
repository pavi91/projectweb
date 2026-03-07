package com.hotelreservation.servlet;

import com.hotelreservation.controller.ReservationController;
import com.hotelreservation.controller.ReservationController.ControllerResult;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.service.impl.BookingService;
import com.hotelreservation.service.impl.RoomServiceImpl;
import com.hotelreservation.service.impl.OnlineResService;
import com.hotelreservation.service.impl.WalkInResService;
import com.hotelreservation.repository.impl.UserDAOImpl;
import com.hotelreservation.repository.impl.RoomDAOImpl;
import com.hotelreservation.repository.impl.ReservationDAOImpl;
import com.hotelreservation.service.impl.UserServiceImpl;
import com.hotelreservation.service.impl.PaymentServiceImpl;
import com.hotelreservation.service.impl.ReportServiceImpl;
import com.hotelreservation.service.impl.SeasonalPricingServiceImpl;
import com.hotelreservation.repository.impl.SeasonalPricingDAOImpl;
import com.hotelreservation.entity.Guest;
import com.hotelreservation.exception.HotelSystemException;
import com.hotelreservation.repository.GuestRepository;
import com.hotelreservation.repository.impl.GuestRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    private GuestRepository guestRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        roomService = new RoomServiceImpl(new RoomDAOImpl());
        ReservationDAOImpl reservationDAO = new ReservationDAOImpl();

        // Initialize guest repository for mapping userId -> guestId
        guestRepository = new GuestRepositoryImpl();

        BookingService bookingService = new BookingService(
            new OnlineResService(reservationDAO),
            new WalkInResService(reservationDAO),
            roomService,
            new PaymentServiceImpl(),
            reservationDAO,
            guestRepository,
            new SeasonalPricingServiceImpl(new SeasonalPricingDAOImpl())
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
            if (e instanceof HotelSystemException hse) {
                logger.error("Hotel system error handling GET request: [{}] {}", hse.getErrorCode(), hse.getMessage(), hse);
                request.setAttribute("errorCode", hse.getErrorCode());
                request.setAttribute("statusCode", hse.getStatusCode());
            } else {
                logger.error("Error handling GET request", e);
            }
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
            } else if (pathInfo.equals("/pay")) {
                handlePaymentPage(request, response);
            } else if (pathInfo.equals("/cancel")) {
                handleReservationCancel(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            if (e instanceof HotelSystemException hse) {
                logger.error("Hotel system error handling POST request: [{}] {}", hse.getErrorCode(), hse.getMessage(), hse);
                request.setAttribute("errorCode", hse.getErrorCode());
                request.setAttribute("statusCode", hse.getStatusCode());
            } else {
                logger.error("Error handling POST request", e);
            }
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
     * Show payment page with demo card form before creating reservation
     */
    private void handlePaymentPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String checkIn = request.getParameter("checkIn");
            String checkOut = request.getParameter("checkOut");

            // Load room details for the summary
            RoomDTO room = roomService.getRoomById(roomId);
            if (room != null) {
                request.setAttribute("room", room);
                // Calculate total amount
                long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.parse(checkIn), java.time.LocalDate.parse(checkOut));
                if (nights <= 0) nights = 1;
                double totalAmount = nights * room.getBasePrice();
                request.setAttribute("totalAmount", totalAmount);
            }

            // Pass through all form data
            request.setAttribute("roomId", String.valueOf(roomId));
            request.setAttribute("checkIn", checkIn);
            request.setAttribute("checkOut", checkOut);
            request.setAttribute("name", request.getParameter("name"));
            request.setAttribute("nic", request.getParameter("nic"));
            request.setAttribute("phone", request.getParameter("phone"));
            request.setAttribute("email", request.getParameter("email"));

            request.getRequestDispatcher("/jsp/guest/payment.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading payment page", e);
            request.setAttribute("error", "Error loading payment page: " + e.getMessage());
            request.getRequestDispatcher("/jsp/guest/roomSearch.jsp").forward(request, response);
        }
    }

    /**
     * Handle reservation creation
     */
    private void handleReservationCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get the guest ID from session (set at login time)
            HttpSession session = request.getSession(false);
            GuestDTO guestDTO = new GuestDTO();

            if (session != null && session.getAttribute("guestId") != null) {
                int guestId = (Integer) session.getAttribute("guestId");
                guestDTO.setId(guestId);
                // Use form data for display, but the guestId is what matters for the reservation
                guestDTO.setName(request.getParameter("name"));
                guestDTO.setNic(request.getParameter("nic"));
                guestDTO.setPhone(request.getParameter("phone"));
                guestDTO.setEmail(request.getParameter("email"));
                logger.info("Creating reservation with session guestId={}", guestId);
            } else {
                // Fallback to form data only (e.g. walk-in or no guest profile)
                guestDTO.setName(request.getParameter("name"));
                guestDTO.setNic(request.getParameter("nic"));
                guestDTO.setPhone(request.getParameter("phone"));
                guestDTO.setEmail(request.getParameter("email"));
            }

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

        logger.debug("Displaying my reservations");

        // Get guestId from session (set at login time)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("guestId") == null) {
            logger.warn("No guest profile in session for /reservation/list");
            response.sendRedirect(request.getContextPath() + "/login?message=Please%20log%20in%20to%20view%20your%20reservations");
            return;
        }

        int guestId = (Integer) session.getAttribute("guestId");

        ControllerResult<List<ReservationDTO>> result = controller.listReservationsForGuest(guestId);

        if (result.isSuccess()) {
            request.setAttribute("reservations", result.getData());
        } else {
            request.setAttribute("error", result.getMessage());
        }

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

