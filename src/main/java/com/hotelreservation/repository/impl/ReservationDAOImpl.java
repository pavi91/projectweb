package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.OnlineReservation;
import com.hotelreservation.entity.Reservation;
import com.hotelreservation.entity.WalkInReservation;

import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ReservationDAOImpl - Data Access Object implementation for Reservation entity
 * Handles all database operations for reservations (Online and Walk-In)
 */
public class ReservationDAOImpl implements ReservationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAOImpl.class);
    private static final String TABLE_NAME = "reservations";

    @Override
    public Optional<Reservation> findById(String id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reservation by ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findByGuest(int guestId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE guest_id = ?";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by guest: {}", guestId, e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = ?";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by status: {}", status, e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByRoom(int roomId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE room_id = ?";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by room: {}", roomId, e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE room_id = ? AND status NOT IN ('CANCELLED','CHECKED_OUT')" +
                " AND check_in_date < ? AND check_out_date > ?";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, Date.valueOf(checkOut));
            stmt.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by room and date range", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all reservations", e);
        }
        return reservations;
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (id, guest_id, room_id, check_in_date, check_out_date, total_amount, status, reservation_type, email_sent, receipt_printed, payment_method)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getId());
            stmt.setInt(2, reservation.getGuestId());
            stmt.setInt(3, reservation.getRoomId());
            stmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(6, reservation.getTotalAmount());
            stmt.setString(7, reservation.getStatus());
            stmt.setString(8, reservation.getReservationType());

            boolean emailSent = false;
            boolean receiptPrinted = false;
            if (reservation instanceof OnlineReservation) {
                emailSent = ((OnlineReservation) reservation).isEmailSent();
            } else if (reservation instanceof WalkInReservation) {
                receiptPrinted = ((WalkInReservation) reservation).isReceiptPrinted();
            }
            stmt.setBoolean(9, emailSent);
            stmt.setBoolean(10, receiptPrinted);
            stmt.setString(11, reservation.getPaymentMethod());

            stmt.executeUpdate();
            logger.info("Reservation saved: {}", reservation.getId());
            return reservation;
        } catch (SQLException e) {
            logger.error("Error saving reservation: {}", reservation.getId(), e);
        }
        return null;
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE " + TABLE_NAME +
                " SET status = ?, total_amount = ?, email_sent = ?, receipt_printed = ?, payment_method = ?, updated_at = CURRENT_TIMESTAMP" +
                " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getStatus());
            stmt.setDouble(2, reservation.getTotalAmount());

            boolean emailSent = false;
            boolean receiptPrinted = false;
            if (reservation instanceof OnlineReservation) {
                emailSent = ((OnlineReservation) reservation).isEmailSent();
            } else if (reservation instanceof WalkInReservation) {
                receiptPrinted = ((WalkInReservation) reservation).isReceiptPrinted();
            }
            stmt.setBoolean(3, emailSent);
            stmt.setBoolean(4, receiptPrinted);
            stmt.setString(5, reservation.getPaymentMethod());
            stmt.setString(6, reservation.getId());

            stmt.executeUpdate();
            logger.info("Reservation updated: {}", reservation.getId());
        } catch (SQLException e) {
            logger.error("Error updating reservation: {}", reservation.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            logger.info("Reservation deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting reservation: {}", id, e);
        }
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
            logger.error("Error counting reservations by status: {}", status, e);
        }
        return 0;
    }

    @Override
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM " + TABLE_NAME + " WHERE status = 'CHECKED_OUT'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            logger.error("Error getting total revenue", e);
        }
        return 0;
    }

    @Override
    public double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM " + TABLE_NAME +
                " WHERE status = 'CHECKED_OUT' AND check_in_date >= ? AND check_out_date <= ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("revenue");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting revenue by date range", e);
        }
        return 0;
    }

    @Override
    public int countByType(String type) {
        String sql = "SELECT COUNT(*) AS cnt FROM " + TABLE_NAME + " WHERE reservation_type = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting reservations by type: {}", type, e);
        }
        return 0;
    }

    /**
     * Map a ResultSet row to the appropriate Reservation subclass
     */
    private Reservation mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("reservation_type");
        String id = rs.getString("id");
        int guestId = rs.getInt("guest_id");
        int roomId = rs.getInt("room_id");
        LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
        LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
        double totalAmount = rs.getDouble("total_amount");
        String status = rs.getString("status");
        String paymentMethod = rs.getString("payment_method");

        Reservation reservation;
        if ("ONLINE".equals(type)) {
            OnlineReservation online = new OnlineReservation(id, guestId, roomId, checkIn, checkOut, totalAmount);
            online.setEmailSent(rs.getBoolean("email_sent"));
            reservation = online;
        } else {
            WalkInReservation walkIn = new WalkInReservation(id, guestId, roomId, checkIn, checkOut, totalAmount);
            walkIn.setReceiptPrinted(rs.getBoolean("receipt_printed"));
            reservation = walkIn;
        }
        reservation.setStatus(status);
        reservation.setPaymentMethod(paymentMethod);
        return reservation;
    }
}

