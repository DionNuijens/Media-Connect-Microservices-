package org.example.authservice.service;

import org.example.authservice.dto.AuthenticationRequestDto;
import org.example.authservice.dto.AuthenticationResponseDto;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private AuthenticationRequestDto authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("user@example.com");
        testUser.setPassword("encodedPassword");

        authRequest = new AuthenticationRequestDto("user", "user@example.com", "password123");
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken("user@example.com", testUser.getId()))
                .thenReturn(expectedToken);

        // Act
        AuthenticationResponseDto response = authenticationService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(jwtService, times(1)).generateToken("user@example.com", testUser.getId());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        AuthenticationRequestDto request = new AuthenticationRequestDto("nonexistent","nonexistent@example.com", "password");

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(jwtService, never()).generateToken(anyString(), any(UUID.class));
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        // Act & Assert
        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate(authRequest));
        verify(userRepository, never()).findByEmail(anyString());
    }

//    @Test
//    void testAuthenticate_TokenGeneratedWithCorrectParams() {
//        // Arrange
//        String expectedToken = "generated.jwt.token";
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(null);
//        when(userRepository.findByEmail(authRequest.email()))
//                .thenReturn(Optional.of(testUser));
//        when(jwtService.generateToken(authRequest.email(), testUser.getId()))
//                .thenReturn(expectedToken);
//
//        // Act
//        AuthenticationResponseDto response = authenticationService.authenticate(authRequest);
//
//        // Assert
//        verify(jwtService, times(1)).generateToken(authRequest.email(), testUser.getId());
//        assertEquals(expectedToken, response.token());
//    }
}