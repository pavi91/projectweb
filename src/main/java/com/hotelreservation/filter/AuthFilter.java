package com.hotelreservation.filter;

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
 * - Allow internal JSP forwards (FORWARD dispatches) without re-checking auth
 */
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    // Public URL suffixes (relative to context path, no authentication required)
    private static final Set<String> PUBLIC_PATHS = new HashSet<>();
    static {
        PUBLIC_PATHS.add("/");
        PUBLIC_PATHS.add("");
        PUBLIC_PATHS.add("/login");
        PUBLIC_PATHS.add("/signup");
        PUBLIC_PATHS.add("/logout");
        PUBLIC_PATHS.add("/jsp/login.jsp");
        PUBLIC_PATHS.add("/jsp/signup.jsp");
        PUBLIC_PATHS.add("/jsp/error.jsp");
        PUBLIC_PATHS.add("/jsp/404.jsp");
        PUBLIC_PATHS.add("/jsp/500.jsp");
        PUBLIC_PATHS.add("/jsp/accessDenied.jsp");
        PUBLIC_PATHS.add("/index.jsp");
        PUBLIC_PATHS.add("/help");
        PUBLIC_PATHS.add("/jsp/help.jsp");
        // DEBUG ONLY - remove before production
        PUBLIC_PATHS.add("/debug/session");
        PUBLIC_PATHS.add("/debug/queries");
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

        // Allow internal forwards (servlet -> JSP) without re-checking authorization.
        // When a servlet forwards to a JSP (e.g., /jsp/admin/dashboard.jsp), the filter
        // fires again with DispatcherType.FORWARD. The original servlet already passed auth,
        // so we allow the forward through.
        if (request.getDispatcherType() == DispatcherType.FORWARD ||
            request.getDispatcherType() == DispatcherType.INCLUDE ||
            request.getDispatcherType() == DispatcherType.ERROR) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        // Get path relative to context root (e.g. "/login", "/reservation/search")
        String path = requestURI.substring(contextPath.length());
        String method = request.getMethod();

        logger.debug("AuthFilter: {} {} (path: {})", method, requestURI, path);

        // Allow public URLs without authentication
        if (isPublicUrl(path)) {
            logger.debug("Public URL, allowing access: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Block direct access to JSP files — users must go through servlets
        if (path.startsWith("/jsp/")) {
            logger.warn("Direct JSP access blocked: {}", path);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
            forwardToAccessDenied(request, response, userRole);
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
     * @param userRole the user's role
     */
    private boolean hasAccessToUrl(String path, String userRole) {
        // Guest URLs — /reservation/*
        if (path.startsWith(GUEST_PATTERN)) {
            return "GUEST".equals(userRole);
        }

        // Receptionist URLs — /frontdesk/*
        if (path.startsWith(DESK_PATTERN)) {
            return "RECEPTIONIST".equals(userRole);
        }

        // Admin URLs — /admin/*
        if (path.startsWith(ADMIN_PATTERN)) {
            return "ADMIN".equals(userRole);
        }

        // Any authenticated user can access root-level protected URLs
        // (e.g., a profile page in the future)
        return true;
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
     * Forward to access denied JSP page with role info
     */
    private void forwardToAccessDenied(HttpServletRequest request, HttpServletResponse response, String userRole) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setAttribute("userRole", userRole);
        request.setAttribute("error", "Your role (" + userRole + ") does not have permission to access this page.");
        request.getRequestDispatcher("/jsp/accessDenied.jsp").forward(request, response);
    }

    /**
     * Simple URL parameter encoding
     */
    private String encodeUrlParameter(String param) {
        return param.replace(" ", "%20").replace(".", "%2E");
    }
}
