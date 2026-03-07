package com.hotelreservation.mapper;

import com.hotelreservation.dto.GuestDTO;
import com.hotelreservation.dto.RoomDTO;
import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.Guest;
import com.hotelreservation.entity.Room;
import com.hotelreservation.entity.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Mapper classes (UserMapper, GuestMapper, RoomMapper)
 * Tests DTO ↔ Entity conversion and null handling.
 */
public class MapperTest {

    // =============================================
    //  UserMapper Tests
    // =============================================

    @Test
    public void testUserMapperToEntity() {
        UserDTO dto = new UserDTO("admin", "admin123", "ADMIN");
        User entity = UserMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("admin", entity.getUsername());
        assertEquals("ADMIN", entity.getRole());
        // Password should be hashed (BCrypt)
        assertNotNull(entity.getPasswordHash());
        assertTrue(entity.getPasswordHash().startsWith("$2a$"));
        assertTrue(entity.validatePassword("admin123"));
    }

    @Test
    public void testUserMapperToEntityNull() {
        assertNull(UserMapper.toEntity(null));
    }

    @Test
    public void testUserMapperToEntityNullPassword() {
        UserDTO dto = new UserDTO(1, "admin", "ADMIN"); // no password set
        User entity = UserMapper.toEntity(dto);
        assertNotNull(entity);
        assertNull(entity.getPasswordHash());
    }

    @Test
    public void testUserMapperToDTO() {
        User entity = new User(1, "admin", "$2a$12$hash", "ADMIN");
        UserDTO dto = UserMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("admin", dto.getUsername());
        assertEquals("ADMIN", dto.getRole());
        // DTO should NOT expose password
        assertNull(dto.getPassword());
    }

    @Test
    public void testUserMapperToDTONull() {
        assertNull(UserMapper.toDTO(null));
    }

    @Test
    public void testUserMapperUpdateEntity() {
        User entity = new User(1, "olduser", User.hashPassword("old123"), "GUEST");
        UserDTO dto = new UserDTO("newuser", "new123", "RECEPTIONIST");

        UserMapper.updateEntity(entity, dto);

        assertEquals("newuser", entity.getUsername());
        assertEquals("RECEPTIONIST", entity.getRole());
        // Password should be re-hashed
        assertTrue(entity.validatePassword("new123"));
    }

    @Test
    public void testUserMapperUpdateEntityNullParams() {
        // Should not throw with null params
        UserMapper.updateEntity(null, null);
        UserMapper.updateEntity(new User(), null);
        UserMapper.updateEntity(null, new UserDTO());
    }

    // =============================================
    //  GuestMapper Tests
    // =============================================

    @Test
    public void testGuestMapperToEntity() {
        GuestDTO dto = new GuestDTO("John Doe", "200112345678", "0771234567");
        dto.setEmail("john@email.com");
        dto.setAddress("123 Street");

        Guest entity = GuestMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("John Doe", entity.getName());
        assertEquals("200112345678", entity.getNic());
        assertEquals("0771234567", entity.getPhone());
        assertEquals("john@email.com", entity.getEmail());
        assertEquals("123 Street", entity.getAddress());
    }

    @Test
    public void testGuestMapperToEntityNull() {
        assertNull(GuestMapper.toEntity(null));
    }

    @Test
    public void testGuestMapperToDTO() {
        Guest entity = new Guest(1, "Jane Smith", "199887654321", "0712345678", "jane@email.com", "456 Road");
        GuestDTO dto = GuestMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Jane Smith", dto.getName());
        assertEquals("199887654321", dto.getNic());
        assertEquals("0712345678", dto.getPhone());
        assertEquals("jane@email.com", dto.getEmail());
        assertEquals("456 Road", dto.getAddress());
    }

    @Test
    public void testGuestMapperToDTONull() {
        assertNull(GuestMapper.toDTO(null));
    }

    @Test
    public void testGuestMapperUpdateEntity() {
        Guest entity = new Guest("Old Name", "111111111V", "0700000000");
        GuestDTO dto = new GuestDTO();
        dto.setName("New Name");
        dto.setPhone("0777777777");
        dto.setEmail("new@email.com");
        dto.setAddress("New Address");

        GuestMapper.updateEntity(entity, dto);

        assertEquals("New Name", entity.getName());
        assertEquals("0777777777", entity.getPhone());
        assertEquals("new@email.com", entity.getEmail());
        assertEquals("New Address", entity.getAddress());
    }

    // =============================================
    //  RoomMapper Tests
    // =============================================

    @Test
    public void testRoomMapperToEntity() {
        RoomDTO dto = new RoomDTO(1, "101", "SINGLE", 100.00, "AVAILABLE", true);
        Room entity = RoomMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("101", entity.getNumber());
        assertEquals("SINGLE", entity.getType());
        assertEquals(100.00, entity.getBasePrice(), 0.001);
        assertEquals("AVAILABLE", entity.getStatus());
        assertTrue(entity.isClean());
    }

    @Test
    public void testRoomMapperToEntityNull() {
        assertNull(RoomMapper.toEntity(null));
    }

    @Test
    public void testRoomMapperToDTO() {
        Room entity = new Room(1, "201", "DOUBLE", 175.00, "OCCUPIED", false);
        RoomDTO dto = RoomMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("201", dto.getNumber());
        assertEquals("DOUBLE", dto.getType());
        assertEquals(175.00, dto.getBasePrice(), 0.001);
        assertEquals("OCCUPIED", dto.getStatus());
        assertFalse(dto.isClean());
    }

    @Test
    public void testRoomMapperToDTONull() {
        assertNull(RoomMapper.toDTO(null));
    }

    @Test
    public void testRoomMapperUpdateEntity() {
        Room entity = new Room("101", "SINGLE", 100.00);
        RoomDTO dto = new RoomDTO();
        dto.setStatus("OCCUPIED");
        dto.setBasePrice(120.00);

        RoomMapper.updateEntity(entity, dto);

        assertEquals("OCCUPIED", entity.getStatus());
        assertEquals(120.00, entity.getBasePrice(), 0.001);
    }

    @Test
    public void testRoomMapperUpdateEntityNullParams() {
        RoomMapper.updateEntity(null, null);
        RoomMapper.updateEntity(new Room(), null);
        RoomMapper.updateEntity(null, new RoomDTO());
    }

    // =============================================
    //  Round-Trip Tests (Entity → DTO → Entity)
    // =============================================

    @Test
    public void testGuestRoundTrip() {
        Guest original = new Guest(1, "John", "123V", "077", "j@e.com", "addr");
        GuestDTO dto = GuestMapper.toDTO(original);
        Guest converted = GuestMapper.toEntity(dto);

        assertEquals(original.getName(), converted.getName());
        assertEquals(original.getNic(), converted.getNic());
        assertEquals(original.getPhone(), converted.getPhone());
        assertEquals(original.getEmail(), converted.getEmail());
    }

    @Test
    public void testRoomRoundTrip() {
        Room original = new Room(1, "301", "SUITE", 300.00, "AVAILABLE", true);
        RoomDTO dto = RoomMapper.toDTO(original);
        Room converted = RoomMapper.toEntity(dto);

        assertEquals(original.getId(), converted.getId());
        assertEquals(original.getNumber(), converted.getNumber());
        assertEquals(original.getType(), converted.getType());
        assertEquals(original.getBasePrice(), converted.getBasePrice(), 0.001);
        assertEquals(original.getStatus(), converted.getStatus());
        assertEquals(original.isClean(), converted.isClean());
    }
}

