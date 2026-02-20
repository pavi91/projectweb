package com.hotelreservation.filter;

import com.hotelreservation.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    // Public URL suffixes (relative to context path, no authentication required)
    private static final Set<String> PUBLIC_PATHS = new HashSet<>();
    static {
        PUBLIC_PATHS.add("/");
        PUBLIC_PATHS.add("");
        PUBLIC_PATHS.add("/login");
        PUBLIC_PATHS.add("/logout");
        PUBLIC_PATHS.add("/jsp/login.jsp");
        PUBLIC_PATHS.add("/jsp/error.jsp");
        PUBLIC_PATHS.add("/jsp/404.jsp");
        PUBLIC_PATHS.add("/jsp/500.jsp");
        PUBLIC_PATHS.add("/index.jsp");
    }

    // Protected URL path prefixes (relative to context path) and their required roles
    private static final String GUEST_PATTERN = "/reservation";
    private static final String DESK_PATTERN = "/frontdesk";
    private static final String ADMIN_PATTERN = "/admin";

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
        String contextPath = request.getContextPath();
        // Get path relative to context root (e.g. "/login", "/reservation/search")
        String path = requestURI.substring(contextPath.length());
        String method = request.getMethod();

        logger.debug("Request: {} {} (path: {})", method, requestURI, path);

        // Allow public URLs without authentication
        if (isPublicUrl(path)) {
            logger.debug("Public URL, allowing access: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Get or validate session
        HttpSession session = request.getSession(false);

        if (session == null) {
            logger.warn("No session found for protected URL: {}", path);
            redirectToLogin(request, response, "Session expired. Please login again.");
            return;
        }

        // Validate session attributes
        Object userIdObj = session.getAttribute("userId");
        Object userRoleObj = session.getAttribute("role");

        if (userIdObj == null || userRoleObj == null) {
            logger.warn("Invalid session attributes for URL: {}", path);
            session.invalidate();
            redirectToLogin(request, response, "Session invalid. Please login again.");
            return;
        }

        String userRole = (String) userRoleObj;

        // Check role-based access
        if (!hasAccessToUrl(path, userRole)) {
            logger.warn("Access denied for user with role {} to URL: {}", userRole, path);
            respondWithAccessDenied(request, response, userRole);
            return;
        }

        // Set user context in request for servlets
        request.setAttribute("userId", userIdObj);
        request.setAttribute("userRole", userRole);
        request.setAttribute("username", session.getAttribute("username"));

        logger.debug("User {} (role: {}) accessing: {}", userIdObj, userRole, path);

        // Allow request to proceed
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }

    /**
     * Check if URL is public (no authentication required)
     * @param path the path relative to context root
     */
    private boolean isPublicUrl(String path) {
        return PUBLIC_PATHS.contains(path) ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".jpg") ||
               path.endsWith(".png") ||
               path.endsWith(".gif") ||
               path.endsWith(".ico");
    }

    /**
     * Check if user has access to requested URL based on role
     * @param path the path relative to context root
     */
    private boolean hasAccessToUrl(String path, String userRole) {
        // Guest URLs
        if (path.startsWith(GUEST_PATTERN)) {
            return "GUEST".equals(userRole);
        }

        // Receptionist URLs
        if (path.startsWith(DESK_PATTERN)) {
            return "RECEPTIONIST".equals(userRole);
        }

        // Admin URLs
        if (path.startsWith(ADMIN_PATTERN)) {
            return "ADMIN".equals(userRole);
        }

        // Dashboard access based on role
        if (path.contains("/dashboard")) {
            return true;
        }

        return false;
    }

    /**
     * Redirect user to login page
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        String contextPath = request.getContextPath();
        logger.info("Redirecting to login: {}", message);
        response.sendRedirect(contextPath + "/login?message=" + encodeUrlParameter(message));
    }

    /**
     * Respond with access denied message
     */
    private void respondWithAccessDenied(HttpServletRequest request, HttpServletResponse response, String userRole) throws IOException {
        String contextPath = request.getContextPath();
        logger.warn("Access denied for role: {}", userRole);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html");
        response.getWriter().println("<h1>Access Denied</h1>");
        response.getWriter().println("<p>Your role (" + userRole + ") does not have access to this page.</p>");
        response.getWriter().println("<a href='" + contextPath + "/'>Go to Home</a>");
    }

    /**
     * Simple URL parameter encoding
     */
    private String encodeUrlParameter(String param) {
        return param.replace(" ", "%20").replace(".", "%2E");
    }
}

