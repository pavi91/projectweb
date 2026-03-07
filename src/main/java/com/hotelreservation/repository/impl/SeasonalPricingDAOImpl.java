package com.hotelreservation.repository.impl;

import com.hotelreservation.entity.SeasonalPricing;
import com.hotelreservation.persistence.DatabaseConnection;
import com.hotelreservation.repository.SeasonalPricingRepository;
import com.hotelreservation.util.QueryLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SeasonalPricingDAOImpl - Data Access Object implementation for SeasonalPricing entity
 * Handles all database operations for seasonal pricing configuration
 */
public class SeasonalPricingDAOImpl implements SeasonalPricingRepository {
    private static final Logger logger = LoggerFactory.getLogger(SeasonalPricingDAOImpl.class);
    private static final String TABLE_NAME = "seasonal_pricing";
    private static final String CLASS_NAME = "SeasonalPricingDAOImpl";

    @Override
    public Optional<SeasonalPricing> findById(int id) {
        String sql = "SELECT id, season_name, start_date, end_date, multiplier, is_active FROM " + TABLE_NAME + " WHERE id = ?";
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "id=" + id, 1, System.currentTimeMillis() - start, CLASS_NAME);
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, 0, System.currentTimeMillis() - start, CLASS_NAME);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error finding seasonal pricing by ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SeasonalPricing> findAll() {
        String sql = "SELECT id, season_name, start_date, end_date, multiplier, is_active FROM " + TABLE_NAME + " ORDER BY start_date";
        List<SeasonalPricing> results = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            QueryLogger.getInstance().logSuccess(sql, "", results.size(), System.currentTimeMillis() - start, CLASS_NAME);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "", System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error finding all seasonal pricing entries", e);
        }
        return results;
    }

    @Override
    public List<SeasonalPricing> findActive() {
        String sql = "SELECT id, season_name, start_date, end_date, multiplier, is_active FROM " + TABLE_NAME + " WHERE is_active = TRUE ORDER BY start_date";
        List<SeasonalPricing> results = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            QueryLogger.getInstance().logSuccess(sql, "active=true", results.size(), System.currentTimeMillis() - start, CLASS_NAME);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "active=true", System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error finding active seasonal pricing entries", e);
        }
        return results;
    }

    @Override
    public Optional<SeasonalPricing> findByDate(LocalDate checkInDate) {
        String sql = "SELECT id, season_name, start_date, end_date, multiplier, is_active FROM " + TABLE_NAME +
                " WHERE is_active = TRUE AND start_date <= ? AND end_date >= ? ORDER BY multiplier DESC LIMIT 1";
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(checkInDate));
            stmt.setDate(2, Date.valueOf(checkInDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QueryLogger.getInstance().logSuccess(sql, "date=" + checkInDate, 1, System.currentTimeMillis() - start, CLASS_NAME);
                    return Optional.of(mapRow(rs));
                }
            }
            QueryLogger.getInstance().logSuccess(sql, "date=" + checkInDate, 0, System.currentTimeMillis() - start, CLASS_NAME);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "date=" + checkInDate, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error finding seasonal pricing for date: {}", checkInDate, e);
        }
        return Optional.empty();
    }

    @Override
    public SeasonalPricing save(SeasonalPricing pricing) {
        String sql = "INSERT INTO " + TABLE_NAME + " (season_name, start_date, end_date, multiplier, is_active) VALUES (?, ?, ?, ?, ?)";
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pricing.getSeasonName());
            stmt.setDate(2, Date.valueOf(pricing.getStartDate()));
            stmt.setDate(3, Date.valueOf(pricing.getEndDate()));
            stmt.setDouble(4, pricing.getMultiplier());
            stmt.setBoolean(5, pricing.isActive());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        pricing.setId(keys.getInt(1));
                    }
                }
            }
            QueryLogger.getInstance().logSuccess(sql, pricing.getSeasonName(), rows, System.currentTimeMillis() - start, CLASS_NAME);
            logger.info("Saved seasonal pricing: {}", pricing);
            return pricing;
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, pricing.getSeasonName(), System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error saving seasonal pricing", e);
            return null;
        }
    }

    @Override
    public void update(SeasonalPricing pricing) {
        String sql = "UPDATE " + TABLE_NAME + " SET season_name = ?, start_date = ?, end_date = ?, multiplier = ?, is_active = ? WHERE id = ?";
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pricing.getSeasonName());
            stmt.setDate(2, Date.valueOf(pricing.getStartDate()));
            stmt.setDate(3, Date.valueOf(pricing.getEndDate()));
            stmt.setDouble(4, pricing.getMultiplier());
            stmt.setBoolean(5, pricing.isActive());
            stmt.setInt(6, pricing.getId());
            int rows = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, "id=" + pricing.getId(), rows, System.currentTimeMillis() - start, CLASS_NAME);
            logger.info("Updated seasonal pricing: {}", pricing);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + pricing.getId(), System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error updating seasonal pricing: {}", pricing.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        long start = System.currentTimeMillis();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            QueryLogger.getInstance().logSuccess(sql, "id=" + id, rows, System.currentTimeMillis() - start, CLASS_NAME);
            logger.info("Deleted seasonal pricing: id={}", id);
        } catch (SQLException e) {
            QueryLogger.getInstance().logError(sql, "id=" + id, System.currentTimeMillis() - start, e.getMessage(), CLASS_NAME);
            logger.error("Error deleting seasonal pricing: {}", id, e);
        }
    }

    /**
     * Map a ResultSet row to a SeasonalPricing entity
     */
    private SeasonalPricing mapRow(ResultSet rs) throws SQLException {
        return new SeasonalPricing(
            rs.getInt("id"),
            rs.getString("season_name"),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getDouble("multiplier"),
            rs.getBoolean("is_active")
        );
    }
}

