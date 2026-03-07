package com.hotelreservation.service;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.entity.User;
import com.hotelreservation.exception.AuthenticationException;
import com.hotelreservation.repository.UserRepository;
import com.hotelreservation.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.Before;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * Uses Mockito to mock UserRepository — no database required.
 * Tests authentication, user creation, validation, and error handling.
 */
public class UserServiceTest {

    private UserServiceImpl userService;
    private UserRepository mockRepo;

    @Before
    public void setUp() {
        mockRepo = mock(UserRepository.class);
        userService = new UserServiceImpl(mockRepo);
    }

    // =============================================
    //  Authentication Tests
    // =============================================

    @Test
    public void testAuthenticateSuccess() throws AuthenticationException {
        String hashedPassword = User.hashPassword("admin123");
        User user = new User(1, "admin", hashedPassword, "ADMIN");
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDTO result = userService.authenticate("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        assertEquals(1, result.getId());
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateWrongPassword() throws AuthenticationException {
        String hashedPassword = User.hashPassword("admin123");
        User user = new User(1, "admin", hashedPassword, "ADMIN");
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(user));

        userService.authenticate("admin", "wrongpassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateUserNotFound() throws AuthenticationException {
        when(mockRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        userService.authenticate("nonexistent", "password");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateEmptyUsername() throws AuthenticationException {
        userService.authenticate("", "password");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateNullUsername() throws AuthenticationException {
        userService.authenticate(null, "password");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateEmptyPassword() throws AuthenticationException {
        userService.authenticate("admin", "");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateNullPassword() throws AuthenticationException {
        userService.authenticate("admin", null);
    }

    // =============================================
    //  User Creation Tests
    // =============================================

    @Test
    public void testCreateUserSuccess() throws Exception {
        when(mockRepo.existsByUsername("newuser")).thenReturn(false);
        when(mockRepo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        UserDTO dto = new UserDTO("newuser", "pass123", "GUEST");
        userService.createUser(dto);

        verify(mockRepo).existsByUsername("newuser");
        verify(mockRepo).save(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserDuplicateUsername() throws Exception {
        when(mockRepo.existsByUsername("existing")).thenReturn(true);

        UserDTO dto = new UserDTO("existing", "pass123", "GUEST");
        userService.createUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullDTO() throws Exception {
        userService.createUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserEmptyUsername() throws Exception {
        UserDTO dto = new UserDTO("", "pass123", "GUEST");
        userService.createUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserEmptyPassword() throws Exception {
        UserDTO dto = new UserDTO("user", "", "GUEST");
        userService.createUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserEmptyRole() throws Exception {
        UserDTO dto = new UserDTO("user", "pass123", "");
        userService.createUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullUsername() throws Exception {
        UserDTO dto = new UserDTO(null, "pass123", "GUEST");
        userService.createUser(dto);
    }

    // =============================================
    //  Get User Tests
    // =============================================

    @Test
    public void testGetUserById() {
        User user = new User(5, "guest", User.hashPassword("pass"), "GUEST");
        when(mockRepo.findById(5)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(5);

        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals("guest", result.getUsername());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(mockRepo.findById(999)).thenReturn(Optional.empty());

        UserDTO result = userService.getUserById(999);
        assertNull(result);
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User(1, "receptionist", User.hashPassword("pass"), "RECEPTIONIST");
        when(mockRepo.findByUsername("receptionist")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByUsername("receptionist");

        assertNotNull(result);
        assertEquals("receptionist", result.getUsername());
        assertEquals("RECEPTIONIST", result.getRole());
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        when(mockRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        UserDTO result = userService.getUserByUsername("ghost");
        assertNull(result);
    }

    // =============================================
    //  User Exists Tests
    // =============================================

    @Test
    public void testUserExistsTrue() {
        when(mockRepo.existsByUsername("admin")).thenReturn(true);
        assertTrue(userService.userExists("admin"));
    }

    @Test
    public void testUserExistsFalse() {
        when(mockRepo.existsByUsername("nonexistent")).thenReturn(false);
        assertFalse(userService.userExists("nonexistent"));
    }
}

