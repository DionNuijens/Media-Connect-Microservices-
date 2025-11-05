package org.example.authservice.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistartionService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) ||
                userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Username or Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
