package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.User;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * UserDAOImpl - Data Access Object implementation for User entity
 * Handles all database operations for users
 */
public class UserDAOImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private static final String TABLE_NAME = "users";

    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role FROM " + TABLE_NAME + " WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                    );
                    logger.debug("Found user: {}", username);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        }
        return Optional.empty();
    }

    /**
     * Find a user by ID
     * @param id the user ID
     * @return Optional containing the user if found
     */
    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password_hash, role FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                    );
                    logger.debug("Found user by ID: {}", id);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", id, e);
        }
        return Optional.empty();
    }

    /**
     * Save a new user
     * @param user the user to save
     * @return the saved user with generated ID
     */
    @Override
    public User save(User user) {
        String sql = "INSERT INTO " + TABLE_NAME + " (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                        logger.info("User created successfully: {}", user.getUsername());
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving user: {}", user.getUsername(), e);
        }
        return null;
    }

    /**
     * Update an existing user
     * @param user the user to update
     */
    @Override
    public void update(User user) {
        String sql = "UPDATE " + TABLE_NAME + " SET username = ?, password_hash = ?, role = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User updated successfully: {}", user.getUsername());
            }
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getUsername(), e);
        }
    }

    /**
     * Delete a user by ID
     * @param id the user ID to delete
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User deleted successfully with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting user with ID: {}", id, e);
        }
    }

    /**
     * Check if username already exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) as count FROM " + TABLE_NAME + " WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking if username exists: {}", username, e);
        }
        return false;
    }
}

