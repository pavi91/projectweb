package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.Room;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.RoomRepository;
import com.hotelreservation.util.QueryLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RoomDAOImpl - Data Access Object implementation for Room entity
 * Handles all database operations for rooms
 */
public class RoomDAOImpl implements RoomRepository {
    private static final Logger logger = LoggerFactory.getLogger(RoomDAOImpl.class);
    private static final String TABLE_NAME = "rooms";
    private static final String CLASS_NAME = "RoomDAOImpl"; // DEV ONLY - for QueryLogger

    @Override
    public Optional<Room> findById(int id) {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE id = ?";
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
            logger.error("Error finding room by ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAvailable() {
        return findByStatus("AVAILABLE");
    }

    @Override
    public List<Room> findAvailableByDateRange(LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT r.id, r.number, r.type, r.base_price, r.status, r.is_clean FROM " + TABLE_NAME + " r " +
                "WHERE r.status = 'AVAILABLE' AND r.is_clean = TRUE AND r.id NOT IN (" +
                "  SELECT res.room_id FROM reservations res " +
                "  WHERE res.status NOT IN ('CANCELLED','CHECKED_OUT') " +
                "  AND res.check_in_date < ? AND res.check_out_date > ?" +
                ")";
        List<Room> rooms = new ArrayList<>();
        long start = System.currentTimeMillis(); // DEV ONLY
        String params = "checkOut=" + checkOut + ", checkIn=" + checkIn; // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(checkOut));
            stmt.setDate(2, Date.valueOf(checkIn));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, params, rooms.size(), System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding available rooms for date range {} to {}", checkIn, checkOut, e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(String status) {
        // For AVAILABLE status, also require the room to be clean (maintained)
        String sql;
        if ("AVAILABLE".equals(status)) {
            sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE status = ? AND is_clean = TRUE";
        } else {
            sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE status = ?";
        }
        List<Room> rooms = new ArrayList<>();
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "status=" + status, rooms.size(), System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "status=" + status, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding rooms by status: {}", status, e);
        }
        return rooms;
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME;
        List<Room> rooms = new ArrayList<>();
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
            QueryLogger.getInstance().logSuccess(sql, "(none)", rooms.size(), System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "(none)", System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding all rooms", e);
        }
        return rooms;
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO " + TABLE_NAME + " (number, type, base_price, status, is_clean) VALUES (?, ?, ?, ?, ?)";
        String params = "number=" + room.getNumber() + ", type=" + room.getType() + ", price=" + room.getBasePrice(); // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType());
            stmt.setDouble(3, room.getBasePrice());
            stmt.setString(4, room.getStatus());
            stmt.setBoolean(5, room.isClean());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        room.setId(keys.getInt(1));
                        logger.info("Room saved: {}", room.getNumber());
                        QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                        return room;
                    }
                }
            }
            QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error saving room: {}", room.getNumber(), e);
        }
        return null;
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE " + TABLE_NAME + " SET number = ?, type = ?, base_price = ?, status = ?, is_clean = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String params = "number=" + room.getNumber() + ", status=" + room.getStatus() + ", id=" + room.getId(); // DEV ONLY
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType());
            stmt.setDouble(3, room.getBasePrice());
            stmt.setString(4, room.getStatus());
            stmt.setBoolean(5, room.isClean());
            stmt.setInt(6, room.getId());
            int rows = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, params, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
            logger.info("Room updated: {}", room.getNumber());
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, params, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error updating room: {}", room.getNumber(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, rows, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
            logger.info("Room deleted: {}", id);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error deleting room: {}", id, e);
        }
    }

    @Override
    public Optional<Room> findByNumber(String number) {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE number = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "number=" + number, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "number=" + number, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "number=" + number, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error finding room by number: {}", number, e);
        }
        return Optional.empty();
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM " + TABLE_NAME + " WHERE status = ?";
        long start = System.currentTimeMillis(); // DEV ONLY
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("cnt");
                    QueryLogger.getInstance().logSuccess(sql, "status=" + status, 1, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
                    return count;
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "status=" + status, 0, System.currentTimeMillis() - start, CLASS_NAME); // DEV ONLY
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "status=" + status, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME); // DEV ONLY
            logger.error("Error counting rooms by status: {}", status, e);
        }
        return 0;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("id"),
            rs.getString("number"),
            rs.getString("type"),
            rs.getDouble("base_price"),
            rs.getString("status"),
            rs.getBoolean("is_clean")
        );
    }
}

