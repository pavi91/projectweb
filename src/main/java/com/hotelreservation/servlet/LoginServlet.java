package com.hotelreservation.servlet;

import com.hotelreservation.controller.SystemController;
import com.hotelreservation.controller.SystemController.AuthResult;
import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.service.UserService;
import com.hotelreservation.service.impl.UserServiceImpl;
import com.hotelreservation.repository.impl.UserDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet - Handles user authentication
 *
 * URL Pattern: /login
 * Methods: GET (show login form), POST (process credentials)
 */
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final long serialVersionUID = 1L;

    private SystemController systemController;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize services
        userService = new UserServiceImpl(new UserDAOImpl());
        systemController = new SystemController(userService);
        logger.info("LoginServlet initialized");
    }

    /**
     * GET: Display login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("GET /login - Display login form");

        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            String redirectUrl = getDashboardUrl(role);
            logger.info("User already logged in, redirecting to: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        }

        // Get error message if login failed
        String errorMessage = request.getParameter("message");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        // Forward to login JSP
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    /**
     * POST: Process login credentials
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("POST /login - Login attempt for user: {}", username);

        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            logger.warn("Login attempt with empty credentials");
            response.sendRedirect("/projectweb/login?message=Username%20and%20password%20required");
            return;
        }

        // Attempt authentication
        AuthResult authResult = systemController.login(username, password);

        if (authResult.isSuccess()) {
            logger.info("Successful login for user: {} (role: {})", username, authResult.getUserRole());

            // Create session
            createUserSession(request, response, authResult.getUser());

            // Redirect to appropriate dashboard
            String redirectUrl = getDashboardUrl(authResult.getUserRole());
            response.sendRedirect(redirectUrl);
        } else {
            logger.warn("Failed login attempt for user: {}", username);
            String errorMessage = authResult.getMessage();
            response.sendRedirect("/projectweb/login?message=" + encodeUrlParameter(errorMessage));
        }
    }

    /**
     * Create user session with secure attributes
     */
    private void createUserSession(HttpServletRequest request, HttpServletResponse response, UserDTO user) {
        HttpSession session = request.getSession(true);

        // Set session attributes
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        // Set timeout (30 minutes)
        session.setMaxInactiveInterval(1800);

        logger.debug("Session created for user: {} with role: {}", user.getUsername(), user.getRole());
    }

    /**
     * Get dashboard URL based on user role
     */
    private String getDashboardUrl(String role) {
        switch (role) {
            case "GUEST":
                return "/projectweb/reservation/search";
            case "RECEPTIONIST":
                return "/projectweb/frontdesk/dashboard";
            case "ADMIN":
                return "/projectweb/admin/dashboard";
            case "MAINTENANCE":
                return "/projectweb/maintenance/dashboard";
            default:
                logger.warn("Unknown role for dashboard redirect: {}", role);
                return "/projectweb/";
        }
    }

    /**
     * URL encode parameter
     */
    private String encodeUrlParameter(String param) {
        if (param == null) return "";
        return param.replace(" ", "%20")
                   .replace("&", "%26")
                   .replace(".", "%2E")
                   .replace("!", "%21");
    }
}

