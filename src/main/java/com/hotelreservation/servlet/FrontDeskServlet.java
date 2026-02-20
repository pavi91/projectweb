package com.hotelreservation.servlet;

import com.hotelreservation.controller.FrontDeskController;
import com.hotelreservation.controller.FrontDeskController.ControllerResult;
import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.service.impl.BookingService;
import com.hotelreservation.service.impl.RoomServiceImpl;
import com.hotelreservation.repository.impl.RoomDAOImpl;
import com.hotelreservation.repository.impl.ReservationDAOImpl;
import com.hotelreservation.service.impl.PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FrontDeskServlet - Handles receptionist operations
 *
 * URL Pattern: /frontdesk/*
 * - /frontdesk/dashboard (GET) - Receptionist dashboard
 * - /frontdesk/walkin (POST) - Create walk-in reservation
 * - /frontdesk/checkin (POST) - Check-in guest
 * - /frontdesk/checkout (POST) - Check-out guest
 */
public class FrontDeskServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontDeskServlet.class);
    private static final long serialVersionUID = 1L;

    private FrontDeskController controller;
    private RoomServiceImpl roomService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        roomService = new RoomServiceImpl(new RoomDAOImpl());

        BookingService bookingService = new BookingService(
            null, // OnlineResService
            null, // WalkInResService
            roomService,
            new PaymentServiceImpl(),
            new ReservationDAOImpl()
        );

        controller = new FrontDeskController(bookingService);
        logger.info("FrontDeskServlet initialized");
    }

    /**
     * GET: Display forms and dashboards
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        logger.debug("GET /frontdesk{} - Handling request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo == null || pathInfo.equals("/dashboard")) {
                handleDashboard(request, response);
            } else if (pathInfo.equals("/walkin")) {
                handleWalkInForm(request, response);
            } else if (pathInfo.equals("/checkin")) {
                handleCheckInForm(request, response);
            } else if (pathInfo.equals("/checkout")) {
                handleCheckOutForm(request, response);
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
        logger.debug("POST /frontdesk{} - Processing request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo.equals("/walkin")) {
                handleWalkInCreate(request, response);
            } else if (pathInfo.equals("/checkin")) {
                handleCheckInProcess(request, response);
            } else if (pathInfo.equals("/checkout")) {
                handleCheckOutProcess(request, response);
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
     * Display receptionist dashboard
     */
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying receptionist dashboard");

        // Get stats for dashboard
        try {
            int availableRooms = roomService.getAvailableRooms().size();
            request.setAttribute("availableRooms", availableRooms);
        } catch (Exception e) {
            logger.warn("Error getting available rooms count", e);
        }

        request.getRequestDispatcher("/jsp/receptionist/dashboard.jsp").forward(request, response);
    }

    /**
     * Display walk-in reservation form
     */
    private void handleWalkInForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying walk-in reservation form");
        request.getRequestDispatcher("/jsp/receptionist/walkInForm.jsp").forward(request, response);
    }

    /**
     * Create walk-in reservation
     */
    private void handleWalkInCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Parse guest details
            GuestDTO guestDTO = new GuestDTO();
            guestDTO.setName(request.getParameter("name"));
            guestDTO.setNic(request.getParameter("nic"));
            guestDTO.setPhone(request.getParameter("phone"));

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String checkIn = request.getParameter("checkIn");
            String checkOut = request.getParameter("checkOut");

            logger.info("Creating walk-in reservation for guest: {}, room: {}", guestDTO.getName(), roomId);

            ControllerResult<ReservationDTO> result = controller.makeWalkInReservation(guestDTO, roomId, checkIn, checkOut);

            if (result.isSuccess()) {
                request.setAttribute("reservation", result.getData());
                request.getRequestDispatcher("/jsp/receptionist/walkInConfirmation.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/receptionist/walkInForm.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid input data", e);
            request.setAttribute("error", "Invalid input data");
            request.getRequestDispatcher("/jsp/receptionist/walkInForm.jsp").forward(request, response);
        }
    }

    /**
     * Display check-in form
     */
    private void handleCheckInForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying check-in form");
        request.getRequestDispatcher("/jsp/receptionist/checkIn.jsp").forward(request, response);
    }

    /**
     * Process check-in
     */
    private void handleCheckInProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationId = request.getParameter("reservationId");
        logger.info("Processing check-in for reservation: {}", reservationId);

        ControllerResult<Boolean> result = controller.checkIn(reservationId);

        if (result.isSuccess()) {
            request.setAttribute("message", "Check-in successful");
            request.getRequestDispatcher("/jsp/receptionist/checkInConfirmation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("reservationId", reservationId);
            request.getRequestDispatcher("/jsp/receptionist/checkIn.jsp").forward(request, response);
        }
    }

    /**
     * Display check-out form
     */
    private void handleCheckOutForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying check-out form");
        request.getRequestDispatcher("/jsp/receptionist/checkOut.jsp").forward(request, response);
    }

    /**
     * Process check-out and generate bill
     */
    private void handleCheckOutProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationId = request.getParameter("reservationId");
        logger.info("Processing check-out for reservation: {}", reservationId);

        ControllerResult<ReservationDTO> result = controller.checkOut(reservationId);

        if (result.isSuccess()) {
            request.setAttribute("reservation", result.getData());
            request.setAttribute("bill", calculateBill(result.getData()));
            request.getRequestDispatcher("/jsp/receptionist/bill.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("reservationId", reservationId);
            request.getRequestDispatcher("/jsp/receptionist/checkOut.jsp").forward(request, response);
        }
    }

    /**
     * Calculate bill details
     */
    private BillDetails calculateBill(ReservationDTO reservation) {
        BillDetails bill = new BillDetails();
        bill.setReservationId(reservation.getId());
        bill.setRoomNumber(reservation.getRoom().getNumber());
        bill.setCheckInDate(reservation.getCheckInDate().toString());
        bill.setCheckOutDate(reservation.getCheckOutDate().toString());

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(
            reservation.getCheckInDate(),
            reservation.getCheckOutDate()
        );

        bill.setNights(nights);
        bill.setRatePerNight(reservation.getRoom().getBasePrice());
        bill.setRoomCharges(reservation.getTotalAmount());
        bill.setAdditionalCharges(0);
        bill.setTotal(reservation.getTotalAmount());

        return bill;
    }

    /**
     * Helper class for bill details
     */
    public static class BillDetails {
        private String reservationId;
        private String roomNumber;
        private String checkInDate;
        private String checkOutDate;
        private int nights;
        private double ratePerNight;
        private double roomCharges;
        private double additionalCharges;
        private double total;

        // Getters and Setters
        public String getReservationId() { return reservationId; }
        public void setReservationId(String id) { this.reservationId = id; }

        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String num) { this.roomNumber = num; }

        public String getCheckInDate() { return checkInDate; }
        public void setCheckInDate(String date) { this.checkInDate = date; }

        public String getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(String date) { this.checkOutDate = date; }

        public int getNights() { return nights; }
        public void setNights(int n) { this.nights = n; }

        public double getRatePerNight() { return ratePerNight; }
        public void setRatePerNight(double rate) { this.ratePerNight = rate; }

        public double getRoomCharges() { return roomCharges; }
        public void setRoomCharges(double charges) { this.roomCharges = charges; }

        public double getAdditionalCharges() { return additionalCharges; }
        public void setAdditionalCharges(double charges) { this.additionalCharges = charges; }

        public double getTotal() { return total; }
        public void setTotal(double t) { this.total = t; }
    }
}

