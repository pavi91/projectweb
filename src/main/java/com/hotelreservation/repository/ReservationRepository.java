package com.hotelreservation.repository;

import com.hotelreservation.entity.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ReservationRepository interface - defines contract for reservation data access
 */
public interface ReservationRepository {

    /**
     * Find a reservation by ID
     * @param id the reservation ID
     * @return Optional containing the reservation if found
     */
    Optional<Reservation> findById(String id);

    /**
     * Find all reservations for a guest
     * @param guestId the guest ID
     * @return list of reservations for the guest
     */
    List<Reservation> findByGuest(int guestId);

    /**
     * Find reservations by status
     * @param status the reservation status (PENDING, CONFIRMED, etc.)
     * @return list of reservations with given status
     */
    List<Reservation> findByStatus(String status);

    /**
     * Find reservations for a specific room
     * @param roomId the room ID
     * @return list of reservations for the room
     */
    List<Reservation> findByRoom(int roomId);

    /**
     * Find reservations for a room within date range
     * @param roomId the room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of reservations overlapping the date range
     */
    List<Reservation> findByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut);

    /**
     * Get all reservations
     * @return list of all reservations
     */
    List<Reservation> findAll();

    /**
     * Save a new reservation
     * @param reservation the reservation to save
     * @return the saved reservation
     */
    Reservation save(Reservation reservation);

    /**
     * Update an existing reservation
     * @param reservation the reservation to update
     */
    void update(Reservation reservation);

    /**
     * Delete a reservation by ID
     * @param id the reservation ID to delete
     */
    void delete(String id);

    /**
     * Count reservations by status
     * @param status the status to count
     * @return count of reservations with given status
     */
    int countByStatus(String status);

    /**
     * Get total revenue (sum of all completed reservations)
     * @return total revenue amount
     */
    double getTotalRevenue();

    /**
     * Get revenue for a specific date range
     * @param startDate start date
     * @param endDate end date
     * @return revenue for the date range
     */
    double getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Count reservations by type
     * @param type reservation type (ONLINE or WALK_IN)
     * @return count of reservations of given type
     */
    int countByType(String type);
}

