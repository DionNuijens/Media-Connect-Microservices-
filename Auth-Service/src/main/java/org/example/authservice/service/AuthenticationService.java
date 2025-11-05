package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.AuthenticationRequestDto;
import org.example.authservice.dto.AuthenticationResponseDto;
import org.example.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                );

        authenticationManager.authenticate(authToken);

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(request.email(), user.getId());

        return new AuthenticationResponseDto(token);
    }

}
