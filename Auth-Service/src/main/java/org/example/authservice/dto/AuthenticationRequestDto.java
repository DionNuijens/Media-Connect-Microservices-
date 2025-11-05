package org.example.authservice.dto;

public record AuthenticationRequestDto(
        String username,
        String email,
        String password
) {}
