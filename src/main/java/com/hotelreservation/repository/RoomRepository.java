package com.hotelreservation.repository;

import com.hotelreservation.entity.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * RoomRepository interface - defines contract for room data access
 */
public interface RoomRepository {

    /**
     * Find a room by ID
     * @param id the room ID
     * @return Optional containing the room if found
     */
    Optional<Room> findById(int id);

    /**
     * Find all available rooms
     * @return list of available rooms
     */
    List<Room> findAvailable();

    /**
     * Find available rooms for given date range
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available rooms for the date range
     */
    List<Room> findAvailableByDateRange(LocalDate checkIn, LocalDate checkOut);

    /**
     * Find rooms by status
     * @param status the room status (AVAILABLE, OCCUPIED, etc.)
     * @return list of rooms with given status
     */
    List<Room> findByStatus(String status);

    /**
     * Get all rooms
     * @return list of all rooms
     */
    List<Room> findAll();

    /**
     * Save a new room
     * @param room the room to save
     * @return the saved room with generated ID
     */
    Room save(Room room);

    /**
     * Update an existing room
     * @param room the room to update
     */
    void update(Room room);

    /**
     * Delete a room by ID
     * @param id the room ID to delete
     */
    void delete(int id);

    /**
     * Find room by room number
     * @param number the room number
     * @return Optional containing the room if found
     */
    Optional<Room> findByNumber(String number);

    /**
     * Count rooms by status
     * @param status the status to count
     * @return count of rooms with given status
     */
    int countByStatus(String status);
}

