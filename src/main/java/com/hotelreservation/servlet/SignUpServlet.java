package com.hotelreservation.servlet;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.Guest;
import com.hotelreservation.repository.impl.GuestRepositoryImpl;
import com.hotelreservation.repository.impl.UserDAOImpl;
import com.hotelreservation.service.UserService;
import com.hotelreservation.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * SignUpServlet - Handles guest self-registration
 * All sign-ups are assigned the GUEST role automatically.
 * Creates both a user account and a guest profile.
 * If the NIC already exists from a walk-in (no user_id), links the existing guest to the new account.
 *
 * URL: /signup
 * GET  → show sign-up form
 * POST → process registration
 */
public class SignUpServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SignUpServlet.class);
    private static final long serialVersionUID = 1L;

    private UserService userService;
    private GuestRepositoryImpl guestRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserServiceImpl(new UserDAOImpl());
        guestRepository = new GuestRepositoryImpl();
        logger.info("SignUpServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String name = request.getParameter("name");
        String nic = request.getParameter("nic");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        logger.info("Sign-up attempt for username: {}", username);

        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Full name is required");
            }
            if (nic == null || nic.trim().isEmpty()) {
                throw new IllegalArgumentException("NIC number is required");
            }
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required");
            }

            // Check if NIC already has a linked user account
            Optional<Guest> existingGuest = guestRepository.findByNic(nic.trim());
            if (existingGuest.isPresent() && existingGuest.get().getUserId() > 0) {
                // NIC exists AND already linked to a user — cannot re-register
                throw new IllegalArgumentException("This NIC is already registered with an account. Please sign in instead.");
            }

            // 1. Create user account with GUEST role
            UserDTO userDTO = new UserDTO(username.trim(), password, "GUEST");
            userService.createUser(userDTO);

            // 2. Get the created user's ID
            UserDTO createdUser = userService.getUserByUsername(username.trim());
            if (createdUser == null) {
                throw new Exception("Failed to retrieve created user account");
            }

            // 3. Handle guest profile
            if (existingGuest.isPresent()) {
                // NIC exists but has no user_id (created via walk-in) — link existing guest to new user
                Guest guest = existingGuest.get();
                boolean linked = guestRepository.updateUserId(guest.getId(), createdUser.getId());
                if (!linked) {
                    throw new Exception("Failed to link your account to existing guest profile");
                }
                logger.info("Linked existing guest {} (NIC={}) to new user {}", guest.getId(), nic, createdUser.getId());
            } else {
                // No existing guest — create a new guest profile
                Guest guest = new Guest();
                guest.setUserId(createdUser.getId());
                guest.setName(name.trim());
                guest.setNic(nic.trim());
                guest.setPhone(phone.trim());
                guest.setEmail(email != null ? email.trim() : null);

                Guest savedGuest = guestRepository.save(guest);
                if (savedGuest == null) {
                    throw new Exception("Failed to create guest profile");
                }
                logger.info("Guest registered successfully: username={}, guestId={}", username, savedGuest.getId());
            }

            // Redirect to login with success message
            response.sendRedirect(request.getContextPath() + "/login?registered=true");

        } catch (IllegalArgumentException e) {
            logger.warn("Sign-up validation error: {}", e.getMessage());
            request.setAttribute("error", e.getMessage());
            preserveFormData(request, username, name, nic, phone, email);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Sign-up error", e);
            String msg = e.getMessage();
            if (msg != null && msg.contains("Username already exists")) {
                request.setAttribute("error", "This username is already taken. Please choose another.");
            } else {
                request.setAttribute("error", "Registration failed: " + msg);
            }
            preserveFormData(request, username, name, nic, phone, email);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
        }
    }

    private void preserveFormData(HttpServletRequest request, String username, String name, String nic, String phone, String email) {
        request.setAttribute("username", username);
        request.setAttribute("name", name);
        request.setAttribute("nic", nic);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
    }
}

