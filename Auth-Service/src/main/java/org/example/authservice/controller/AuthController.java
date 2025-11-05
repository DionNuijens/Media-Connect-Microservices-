package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.AuthenticationRequestDto;
import org.example.authservice.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity authenticate(
            @RequestBody AuthenticationRequestDto authenticationRequestDto) {

        return ResponseEntity.ok(
                authenticationService.authenticate(authenticationRequestDto)
        );
    }
}
