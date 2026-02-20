package com.hotelreservation.service;

import com.hotelreservation.dto.RoomDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomService interface - defines contract for room management and availability
 */
public interface RoomService {

    /**
     * Get all available rooms
     * @return list of available rooms
     */
    List<RoomDTO> getAvailableRooms();

    /**
     * Get available rooms for a date range
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available rooms for the date range
     */
    List<RoomDTO> getAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    /**
     * Get room by ID
     * @param roomId the room ID
     * @return RoomDTO if found
     */
    RoomDTO getRoomById(int roomId);

    /**
     * Get all rooms
     * @return list of all rooms
     */
    List<RoomDTO> getAllRooms();

    /**
     * Update room status
     * @param roomId the room ID
     * @param status the new status (AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE)
     */
    void updateRoomStatus(int roomId, String status);

    /**
     * Mark a room as clean
     * @param roomId the room ID
     */
    void markRoomClean(int roomId);

    /**
     * Mark a room as dirty
     * @param roomId the room ID
     */
    void markRoomDirty(int roomId);

    /**
     * Get room by room number
     * @param roomNumber the room number
     * @return RoomDTO if found
     */
    RoomDTO getRoomByNumber(String roomNumber);

    /**
     * Get count of rooms by status
     * @param status the status to count
     * @return count of rooms with given status
     */
    int getRoomCountByStatus(String status);

    /**
     * Get occupancy rate for date range
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return occupancy percentage (0-100)
     */
    double getOccupancyRate(LocalDate checkIn, LocalDate checkOut);
}

