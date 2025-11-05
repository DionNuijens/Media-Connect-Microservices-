package org.example.authservice.mapper;

import org.example.authservice.dto.RegistrationRequestDto;
import org.example.authservice.dto.RegistrationResponseDto;
import org.example.authservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public User toEntity(RegistrationRequestDto dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        return user;
    }

    public RegistrationResponseDto toRegistrationResponseDto(User user) {
        return new RegistrationResponseDto(
                user.getUsername(),
                user.getEmail()
        );
    }
}
