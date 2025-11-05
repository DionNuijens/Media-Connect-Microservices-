package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.User; // Import Spring Security's User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> User.builder() // This is Spring Security's User
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER") // Add at least one authority
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username [%s] not found".formatted(email)));
    }
}