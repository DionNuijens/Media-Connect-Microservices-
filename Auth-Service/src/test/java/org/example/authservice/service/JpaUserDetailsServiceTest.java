package org.example.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private JpaUserDetailsService jpaUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        jpaUserDetailsService = new JpaUserDetailsService(userRepository);
        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setPassword("encodedPassword");
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername("user@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> jpaUserDetailsService.loadUserByUsername(email)
        );

        // Optional: check that exception message contains email
        assertTrue(exception.getMessage().contains(email));

        verify(userRepository, times(1)).findByEmail(email);
    }
}
