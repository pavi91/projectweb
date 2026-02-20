package com.hotelreservation.mapper;

import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.entity.Guest;

/**
 * GuestMapper - handles conversion between Guest entity and GuestDTO
 */
public class GuestMapper {

    /**
     * Convert GuestDTO to Guest entity
     * @param dto the GuestDTO to convert
     * @return Guest entity
     */
    public static Guest toEntity(GuestDTO dto) {
        if (dto == null) {
            return null;
        }
        Guest guest = new Guest(dto.getName(), dto.getNic(), dto.getPhone());
        guest.setEmail(dto.getEmail());
        guest.setAddress(dto.getAddress());
        return guest;
    }

    /**
     * Convert Guest entity to GuestDTO
     * @param entity the Guest entity to convert
     * @return GuestDTO
     */
    public static GuestDTO toDTO(Guest entity) {
        if (entity == null) {
            return null;
        }
        return new GuestDTO(
            entity.getId(),
            entity.getName(),
            entity.getNic(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getAddress()
        );
    }

    /**
     * Update existing guest entity from DTO
     * @param entity the entity to update
     * @param dto the DTO containing new values
     */
    public static void updateEntity(Guest entity, GuestDTO dto) {
        if (entity != null && dto != null) {
            if (dto.getName() != null) {
                entity.setName(dto.getName());
            }
            if (dto.getPhone() != null || dto.getEmail() != null) {
                entity.updateContact(dto.getPhone(), dto.getEmail());
            }
            if (dto.getAddress() != null) {
                entity.setAddress(dto.getAddress());
            }
        }
    }
}

