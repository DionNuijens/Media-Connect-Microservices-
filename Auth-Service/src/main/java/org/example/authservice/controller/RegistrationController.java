package org.example.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.RegistrationRequestDto;
//import org.example.authservice.dto.RegistrationResponseDto;
import org.example.authservice.mapper.UserRegistrationMapper;
import org.example.authservice.service.UserRegistartionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistartionService userRegistrationService;
    private final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/register")
    public ResponseEntity registerUser(
            @Valid @RequestBody RegistrationRequestDto registrationDTO) {

        var registeredUser = userRegistrationService.registerUser(
                userRegistrationMapper.toEntity(registrationDTO)
        );

        return ResponseEntity.ok(
                userRegistrationMapper.toRegistrationResponseDto(registeredUser)
        );
    }
}
