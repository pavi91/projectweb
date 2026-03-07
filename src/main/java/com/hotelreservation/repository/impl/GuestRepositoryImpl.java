package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.Guest;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.GuestRepository;
import com.hotelreservation.util.QueryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * GuestRepositoryImpl - JDBC implementation for GuestRepository
 */
public class GuestRepositoryImpl implements GuestRepository {
    private static final Logger logger = LoggerFactory.getLogger(GuestRepositoryImpl.class);
    private static final String TABLE_NAME = "guests";
    private static final String CLASS_NAME = "GuestRepositoryImpl"; // DEV ONLY - for QueryLogger

    @Override
    public Optional<Guest> findById(int id) {
        String sql = "SELECT id, user_id, name, nic, phone, email, address, created_at FROM " + TABLE_NAME + " WHERE id = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "id=" + id, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding guest by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guest> findByUserId(int userId) {
        String sql = "SELECT id, user_id, name, nic, phone, email, address, created_at FROM " + TABLE_NAME + " WHERE user_id = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "user_id=" + userId, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "user_id=" + userId, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "user_id=" + userId, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding guest by user_id: {}", userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guest> findByNic(String nic) {
        String sql = "SELECT id, user_id, name, nic, phone, email, address, created_at FROM " + TABLE_NAME + " WHERE nic = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nic);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "nic=" + nic, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "nic=" + nic, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "nic=" + nic, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding guest by NIC: {}", nic, e);
        }
        return Optional.empty();
    }

    @Override
    public Guest save(Guest guest) {
        String sql = "INSERT INTO " + TABLE_NAME + " (user_id, name, nic, phone, email, address) VALUES (?, ?, ?, ?, ?, ?)";
        String params = "user_id=" + guest.getUserId() + ", name=" + guest.getName() + ", nic=" + guest.getNic(); // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (guest.getUserId() > 0) {
                stmt.setInt(1, guest.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, guest.getName());
            stmt.setString(3, guest.getNic());
            stmt.setString(4, guest.getPhone());
            stmt.setString(5, guest.getEmail());
            stmt.setString(6, guest.getAddress());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        guest.setId(keys.getInt(1));
                        logger.info("Guest saved with ID: {}", guest.getId());
                        QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                        return guest;
                    }
                }
            }
            QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error saving guest: {}", guest.getNic(), e);
        }
        return null;
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getInt("id"));
        guest.setUserId(rs.getInt("user_id"));
        guest.setName(rs.getString("name"));
        guest.setNic(rs.getString("nic"));
        guest.setPhone(rs.getString("phone"));
        guest.setEmail(rs.getString("email"));
        guest.setAddress(rs.getString("address"));
        guest.setCreatedAt(rs.getTimestamp("created_at").getTime());
        return guest;
    }

    @Override
    public boolean updateUserId(int guestId, int userId) {
        String sql = "UPDATE " + TABLE_NAME + " SET user_id = ? WHERE id = ?";
        String params = "userId=" + userId + ", guestId=" + guestId; // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, guestId);
            int rows = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
            if (rows > 0) {
                logger.info("Guest {} linked to user {}", guestId, userId);
                return true;
            }
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error updating user_id for guest: {}", guestId, e);
        }
        return false;
    }
}

