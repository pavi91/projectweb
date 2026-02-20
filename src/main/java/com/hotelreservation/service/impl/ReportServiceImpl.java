package com.hotelreservation.service.impl;

import com.hotelreservation.repository.ReservationRepository;
import com.hotelreservation.repository.RoomRepository;
import com.hotelreservation.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * ReportServiceImpl - Implementation of ReportService
 * Generates analytics and reporting data from reservation and room data
 */
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportServiceImpl(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public String getRevenueStats() {
        try {
            double totalRevenue = reservationRepository.getTotalRevenue();
            int completedReservations = reservationRepository.countByStatus("CHECKED_OUT");
            int cancelledReservations = reservationRepository.countByStatus("CANCELLED");

            return formatRevenueReport(
                    "All Time Revenue Report",
                    totalRevenue,
                    completedReservations,
                    cancelledReservations
            );
        } catch (Exception e) {
            logger.error("Error generating revenue stats", e);
            return "Error generating revenue report";
        }
    }

    @Override
    public String getRevenueStatsByDateRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, dateFormatter);
            LocalDate end = LocalDate.parse(endDate, dateFormatter);

            double rangeRevenue = reservationRepository.getRevenueByDateRange(start, end);

            return "Revenue Report: " + startDate + " to " + endDate + "\n" +
                    "Total Revenue: $" + String.format("%.2f", rangeRevenue);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format", e);
            return "Error: Invalid date format. Use yyyy-MM-dd";
        } catch (Exception e) {
            logger.error("Error generating revenue stats by date range", e);
            return "Error generating revenue report";
        }
    }

    @Override
    public String getOccupancyStats() {
        try {
            int totalRooms = roomRepository.findAll().size();
            int occupiedRooms = roomRepository.countByStatus("OCCUPIED");
            int reservedRooms = roomRepository.countByStatus("RESERVED");
            int maintenanceRooms = roomRepository.countByStatus("UNDER_MAINTENANCE");
            int availableRooms = roomRepository.countByStatus("AVAILABLE");

            double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0) / totalRooms : 0;

            StringBuilder report = new StringBuilder();
            report.append("================================\n");
            report.append("   OCCUPANCY STATISTICS\n");
            report.append("================================\n");
            report.append("Total Rooms: ").append(totalRooms).append("\n");
            report.append("Occupied: ").append(occupiedRooms).append("\n");
            report.append("Reserved: ").append(reservedRooms).append("\n");
            report.append("Available: ").append(availableRooms).append("\n");
            report.append("Under Maintenance: ").append(maintenanceRooms).append("\n");
            report.append("--------------------------------\n");
            report.append("Occupancy Rate: ").append(String.format("%.2f%%", occupancyRate)).append("\n");
            report.append("================================\n");

            return report.toString();
        } catch (Exception e) {
            logger.error("Error generating occupancy stats", e);
            return "Error generating occupancy report";
        }
    }

    @Override
    public String getOccupancyStatsByDateRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, dateFormatter);
            LocalDate end = LocalDate.parse(endDate, dateFormatter);

            int totalRooms = roomRepository.findAll().size();
            int reservedCount = 0;

            // For more accurate calculation, would need reservation data for date range
            StringBuilder report = new StringBuilder();
            report.append("Occupancy Report: ").append(startDate).append(" to ").append(endDate).append("\n");
            report.append("Total Rooms: ").append(totalRooms).append("\n");

            return report.toString();
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format", e);
            return "Error: Invalid date format. Use yyyy-MM-dd";
        } catch (Exception e) {
            logger.error("Error generating occupancy stats by date range", e);
            return "Error generating occupancy report";
        }
    }

    @Override
    public String getCancellationStats() {
        try {
            int totalReservations = reservationRepository.findAll().size();
            int cancelledReservations = reservationRepository.countByStatus("CANCELLED");
            double cancellationRate = totalReservations > 0 ? (cancelledReservations * 100.0) / totalReservations : 0;

            StringBuilder report = new StringBuilder();
            report.append("================================\n");
            report.append("   CANCELLATION STATISTICS\n");
            report.append("================================\n");
            report.append("Total Reservations: ").append(totalReservations).append("\n");
            report.append("Cancelled: ").append(cancelledReservations).append("\n");
            report.append("Active: ").append(totalReservations - cancelledReservations).append("\n");
            report.append("--------------------------------\n");
            report.append("Cancellation Rate: ").append(String.format("%.2f%%", cancellationRate)).append("\n");
            report.append("================================\n");

            return report.toString();
        } catch (Exception e) {
            logger.error("Error generating cancellation stats", e);
            return "Error generating cancellation report";
        }
    }

    @Override
    public String getReservationTypeBreakdown() {
        try {
            int onlineReservations = reservationRepository.countByType("ONLINE");
            int walkInReservations = reservationRepository.countByType("WALK_IN");
            int total = onlineReservations + walkInReservations;

            double onlinePercentage = total > 0 ? (onlineReservations * 100.0) / total : 0;
            double walkInPercentage = total > 0 ? (walkInReservations * 100.0) / total : 0;

            StringBuilder report = new StringBuilder();
            report.append("================================\n");
            report.append("   RESERVATION TYPE BREAKDOWN\n");
            report.append("================================\n");
            report.append("Total Reservations: ").append(total).append("\n");
            report.append("Online: ").append(onlineReservations)
                    .append(" (").append(String.format("%.2f%%", onlinePercentage)).append(")\n");
            report.append("Walk-In: ").append(walkInReservations)
                    .append(" (").append(String.format("%.2f%%", walkInPercentage)).append(")\n");
            report.append("================================\n");

            return report.toString();
        } catch (Exception e) {
            logger.error("Error generating reservation type breakdown", e);
            return "Error generating reservation breakdown";
        }
    }

    @Override
    public String getComprehensiveReport() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("\n");
            report.append("████████████████████████████████████\n");
            report.append("  OCEAN VIEW RESORT - COMPREHENSIVE REPORT\n");
            report.append("████████████████████████████████████\n\n");
            report.append(getRevenueStats()).append("\n");
            report.append(getOccupancyStats()).append("\n");
            report.append(getCancellationStats()).append("\n");
            report.append(getReservationTypeBreakdown()).append("\n");
            report.append("████████████████████████████████████\n");
            report.append("Generated: ").append(LocalDate.now()).append("\n");
            report.append("████████████████████████████████████\n");

            return report.toString();
        } catch (Exception e) {
            logger.error("Error generating comprehensive report", e);
            return "Error generating comprehensive report";
        }
    }

    // ===================== Helper Methods =====================

    private String formatRevenueReport(String title, double totalRevenue, int completedRes, int cancelledRes) {
        StringBuilder report = new StringBuilder();
        report.append("================================\n");
        report.append("   ").append(title).append("\n");
        report.append("================================\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("Completed Reservations: ").append(completedRes).append("\n");
        report.append("Cancelled Reservations: ").append(cancelledRes).append("\n");
        report.append("Average Revenue per Reservation: $")
                .append(String.format("%.2f", completedRes > 0 ? totalRevenue / completedRes : 0)).append("\n");
        report.append("================================\n");

        return report.toString();
    }
}

