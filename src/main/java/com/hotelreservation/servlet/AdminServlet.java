package com.hotelreservation.servlet;

import com.hotelreservation.controller.AdminController;
import com.hotelreservation.controller.AdminController.ControllerResult;
import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.exception.HotelSystemException;
import com.hotelreservation.service.UserService;
import com.hotelreservation.service.PaymentService;
import com.hotelreservation.service.ReportService;
import com.hotelreservation.service.impl.UserServiceImpl;
import com.hotelreservation.service.impl.PaymentServiceImpl;
import com.hotelreservation.service.impl.ReportServiceImpl;
import com.hotelreservation.service.impl.RoomServiceImpl;
import com.hotelreservation.service.impl.SeasonalPricingServiceImpl;
import com.hotelreservation.service.SeasonalPricingService;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.entity.SeasonalPricing;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.repository.impl.UserDAOImpl;
import com.hotelreservation.repository.impl.ReservationDAOImpl;
import com.hotelreservation.repository.impl.RoomDAOImpl;
import com.hotelreservation.repository.impl.SeasonalPricingDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * AdminServlet - Handles admin operations
 *
 * URL Pattern: /admin/*
 * - /admin/dashboard (GET) - Admin dashboard
 * - /admin/staff/create (POST) - Create staff account
 * - /admin/reports/* (GET/POST) - Generate reports
 * - /admin/payment-config (POST) - Configure payment adapter
 * - /admin/maintenance (POST) - Manage maintenance
 * - /admin/seasonal-pricing (GET) - View seasonal pricing config
 * - /admin/seasonal-pricing/create (POST) - Add a new season
 * - /admin/seasonal-pricing/toggle (POST) - Activate/deactivate a season
 * - /admin/seasonal-pricing/delete (POST) - Remove a season
 */
public class AdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private static final long serialVersionUID = 1L;

    private AdminController controller;
    private UserService userService;
    private PaymentService paymentService;
    private ReportService reportService;
    private RoomService roomService;
    private SeasonalPricingService seasonalPricingService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        RoomDAOImpl roomDAO = new RoomDAOImpl();
        userService = new UserServiceImpl(new UserDAOImpl());
        paymentService = new PaymentServiceImpl();
        reportService = new ReportServiceImpl(new ReservationDAOImpl(), roomDAO);
        roomService = new RoomServiceImpl(roomDAO);
        seasonalPricingService = new SeasonalPricingServiceImpl(new SeasonalPricingDAOImpl());

        controller = new AdminController(userService, reportService, paymentService, seasonalPricingService);
        logger.info("AdminServlet initialized");
    }

    /**
     * GET: Display forms and dashboards
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        logger.debug("GET /admin{} - Handling request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo == null || pathInfo.equals("/dashboard")) {
                handleDashboard(request, response);
            } else if (pathInfo.equals("/staff")) {
                handleStaffForm(request, response);
            } else if (pathInfo.equals("/reports")) {
                handleReportsPage(request, response);
            } else if (pathInfo.equals("/payment-config")) {
                handlePaymentConfigForm(request, response);
            } else if (pathInfo.equals("/maintenance")) {
                handleMaintenanceForm(request, response);
            } else if (pathInfo.equals("/seasonal-pricing")) {
                handleSeasonalPricingPage(request, response);
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
        logger.debug("POST /admin{} - Processing request", pathInfo != null ? pathInfo : "");

        try {
            if (pathInfo.equals("/staff/create")) {
                handleStaffCreation(request, response);
            } else if (pathInfo.contains("/reports/")) {
                handleReportGeneration(request, response, pathInfo);
            } else if (pathInfo.equals("/payment-config")) {
                handlePaymentConfiguration(request, response);
            } else if (pathInfo.equals("/maintenance")) {
                handleMaintenanceUpdate(request, response);
            } else if (pathInfo.equals("/seasonal-pricing/create")) {
                handleSeasonCreate(request, response);
            } else if (pathInfo.equals("/seasonal-pricing/toggle")) {
                handleSeasonToggle(request, response);
            } else if (pathInfo.equals("/seasonal-pricing/delete")) {
                handleSeasonDelete(request, response);
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
     * Display admin dashboard
     */
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying admin dashboard");
        request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);
    }

    /**
     * Display staff creation form
     */
    private void handleStaffForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying staff creation form");
        request.getRequestDispatcher("/jsp/admin/staffManagement.jsp").forward(request, response);
    }

    /**
     * Create staff account
     */
    private void handleStaffCreation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        logger.info("Creating staff account for: {} with role: {}", username, role);

        // Validate passwords
        if (!password.equals(confirmPassword)) {
            logger.warn("Password mismatch for staff creation");
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/jsp/admin/staffManagement.jsp").forward(request, response);
            return;
        }

        // Create staff
        UserDTO userDTO = new UserDTO(username, password, role);
        ControllerResult<Boolean> result = controller.createStaffAccount(userDTO);

        if (result.isSuccess()) {
            request.setAttribute("message", "Staff account created successfully");
            request.getRequestDispatcher("/jsp/admin/staffCreationConfirmation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("/jsp/admin/staffManagement.jsp").forward(request, response);
        }
    }

    /**
     * Display reports page
     */
    private void handleReportsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying reports page");
        request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
    }

    /**
     * Generate reports
     */
    private void handleReportGeneration(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {

        if (pathInfo.contains("/revenue")) {
            logger.info("Generating revenue report");
            ControllerResult<String> result = controller.generateRevenueReport();
            if (result.isSuccess()) {
                request.setAttribute("report", result.getData());
                request.getRequestDispatcher("/jsp/admin/revenueReport.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
            }
        } else if (pathInfo.contains("/occupancy")) {
            logger.info("Generating occupancy report");
            ControllerResult<String> result = controller.generateOccupancyReport();
            if (result.isSuccess()) {
                request.setAttribute("report", result.getData());
                request.getRequestDispatcher("/jsp/admin/occupancyReport.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
            }
        } else if (pathInfo.contains("/comprehensive")) {
            logger.info("Generating comprehensive report");
            ControllerResult<String> result = controller.generateComprehensiveReport();
            if (result.isSuccess()) {
                request.setAttribute("report", result.getData());
                request.getRequestDispatcher("/jsp/admin/comprehensiveReport.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
            }
        } else if (pathInfo.contains("/cancellation")) {
            logger.info("Generating cancellation report");
            ControllerResult<String> result = controller.generateCancellationReport();
            if (result.isSuccess()) {
                request.setAttribute("report", result.getData());
                request.getRequestDispatcher("/jsp/admin/cancellationReport.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
            }
        }
    }

    /**
     * Display payment configuration form
     */
    private void handlePaymentConfigForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying payment configuration form");
        request.getRequestDispatcher("/jsp/admin/paymentConfig.jsp").forward(request, response);
    }

    /**
     * Configure payment adapter
     */
    private void handlePaymentConfiguration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String adapterType = request.getParameter("adapter");
        logger.info("Configuring payment adapter: {}", adapterType);

        ControllerResult<Boolean> result = controller.configurePaymentAdapter(adapterType);

        if (result.isSuccess()) {
            request.setAttribute("message", "Payment adapter configured successfully");
            request.getRequestDispatcher("/jsp/admin/paymentConfigConfirmation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("/jsp/admin/paymentConfig.jsp").forward(request, response);
        }
    }

    /**
     * Display maintenance management form with list of dirty/maintenance rooms
     */
    private void handleMaintenanceForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying maintenance management form");
        try {
            // Get all rooms and filter to show dirty ones and under-maintenance ones
            java.util.List<RoomDTO> allRooms = roomService.getAllRooms();
            java.util.List<RoomDTO> dirtyRooms = allRooms.stream()
                    .filter(r -> !r.isClean() || "UNDER_MAINTENANCE".equals(r.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            request.setAttribute("dirtyRooms", dirtyRooms);
            request.setAttribute("allRooms", allRooms);
        } catch (Exception e) {
            logger.warn("Error loading rooms for maintenance form", e);
        }
        request.getRequestDispatcher("/jsp/admin/maintenance.jsp").forward(request, response);
    }

    /**
     * Update maintenance status — mark room as clean or under maintenance
     */
    private void handleMaintenanceUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomIdStr = request.getParameter("roomId");
        String action = request.getParameter("action");

        logger.info("Maintenance update: room={}, action={}", roomIdStr, action);

        try {
            int roomId = Integer.parseInt(roomIdStr);

            if ("markClean".equals(action)) {
                // Maintenance marks room as clean — room becomes bookable again
                roomService.markRoomClean(roomId);
                roomService.updateRoomStatus(roomId, "AVAILABLE");
                request.setAttribute("message", "Room " + roomId + " marked as clean and available for booking.");
            } else if ("markMaintenance".equals(action)) {
                // Put room under maintenance
                roomService.updateRoomStatus(roomId, "UNDER_MAINTENANCE");
                request.setAttribute("message", "Room " + roomId + " placed under maintenance.");
            } else {
                request.setAttribute("error", "Unknown action: " + action);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid room ID");
        } catch (Exception e) {
            logger.error("Error updating maintenance status", e);
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        // Re-load and re-display the maintenance form
        handleMaintenanceForm(request, response);
    }

    // ===================== Seasonal Pricing Handlers =====================

    /**
     * Display seasonal pricing management page with existing seasons
     */
    private void handleSeasonalPricingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying seasonal pricing management page");
        try {
            ControllerResult<List<SeasonalPricing>> result = controller.getAllSeasons();
            if (result.isSuccess()) {
                request.setAttribute("seasons", result.getData());
            }
        } catch (Exception e) {
            logger.warn("Error loading seasonal pricing data", e);
        }
        request.getRequestDispatcher("/jsp/admin/seasonalPricing.jsp").forward(request, response);
    }

    /**
     * Create a new seasonal pricing entry
     */
    private void handleSeasonCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String seasonName = request.getParameter("seasonName");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String multiplierStr = request.getParameter("multiplier");

        logger.info("Creating season: name={}, start={}, end={}, multiplier={}", seasonName, startDateStr, endDateStr, multiplierStr);

        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            double multiplier = Double.parseDouble(multiplierStr);

            ControllerResult<Boolean> result = controller.createSeason(seasonName, startDate, endDate, multiplier);

            if (result.isSuccess()) {
                request.setAttribute("message", result.getMessage());
            } else {
                request.setAttribute("error", result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error creating season", e);
            request.setAttribute("error", "Invalid input: " + e.getMessage());
        }

        // Re-load and re-display the page
        handleSeasonalPricingPage(request, response);
    }

    /**
     * Toggle a season's active/inactive status
     */
    private void handleSeasonToggle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String seasonIdStr = request.getParameter("seasonId");
        String activeStr = request.getParameter("active");

        logger.info("Toggling season: id={}, active={}", seasonIdStr, activeStr);

        try {
            int seasonId = Integer.parseInt(seasonIdStr);
            boolean active = "true".equalsIgnoreCase(activeStr);

            ControllerResult<Boolean> result = controller.toggleSeason(seasonId, active);

            if (result.isSuccess()) {
                request.setAttribute("message", result.getMessage());
            } else {
                request.setAttribute("error", result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error toggling season", e);
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        handleSeasonalPricingPage(request, response);
    }

    /**
     * Delete a seasonal pricing entry
     */
    private void handleSeasonDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String seasonIdStr = request.getParameter("seasonId");
        logger.info("Deleting season: id={}", seasonIdStr);

        try {
            int seasonId = Integer.parseInt(seasonIdStr);
            ControllerResult<Boolean> result = controller.deleteSeason(seasonId);

            if (result.isSuccess()) {
                request.setAttribute("message", result.getMessage());
            } else {
                request.setAttribute("error", result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error deleting season", e);
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        handleSeasonalPricingPage(request, response);
    }
}

