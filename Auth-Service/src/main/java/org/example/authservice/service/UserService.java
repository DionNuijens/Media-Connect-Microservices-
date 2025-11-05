package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.GONE,
                        "The user account has been deleted or inactivated"
                ));
    }
}
