package com.hotelreservation.filter;

import com.hotelreservation.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * AuthFilter - Authentication and Authorization Filter
 * Intercepts all requests to validate session and enforce role-based access control
 *
 * Responsibilities:
 * - Check if user has valid session
 * - Validate user role for requested URL
 * - Redirect to login if not authenticated
 * - Prevent unauthorized access
 */
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    // Public URLs (no authentication required)
    private static final Set<String> PUBLIC_URLS = new HashSet<>();
    static {
        PUBLIC_URLS.add("/projectweb/");
        PUBLIC_URLS.add("/projectweb/login");
        PUBLIC_URLS.add("/projectweb/jsp/login.jsp");
        PUBLIC_URLS.add("/projectweb/jsp/error.jsp");
        PUBLIC_URLS.add("/projectweb/jsp/404.jsp");
        PUBLIC_URLS.add("/projectweb/jsp/500.jsp");
        PUBLIC_URLS.add("/projectweb/index.jsp");
    }

    // Protected URL patterns and their required roles
    private static final String GUEST_PATTERN = "/projectweb/reservation";
    private static final String DESK_PATTERN = "/projectweb/frontdesk";
    private static final String ADMIN_PATTERN = "/projectweb/admin";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Request: {} {}", method, requestURI);

        // Allow public URLs without authentication
        if (isPublicUrl(requestURI)) {
            logger.debug("Public URL, allowing access: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Get or validate session
        HttpSession session = request.getSession(false);

        if (session == null) {
            logger.warn("No session found for protected URL: {}", requestURI);
            redirectToLogin(response, "Session expired. Please login again.");
            return;
        }

        // Validate session attributes
        Object userIdObj = session.getAttribute("userId");
        Object userRoleObj = session.getAttribute("role");

        if (userIdObj == null || userRoleObj == null) {
            logger.warn("Invalid session attributes for URL: {}", requestURI);
            session.invalidate();
            redirectToLogin(response, "Session invalid. Please login again.");
            return;
        }

        String userRole = (String) userRoleObj;

        // Check role-based access
        if (!hasAccessToUrl(requestURI, userRole)) {
            logger.warn("Access denied for user with role {} to URL: {}", userRole, requestURI);
            respondWithAccessDenied(response, userRole);
            return;
        }

        // Set user context in request for servlets
        request.setAttribute("userId", userIdObj);
        request.setAttribute("userRole", userRole);
        request.setAttribute("username", session.getAttribute("username"));

        logger.debug("User {} (role: {}) accessing: {}", userIdObj, userRole, requestURI);

        // Allow request to proceed
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }

    /**
     * Check if URL is public (no authentication required)
     */
    private boolean isPublicUrl(String requestURI) {
        return PUBLIC_URLS.contains(requestURI) ||
               requestURI.endsWith(".css") ||
               requestURI.endsWith(".js") ||
               requestURI.endsWith(".jpg") ||
               requestURI.endsWith(".png") ||
               requestURI.endsWith(".gif");
    }

    /**
     * Check if user has access to requested URL based on role
     */
    private boolean hasAccessToUrl(String requestURI, String userRole) {
        // Guest URLs
        if (requestURI.contains(GUEST_PATTERN)) {
            return userRole.equals("GUEST");
        }

        // Receptionist URLs
        if (requestURI.contains(DESK_PATTERN)) {
            return userRole.equals("RECEPTIONIST");
        }

        // Admin URLs
        if (requestURI.contains(ADMIN_PATTERN)) {
            return userRole.equals("ADMIN");
        }

        // Dashboard access based on role
        if (requestURI.contains("/dashboard")) {
            return true;
        }

        return false;
    }

    /**
     * Redirect user to login page
     */
    private void redirectToLogin(HttpServletResponse response, String message) throws IOException {
        logger.info("Redirecting to login: {}", message);
        response.sendRedirect("/projectweb/login?message=" + encodeUrlParameter(message));
    }

    /**
     * Respond with access denied message
     */
    private void respondWithAccessDenied(HttpServletResponse response, String userRole) throws IOException {
        logger.warn("Access denied for role: {}", userRole);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html");
        response.getWriter().println("<h1>Access Denied</h1>");
        response.getWriter().println("<p>Your role (" + userRole + ") does not have access to this page.</p>");
        response.getWriter().println("<a href='/projectweb/'>Go to Home</a>");
    }

    /**
     * Simple URL parameter encoding
     */
    private String encodeUrlParameter(String param) {
        return param.replace(" ", "%20").replace(".", "%2E");
    }
}

