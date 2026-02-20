package com.hotelreservation.service.impl;

import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.entity.Room;
import com.hotelreservation.mapper.RoomMapper;
import com.hotelreservation.repository.RoomRepository;
import com.hotelreservation.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoomServiceImpl - Implementation of RoomService
 * Manages room availability and status lifecycle
 */
public class RoomServiceImpl implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
    private RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<RoomDTO> getAvailableRooms() {
        try {
            List<Room> rooms = roomRepository.findAvailable();
            logger.debug("Retrieved {} available rooms", rooms.size());
            return rooms.stream()
                    .map(RoomMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving available rooms", e);
            return List.of();
        }
    }

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        try {
            List<Room> rooms = roomRepository.findAvailableByDateRange(checkIn, checkOut);
            logger.debug("Retrieved {} available rooms for dates: {} to {}",
                    rooms.size(), checkIn, checkOut);
            return rooms.stream()
                    .map(RoomMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving available rooms for date range", e);
            return List.of();
        }
    }

    @Override
    public RoomDTO getRoomById(int roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElse(null);
            if (room != null) {
                logger.debug("Retrieved room by ID: {}", roomId);
                return RoomMapper.toDTO(room);
            }
            logger.debug("Room not found with ID: {}", roomId);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving room by ID: {}", roomId, e);
            return null;
        }
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll();
            logger.debug("Retrieved all {} rooms", rooms.size());
            return rooms.stream()
                    .map(RoomMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all rooms", e);
            return List.of();
        }
    }

    @Override
    public void updateRoomStatus(int roomId, String status) {
        try {
            Room room = roomRepository.findById(roomId).orElse(null);
            if (room == null) {
                logger.warn("Room not found for status update: {}", roomId);
                return;
            }

            room.updateStatus(status);
            roomRepository.update(room);
            logger.info("Room {} status updated to: {}", roomId, status);
        } catch (Exception e) {
            logger.error("Error updating room status", e);
        }
    }

    @Override
    public void markRoomClean(int roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElse(null);
            if (room == null) {
                logger.warn("Room not found for clean marking: {}", roomId);
                return;
            }

            room.markClean();
            roomRepository.update(room);
            logger.info("Room {} marked as clean", roomId);
        } catch (Exception e) {
            logger.error("Error marking room as clean", e);
        }
    }

    @Override
    public void markRoomDirty(int roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElse(null);
            if (room == null) {
                logger.warn("Room not found for dirty marking: {}", roomId);
                return;
            }

            room.markDirty();
            roomRepository.update(room);
            logger.info("Room {} marked as dirty", roomId);
        } catch (Exception e) {
            logger.error("Error marking room as dirty", e);
        }
    }

    @Override
    public RoomDTO getRoomByNumber(String roomNumber) {
        try {
            Room room = roomRepository.findByNumber(roomNumber).orElse(null);
            if (room != null) {
                logger.debug("Retrieved room by number: {}", roomNumber);
                return RoomMapper.toDTO(room);
            }
            logger.debug("Room not found with number: {}", roomNumber);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving room by number: {}", roomNumber, e);
            return null;
        }
    }

    @Override
    public int getRoomCountByStatus(String status) {
        try {
            int count = roomRepository.countByStatus(status);
            logger.debug("Room count with status {}: {}", status, count);
            return count;
        } catch (Exception e) {
            logger.error("Error counting rooms by status", e);
            return 0;
        }
    }

    @Override
    public double getOccupancyRate(LocalDate checkIn, LocalDate checkOut) {
        try {
            List<Room> totalRooms = roomRepository.findAll();
            List<Room> availableRooms = roomRepository.findAvailableByDateRange(checkIn, checkOut);

            if (totalRooms.isEmpty()) {
                return 0;
            }

            int occupiedCount = totalRooms.size() - availableRooms.size();
            double occupancyRate = (occupiedCount * 100.0) / totalRooms.size();

            logger.debug("Occupancy rate for {} to {}: {}%", checkIn, checkOut, occupancyRate);
            return occupancyRate;
        } catch (Exception e) {
            logger.error("Error calculating occupancy rate", e);
            return 0;
        }
    }
}

