package com.hotelreservation.servlet;

import com.hotelreservation.controller.AdminController;
import com.hotelreservation.controller.AdminController.ControllerResult;
import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.service.UserService;
import com.hotelreservation.service.PaymentService;
import com.hotelreservation.service.ReportService;
import com.hotelreservation.service.impl.UserServiceImpl;
import com.hotelreservation.service.impl.PaymentServiceImpl;
import com.hotelreservation.service.impl.ReportServiceImpl;
import com.hotelreservation.repository.impl.UserDAOImpl;
import com.hotelreservation.repository.impl.ReservationDAOImpl;
import com.hotelreservation.repository.impl.RoomDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AdminServlet - Handles admin operations
 *
 * URL Pattern: /admin/*
 * - /admin/dashboard (GET) - Admin dashboard
 * - /admin/staff/create (POST) - Create staff account
 * - /admin/reports/* (GET/POST) - Generate reports
 * - /admin/payment-config (POST) - Configure payment adapter
 * - /admin/maintenance (POST) - Manage maintenance
 */
public class AdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private static final long serialVersionUID = 1L;

    private AdminController controller;
    private UserService userService;
    private PaymentService paymentService;
    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        userService = new UserServiceImpl(new UserDAOImpl());
        paymentService = new PaymentServiceImpl();
        reportService = new ReportServiceImpl(new ReservationDAOImpl(), new RoomDAOImpl());

        controller = new AdminController(userService, reportService, paymentService);
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
     * Display maintenance management form
     */
    private void handleMaintenanceForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Displaying maintenance management form");
        request.getRequestDispatcher("/jsp/admin/maintenance.jsp").forward(request, response);
    }

    /**
     * Update maintenance status
     */
    private void handleMaintenanceUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomId = request.getParameter("roomId");
        String status = request.getParameter("status");

        logger.info("Updating room {} maintenance status to: {}", roomId, status);

        request.setAttribute("message", "Maintenance status updated successfully");
        request.getRequestDispatcher("/jsp/admin/maintenanceConfirmation.jsp").forward(request, response);
    }
}

