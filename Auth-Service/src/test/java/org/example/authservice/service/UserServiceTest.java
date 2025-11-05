package org.example.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setUsername("testuser");
    }

    @Test
    void testGetUserByUsername_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByUsername("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        // Arrange
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserByUsername(email)
        );

        assertEquals(HttpStatus.GONE, exception.getStatusCode());
        assertTrue(exception.getReason().contains("user account"));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserByUsername_DifferentUser() {
        // Arrange
        User differentUser = new User();
        differentUser.setEmail("different@example.com");
        differentUser.setUsername("differentuser");

        when(userRepository.findByEmail("different@example.com"))
                .thenReturn(Optional.of(differentUser));

        // Act
        User result = userService.getUserByUsername("different@example.com");

        // Assert
        assertEquals("different@example.com", result.getEmail());
        assertEquals("differentuser", result.getUsername());
        verify(userRepository, times(1)).findByEmail("different@example.com");
    }
}
