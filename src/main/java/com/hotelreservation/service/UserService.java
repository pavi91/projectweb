package com.hotelreservation.service;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.exception.AuthenticationException;

/**
 * UserService interface - defines contract for user authentication and management
 */
public interface UserService {

    /**
     * Authenticate a user with username and password
     * @param username the username
     * @param password the plain text password
     * @return UserDTO if authentication successful
     * @throws AuthenticationException if authentication fails
     */
    UserDTO authenticate(String username, String password) throws AuthenticationException;

    /**
     * Create a new user account
     * @param userDTO the user data
     * @throws Exception if creation fails (duplicate username, invalid data, etc.)
     */
    void createUser(UserDTO userDTO) throws Exception;

    /**
     * Get user by ID
     * @param userId the user ID
     * @return UserDTO if found
     */
    UserDTO getUserById(int userId);

    /**
     * Get user by username
     * @param username the username
     * @return UserDTO if found
     */
    UserDTO getUserByUsername(String username);

    /**
     * Update user details
     * @param userDTO the updated user data
     */
    void updateUser(UserDTO userDTO);

    /**
     * Delete a user
     * @param userId the user ID to delete
     */
    void deleteUser(int userId);

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean userExists(String username);
}

