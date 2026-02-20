package com.hotelreservation.controller;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.exception.AuthenticationException;
import com.hotelreservation.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemController - handles authentication for all actors
 * Used by Guest, Receptionist, Admin, and Maintenance Crew for login/logout
 */
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
    private UserService userService;

    public SystemController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authenticate user with username and password
     * @param username the username
     * @param password the password
     * @return AuthResult containing user info and role on success
     */
    public AuthResult login(String username, String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return new AuthResult(false, "Username cannot be empty", null);
            }
            if (password == null || password.trim().isEmpty()) {
                return new AuthResult(false, "Password cannot be empty", null);
            }

            UserDTO user = userService.authenticate(username, password);
            logger.info("Successful login for user: {} (role: {})", username, user.getRole());
            return new AuthResult(true, "Authentication successful", user);
        } catch (AuthenticationException e) {
            logger.warn("Failed login attempt for user: {}", username);
            return new AuthResult(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error during login", e);
            return new AuthResult(false, "Authentication failed: " + e.getMessage(), null);
        }
    }

    /**
     * Logout user (clear session)
     * @return true if logout successful
     */
    public boolean logout() {
        try {
            logger.info("User logout");
            return true;
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return false;
        }
    }

    /**
     * Validate user session
     * @param userId the user ID
     * @return UserDTO if valid, null otherwise
     */
    public UserDTO validateSession(int userId) {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            logger.error("Error validating session for user ID: {}", userId, e);
            return null;
        }
    }

    /**
     * Authentication result wrapper class
     */
    public static class AuthResult {
        private boolean success;
        private String message;
        private UserDTO user;

        public AuthResult(boolean success, String message, UserDTO user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserDTO getUser() {
            return user;
        }

        public String getUserRole() {
            return user != null ? user.getRole() : null;
        }

        @Override
        public String toString() {
            return "AuthResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", user=" + (user != null ? user.getUsername() : "null") +
                    '}';
        }
    }
}

