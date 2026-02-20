package com.hotelreservation.service;

/**
 * ReportService interface - defines contract for analytics and reporting
 */
public interface ReportService {

    /**
     * Get revenue statistics for the entire hotel
     * @return formatted revenue report string
     */
    String getRevenueStats();

    /**
     * Get revenue statistics for a date range
     * @param startDate start date in format "yyyy-MM-dd"
     * @param endDate end date in format "yyyy-MM-dd"
     * @return formatted revenue report string
     */
    String getRevenueStatsByDateRange(String startDate, String endDate);

    /**
     * Get occupancy statistics
     * @return formatted occupancy report string
     */
    String getOccupancyStats();

    /**
     * Get occupancy statistics for a date range
     * @param startDate start date in format "yyyy-MM-dd"
     * @param endDate end date in format "yyyy-MM-dd"
     * @return formatted occupancy report string
     */
    String getOccupancyStatsByDateRange(String startDate, String endDate);

    /**
     * Get cancellation statistics
     * @return formatted cancellation report string
     */
    String getCancellationStats();

    /**
     * Get reservation type breakdown (online vs walk-in)
     * @return formatted breakdown report string
     */
    String getReservationTypeBreakdown();

    /**
     * Get comprehensive hotel statistics report
     * @return formatted comprehensive report string
     */
    String getComprehensiveReport();
}

