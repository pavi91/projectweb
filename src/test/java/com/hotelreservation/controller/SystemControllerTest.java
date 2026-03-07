package com.hotelreservation.controller;

import com.hotelreservation.dto.UserDTO;
import com.hotelreservation.exception.AuthenticationException;
import com.hotelreservation.service.UserService;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SystemController
 * Tests login, logout, session validation with mocked UserService.
 */
public class SystemControllerTest {

    private SystemController controller;
    private UserService mockUserService;

    @Before
    public void setUp() {
        mockUserService = mock(UserService.class);
        controller = new SystemController(mockUserService);
    }

    // =============================================
    //  Login Tests
    // =============================================

    @Test
    public void testLoginSuccessAdmin() throws AuthenticationException {
        UserDTO adminDTO = new UserDTO(1, "admin", "ADMIN");
        when(mockUserService.authenticate("admin", "admin123")).thenReturn(adminDTO);

        SystemController.AuthResult result = controller.login("admin", "admin123");

        assertTrue(result.isSuccess());
        assertEquals("Authentication successful", result.getMessage());
        assertNotNull(result.getUser());
        assertEquals("ADMIN", result.getUserRole());
        assertEquals("admin", result.getUser().getUsername());
    }

    @Test
    public void testLoginSuccessGuest() throws AuthenticationException {
        UserDTO guestDTO = new UserDTO(3, "guest", "GUEST");
        when(mockUserService.authenticate("guest", "guest123")).thenReturn(guestDTO);

        SystemController.AuthResult result = controller.login("guest", "guest123");

        assertTrue(result.isSuccess());
        assertEquals("GUEST", result.getUserRole());
    }

    @Test
    public void testLoginSuccessReceptionist() throws AuthenticationException {
        UserDTO receptionistDTO = new UserDTO(2, "receptionist", "RECEPTIONIST");
        when(mockUserService.authenticate("receptionist", "recep123")).thenReturn(receptionistDTO);

        SystemController.AuthResult result = controller.login("receptionist", "recep123");

        assertTrue(result.isSuccess());
        assertEquals("RECEPTIONIST", result.getUserRole());
    }

    @Test
    public void testLoginFailureWrongPassword() throws AuthenticationException {
        when(mockUserService.authenticate("admin", "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password"));

        SystemController.AuthResult result = controller.login("admin", "wrong");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid"));
        assertNull(result.getUser());
        assertNull(result.getUserRole());
    }

    @Test
    public void testLoginFailureUserNotFound() throws AuthenticationException {
        when(mockUserService.authenticate("unknown", "pass"))
                .thenThrow(new AuthenticationException("User not found"));

        SystemController.AuthResult result = controller.login("unknown", "pass");

        assertFalse(result.isSuccess());
        assertNull(result.getUser());
    }

    @Test
    public void testLoginEmptyUsername() {
        SystemController.AuthResult result = controller.login("", "password");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void testLoginNullUsername() {
        SystemController.AuthResult result = controller.login(null, "password");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void testLoginEmptyPassword() {
        SystemController.AuthResult result = controller.login("admin", "");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void testLoginNullPassword() {
        SystemController.AuthResult result = controller.login("admin", null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Password cannot be empty"));
    }

    // =============================================
    //  Logout Tests
    // =============================================

    @Test
    public void testLogoutSuccess() {
        boolean result = controller.logout();
        assertTrue(result);
    }

    // =============================================
    //  Session Validation Tests
    // =============================================

    @Test
    public void testValidateSessionValid() {
        UserDTO userDTO = new UserDTO(1, "admin", "ADMIN");
        when(mockUserService.getUserById(1)).thenReturn(userDTO);

        UserDTO result = controller.validateSession(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("admin", result.getUsername());
    }

    @Test
    public void testValidateSessionInvalid() {
        when(mockUserService.getUserById(999)).thenReturn(null);

        UserDTO result = controller.validateSession(999);

        assertNull(result);
    }

    // =============================================
    //  AuthResult Inner Class Tests
    // =============================================

    @Test
    public void testAuthResultToString() {
        SystemController.AuthResult result = new SystemController.AuthResult(
                true, "Success", new UserDTO(1, "admin", "ADMIN"));
        String str = result.toString();
        assertTrue(str.contains("success=true"));
        assertTrue(str.contains("admin"));
    }

    @Test
    public void testAuthResultGetUserRoleNull() {
        SystemController.AuthResult result = new SystemController.AuthResult(false, "Failed", null);
        assertNull(result.getUserRole());
    }
}

