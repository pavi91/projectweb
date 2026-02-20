package com.hotelreservation.repository;

import com.hotelreservation.entity.User;
import java.util.Optional;

/**
 * UserRepository interface - defines contract for user data access
 */
public interface UserRepository {

    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by ID
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(int id);

    /**
     * Save a new user
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);

    /**
     * Update an existing user
     * @param user the user to update
     */
    void update(User user);

    /**
     * Delete a user by ID
     * @param id the user ID to delete
     */
    void delete(int id);

    /**
     * Check if username already exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
}

