package org.example.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.validation.ValidationException;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistartionService userRegistrationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("plainPassword");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String encodedPassword = "encodedPassword123";
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User registeredUser = userRegistrationService.registerUser(testUser);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userRegistrationService.registerUser(testUser)
        );
        assertTrue(exception.getMessage().contains("Username or Email already exists"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userRegistrationService.registerUser(testUser)
        );
        assertTrue(exception.getMessage().contains("Username or Email already exists"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
