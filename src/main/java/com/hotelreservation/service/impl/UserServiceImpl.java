package com.hotelreservation.service.impl;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.User;
import com.hotelreservation.exception.AuthenticationException;
import com.hotelreservation.mapper.UserMapper;
import com.hotelreservation.repository.UserRepository;
import com.hotelreservation.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserServiceImpl - Implementation of UserService
 * Handles user authentication and account management
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO authenticate(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Authentication attempt with empty username");
            throw new AuthenticationException("Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Authentication attempt with empty password");
            throw new AuthenticationException("Password cannot be empty");
        }

        try {
            // Find user by username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AuthenticationException("User not found: " + username));

            // Validate password
            if (!user.validatePassword(password)) {
                logger.warn("Failed authentication attempt for user: {}", username);
                throw new AuthenticationException("Invalid username or password");
            }

            logger.info("User authenticated successfully: {} (role: {})", username, user.getRole());
            return UserMapper.toDTO(user);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void createUser(UserDTO userDTO) throws Exception {
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }

        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (userDTO.getRole() == null || userDTO.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            logger.warn("Attempt to create user with existing username: {}", userDTO.getUsername());
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }

        try {
            // Map DTO to entity and hash password
            User user = UserMapper.toEntity(userDTO);

            // Save to repository
            User savedUser = userRepository.save(user);
            if (savedUser == null) {
                throw new Exception("Failed to save user to database");
            }

            logger.info("User created successfully: {} (role: {})", userDTO.getUsername(), userDTO.getRole());
        } catch (Exception e) {
            logger.error("Error creating user: {}", userDTO.getUsername(), e);
            throw e;
        }
    }

    @Override
    public UserDTO getUserById(int userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElse(null);
            if (user != null) {
                logger.debug("Retrieved user by ID: {}", userId);
                return UserMapper.toDTO(user);
            }
            logger.debug("User not found with ID: {}", userId);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", userId, e);
            return null;
        }
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user != null) {
                logger.debug("Retrieved user by username: {}", username);
                return UserMapper.toDTO(user);
            }
            logger.debug("User not found with username: {}", username);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving user by username: {}", username, e);
            return null;
        }
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() <= 0) {
            logger.warn("Invalid user DTO for update");
            return;
        }

        try {
            User user = userRepository.findById(userDTO.getId())
                    .orElse(null);
            if (user == null) {
                logger.warn("User not found for update: {}", userDTO.getId());
                return;
            }

            // Update user fields
            UserMapper.updateEntity(user, userDTO);
            userRepository.update(user);

            logger.info("User updated successfully: {}", userDTO.getUsername());
        } catch (Exception e) {
            logger.error("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(int userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("User not found for deletion: {}", userId);
                return;
            }

            userRepository.delete(userId);
            logger.info("User deleted successfully: {} (ID: {})", user.getUsername(), userId);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
        }
    }

    @Override
    public boolean userExists(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            logger.error("Error checking if user exists: {}", username, e);
            return false;
        }
    }
}

