package org.example.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Captor
    private ArgumentCaptor<JwtEncoderParameters> encoderParamsCaptor;

    private JwtService jwtService;
    private UUID testUserId;
    private String testIssuer;
    private Duration testTtl;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testIssuer = "http://localhost:8080";
        testTtl = Duration.ofHours(1);
        jwtService = new JwtService(testIssuer, testTtl, jwtEncoder);
    }

    @Test
    void testGenerateToken_AllClaimsPresent() {
        // Arrange
        String username = "user@example.com";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mocked.jwt.token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        String token = jwtService.generateToken(username, testUserId);

        // Assert
        assertNotNull(token);
        verify(jwtEncoder).encode(encoderParamsCaptor.capture());

        JwtEncoderParameters capturedParams = encoderParamsCaptor.getValue();
        assertEquals(testIssuer, capturedParams.getClaims().getClaimAsString("iss"));
        assertEquals(testUserId.toString(), capturedParams.getClaims().getClaimAsString("user_id"));
        assertEquals(username, capturedParams.getClaims().getSubject());
        assertNotNull(capturedParams.getClaims().getIssuedAt());
        assertNotNull(capturedParams.getClaims().getExpiresAt());
    }

    @Test
    void testGenerateToken_MultipleUserIds() {
        // Arrange
        String username = "user@example.com";
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mocked.jwt.token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        jwtService.generateToken(username, userId1);
        jwtService.generateToken(username, userId2);

        // Assert
        verify(jwtEncoder, times(2)).encode(encoderParamsCaptor.capture());
        List<JwtEncoderParameters> allParams = encoderParamsCaptor.getAllValues();

        assertEquals(userId1.toString(), allParams.get(0).getClaims().getClaimAsString("user_id"));
        assertEquals(userId2.toString(), allParams.get(1).getClaims().getClaimAsString("user_id"));
    }
}
