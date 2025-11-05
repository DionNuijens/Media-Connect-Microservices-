package org.example.authservice.dto;

import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String email,
        String username
) {}
