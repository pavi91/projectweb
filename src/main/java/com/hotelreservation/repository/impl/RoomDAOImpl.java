package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.Room;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.RoomRepository;

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

    @Override
    public Optional<Room> findById(int id) {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
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
                "WHERE r.status = 'AVAILABLE' AND r.id NOT IN (" +
                "  SELECT res.room_id FROM reservations res " +
                "  WHERE res.status NOT IN ('CANCELLED','CHECKED_OUT') " +
                "  AND res.check_in_date < ? AND res.check_out_date > ?" +
                ")";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(checkOut));
            stmt.setDate(2, Date.valueOf(checkIn));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding available rooms for date range {} to {}", checkIn, checkOut, e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(String status) {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE status = ?";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding rooms by status: {}", status, e);
        }
        return rooms;
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME;
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all rooms", e);
        }
        return rooms;
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO " + TABLE_NAME + " (number, type, base_price, status, is_clean) VALUES (?, ?, ?, ?, ?)";
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
                        return room;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving room: {}", room.getNumber(), e);
        }
        return null;
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE " + TABLE_NAME + " SET number = ?, type = ?, base_price = ?, status = ?, is_clean = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType());
            stmt.setDouble(3, room.getBasePrice());
            stmt.setString(4, room.getStatus());
            stmt.setBoolean(5, room.isClean());
            stmt.setInt(6, room.getId());
            stmt.executeUpdate();
            logger.info("Room updated: {}", room.getNumber());
        } catch (SQLException e) {
            logger.error("Error updating room: {}", room.getNumber(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Room deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting room: {}", id, e);
        }
    }

    @Override
    public Optional<Room> findByNumber(String number) {
        String sql = "SELECT id, number, type, base_price, status, is_clean FROM " + TABLE_NAME + " WHERE number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding room by number: {}", number, e);
        }
        return Optional.empty();
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM " + TABLE_NAME + " WHERE status = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
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

