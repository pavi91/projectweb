package com.hotelreservation.mapper;

import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.entity.Room;

/**
 * RoomMapper - handles conversion between Room entity and RoomDTO
 */
public class RoomMapper {

    /**
     * Convert RoomDTO to Room entity
     * @param dto the RoomDTO to convert
     * @return Room entity
     */
    public static Room toEntity(RoomDTO dto) {
        if (dto == null) {
            return null;
        }
        Room room = new Room(dto.getNumber(), dto.getType(), dto.getBasePrice());
        room.setStatus(dto.getStatus());
        room.setClean(dto.isClean());
        return room;
    }

    /**
     * Convert Room entity to RoomDTO
     * @param entity the Room entity to convert
     * @return RoomDTO
     */
    public static RoomDTO toDTO(Room entity) {
        if (entity == null) {
            return null;
        }
        return new RoomDTO(
            entity.getId(),
            entity.getNumber(),
            entity.getType(),
            entity.getBasePrice(),
            entity.getStatus(),
            entity.isClean()
        );
    }

    /**
     * Update existing room entity from DTO
     * @param entity the entity to update
     * @param dto the DTO containing new values
     */
    public static void updateEntity(Room entity, RoomDTO dto) {
        if (entity != null && dto != null) {
            if (dto.getStatus() != null) {
                entity.setStatus(dto.getStatus());
            }
            if (dto.getBasePrice() > 0) {
                entity.setBasePrice(dto.getBasePrice());
            }
        }
    }
}

