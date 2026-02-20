package com.hotelreservation.mapper;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.User;

/**
 * UserMapper - handles conversion between User entity and UserDTO
 * Ensures password is never exposed in DTOs
 */
public class UserMapper {

    /**
     * Convert UserDTO to User entity
     * Hashes the password during conversion
     * @param dto the UserDTO to convert
     * @return User entity with hashed password
     */
    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        String hashedPassword = dto.getPassword() != null ? User.hashPassword(dto.getPassword()) : null;
        return new User(dto.getUsername(), hashedPassword, dto.getRole());
    }

    /**
     * Convert User entity to UserDTO
     * Does NOT expose password hash
     * @param entity the User entity to convert
     * @return UserDTO without sensitive data
     */
    public static UserDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }
        return new UserDTO(entity.getId(), entity.getUsername(), entity.getRole());
    }

    /**
     * Update existing user entity from DTO
     * @param entity the entity to update
     * @param dto the DTO containing new values
     */
    public static void updateEntity(User entity, UserDTO dto) {
        if (entity != null && dto != null) {
            if (dto.getUsername() != null) {
                entity.setUsername(dto.getUsername());
            }
            if (dto.getPassword() != null) {
                entity.setPasswordHash(User.hashPassword(dto.getPassword()));
            }
            if (dto.getRole() != null) {
                entity.setRole(dto.getRole());
            }
        }
    }
}

