package com.hotelreservation.controller;

import com.hotelreservation.adapter.IPaymentAdapter;
import com.hotelreservation.adapter.OnlineGatewayAdapter;
import com.hotelreservation.adapter.POSAdapter;
import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.SeasonalPricing;
import com.hotelreservation.service.PaymentService;
import com.hotelreservation.service.ReportService;
import com.hotelreservation.service.SeasonalPricingService;
import com.hotelreservation.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * AdminController - handles admin operations
 * Used by Admin actors for staff management, reports, and payment configuration
 */
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private UserService userService;
    private ReportService reportService;
    private PaymentService paymentService;
    private SeasonalPricingService seasonalPricingService;

    public AdminController(UserService userService, ReportService reportService, PaymentService paymentService) {
        this.userService = userService;
        this.reportService = reportService;
        this.paymentService = paymentService;
        this.seasonalPricingService = null;
    }

    public AdminController(UserService userService, ReportService reportService, PaymentService paymentService, SeasonalPricingService seasonalPricingService) {
        this.userService = userService;
        this.reportService = reportService;
        this.paymentService = paymentService;
        this.seasonalPricingService = seasonalPricingService;
    }

    /**
     * Create a new staff account
     * @param userDTO staff account details (role: RECEPTIONIST)
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> createStaffAccount(UserDTO userDTO) {
        try {
            if (userDTO == null) {
                return new ControllerResult<>(false, "User details required", false);
            }

            if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
                return new ControllerResult<>(false, "Username cannot be empty", false);
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return new ControllerResult<>(false, "Password cannot be empty", false);
            }

            if (userDTO.getRole() == null || userDTO.getRole().trim().isEmpty()) {
                return new ControllerResult<>(false, "Role cannot be empty", false);
            }

            // Validate role
            if (!isValidStaffRole(userDTO.getRole())) {
                return new ControllerResult<>(false, "Invalid role: " + userDTO.getRole(), false);
            }

            userService.createUser(userDTO);
            logger.info("Staff account created: {} (role: {})", userDTO.getUsername(), userDTO.getRole());

            return new ControllerResult<>(true, "Staff account created successfully", true);
        } catch (Exception e) {
            logger.error("Error creating staff account", e);
            return new ControllerResult<>(false, "Account creation failed: " + e.getMessage(), false);
        }
    }

    /**
     * Generate revenue report
     * @return ControllerResult with formatted report
     */
    public ControllerResult<String> generateRevenueReport() {
        try {
            String report = reportService.getRevenueStats();
            logger.info("Revenue report generated");
            return new ControllerResult<>(true, "Report generated successfully", report);
        } catch (Exception e) {
            logger.error("Error generating revenue report", e);
            return new ControllerResult<>(false, "Report generation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Generate occupancy report
     * @return ControllerResult with formatted report
     */
    public ControllerResult<String> generateOccupancyReport() {
        try {
            String report = reportService.getOccupancyStats();
            logger.info("Occupancy report generated");
            return new ControllerResult<>(true, "Report generated successfully", report);
        } catch (Exception e) {
            logger.error("Error generating occupancy report", e);
            return new ControllerResult<>(false, "Report generation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Generate comprehensive hotel report
     * @return ControllerResult with formatted report
     */
    public ControllerResult<String> generateComprehensiveReport() {
        try {
            String report = reportService.getComprehensiveReport();
            logger.info("Comprehensive report generated");
            return new ControllerResult<>(true, "Report generated successfully", report);
        } catch (Exception e) {
            logger.error("Error generating comprehensive report", e);
            return new ControllerResult<>(false, "Report generation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Generate cancellation statistics
     * @return ControllerResult with formatted report
     */
    public ControllerResult<String> generateCancellationReport() {
        try {
            String report = reportService.getCancellationStats();
            logger.info("Cancellation report generated");
            return new ControllerResult<>(true, "Report generated successfully", report);
        } catch (Exception e) {
            logger.error("Error generating cancellation report", e);
            return new ControllerResult<>(false, "Report generation failed: " + e.getMessage(), null);
        }
    }

    /**
     * Configure payment adapter (switch between POS and Online Gateway)
     * @param adapterType "POS" or "ONLINE_GATEWAY"
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> configurePaymentAdapter(String adapterType) {
        try {
            if (adapterType == null || adapterType.trim().isEmpty()) {
                return new ControllerResult<>(false, "Adapter type required", false);
            }

            IPaymentAdapter adapter = null;
            if ("POS".equalsIgnoreCase(adapterType)) {
                adapter = new POSAdapter();
                logger.info("Switched payment adapter to: POS");
            } else if ("ONLINE_GATEWAY".equalsIgnoreCase(adapterType)) {
                adapter = new OnlineGatewayAdapter();
                logger.info("Switched payment adapter to: ONLINE_GATEWAY");
            } else {
                return new ControllerResult<>(false, "Invalid adapter type: " + adapterType, false);
            }

            paymentService.setPaymentAdapter(adapter);
            logger.info("Payment adapter configured: {}", adapterType);

            return new ControllerResult<>(true, "Payment adapter configured successfully", true);
        } catch (Exception e) {
            logger.error("Error configuring payment adapter", e);
            return new ControllerResult<>(false, "Configuration failed: " + e.getMessage(), false);
        }
    }

    /**
     * Get current payment adapter configuration
     * @return ControllerResult with adapter information
     */
    public ControllerResult<String> getPaymentAdapterStatus() {
        try {
            IPaymentAdapter adapter = paymentService.getCurrentAdapter();
            String status = adapter != null ? adapter.getAdapterName() : "NONE";
            return new ControllerResult<>(true, "Current adapter: " + status, status);
        } catch (Exception e) {
            logger.error("Error getting payment adapter status", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), null);
        }
    }

    /**
     * Get list of staff accounts
     * @return ControllerResult with list of staff usernames
     */
    public ControllerResult<String> getStaffList() {
        try {
            // In production, this would query all RECEPTIONIST users
            logger.info("Retrieved staff list");
            return new ControllerResult<>(true, "Staff list retrieved", "See database for details");
        } catch (Exception e) {
            logger.error("Error retrieving staff list", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), null);
        }
    }

    // ===================== Seasonal Pricing Methods =====================

    /**
     * Get all seasonal pricing entries
     * @return ControllerResult with list of SeasonalPricing
     */
    public ControllerResult<List<SeasonalPricing>> getAllSeasons() {
        try {
            if (seasonalPricingService == null) {
                return new ControllerResult<>(false, "Seasonal pricing service not configured", null);
            }
            List<SeasonalPricing> seasons = seasonalPricingService.getAllSeasons();
            return new ControllerResult<>(true, "Seasons retrieved", seasons);
        } catch (Exception e) {
            logger.error("Error retrieving seasons", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), null);
        }
    }

    /**
     * Create a new seasonal pricing entry
     * @param seasonName name of the season
     * @param startDate season start date
     * @param endDate season end date
     * @param multiplier pricing multiplier
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> createSeason(String seasonName, LocalDate startDate, LocalDate endDate, double multiplier) {
        try {
            if (seasonalPricingService == null) {
                return new ControllerResult<>(false, "Seasonal pricing service not configured", false);
            }
            if (seasonName == null || seasonName.trim().isEmpty()) {
                return new ControllerResult<>(false, "Season name is required", false);
            }
            if (startDate == null || endDate == null) {
                return new ControllerResult<>(false, "Start and end dates are required", false);
            }
            if (endDate.isBefore(startDate)) {
                return new ControllerResult<>(false, "End date must be after start date", false);
            }
            if (multiplier <= 0) {
                return new ControllerResult<>(false, "Multiplier must be greater than 0", false);
            }

            SeasonalPricing pricing = new SeasonalPricing(seasonName, startDate, endDate, multiplier);
            SeasonalPricing saved = seasonalPricingService.createSeason(pricing);

            if (saved != null && saved.getId() > 0) {
                logger.info("Seasonal pricing created: {} ({}x)", seasonName, multiplier);
                return new ControllerResult<>(true, "Season '" + seasonName + "' created successfully with " + multiplier + "x multiplier", true);
            }
            return new ControllerResult<>(false, "Failed to save season", false);
        } catch (Exception e) {
            logger.error("Error creating season", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), false);
        }
    }

    /**
     * Toggle a season's active status
     * @param seasonId the season ID
     * @param active new active status
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> toggleSeason(int seasonId, boolean active) {
        try {
            if (seasonalPricingService == null) {
                return new ControllerResult<>(false, "Seasonal pricing service not configured", false);
            }
            SeasonalPricing season = seasonalPricingService.getSeasonById(seasonId);
            if (season == null) {
                return new ControllerResult<>(false, "Season not found", false);
            }
            season.setActive(active);
            seasonalPricingService.updateSeason(season);
            String status = active ? "activated" : "deactivated";
            logger.info("Season {} {}: {}", seasonId, status, season.getSeasonName());
            return new ControllerResult<>(true, "Season '" + season.getSeasonName() + "' " + status, true);
        } catch (Exception e) {
            logger.error("Error toggling season", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), false);
        }
    }

    /**
     * Delete a seasonal pricing entry
     * @param seasonId the season ID to delete
     * @return ControllerResult indicating success/failure
     */
    public ControllerResult<Boolean> deleteSeason(int seasonId) {
        try {
            if (seasonalPricingService == null) {
                return new ControllerResult<>(false, "Seasonal pricing service not configured", false);
            }
            seasonalPricingService.deleteSeason(seasonId);
            logger.info("Season deleted: id={}", seasonId);
            return new ControllerResult<>(true, "Season deleted successfully", true);
        } catch (Exception e) {
            logger.error("Error deleting season", e);
            return new ControllerResult<>(false, "Error: " + e.getMessage(), false);
        }
    }

    // ===================== Helper Methods =====================

    private boolean isValidStaffRole(String role) {
        return "RECEPTIONIST".equalsIgnoreCase(role);
    }

    /**
     * Generic result wrapper for controller responses
     */
    public static class ControllerResult<T> {
        private boolean success;
        private String message;
        private T data;

        public ControllerResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }

        @Override
        public String toString() {
            return "ControllerResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

