package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.User;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.UserRepository;
import com.hotelreservation.util.QueryLogger;
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
    private static final String CLASS_NAME = "UserDAOImpl"; // DEV ONLY - for QueryLogger

    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role FROM " + TABLE_NAME + " WHERE username = ?";
        long start = System.currentTimeMillis(); // DEV ONLY

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
                    QueryLogger.getInstance().logSuccess(sql, "username=" + username, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(user);
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "username=" + username, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "username=" + username, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
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
        long start = System.currentTimeMillis(); // DEV ONLY

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
                    QueryLogger.getInstance().logSuccess(sql, "id=" + id, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(user);
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
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
        String params = "username=" + user.getUsername() + ", role=" + user.getRole(); // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY

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
                        QueryLogger.getInstance().logSuccess(sql, params, rowsAffected, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                        return user;
                    }
                }
            }
            QueryLogger.getInstance().logSuccess(sql, params, rowsAffected, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
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
        String params = "username=" + user.getUsername() + ", id=" + user.getId(); // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            int rowsAffected = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, params, rowsAffected, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
            if (rowsAffected > 0) {
                logger.info("User updated successfully: {}", user.getUsername());
            }
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
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
        long start = System.currentTimeMillis(); // DEV ONLY

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, rowsAffected, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
            if (rowsAffected > 0) {
                logger.info("User deleted successfully with ID: {}", id);
            }
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
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
        long start = System.currentTimeMillis(); // DEV ONLY

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt("count") > 0;
                    QueryLogger.getInstance().logSuccess(sql, "username=" + username, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return exists;
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "username=" + username, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "username=" + username, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error checking if username exists: {}", username, e);
        }
        return false;
    }
}

