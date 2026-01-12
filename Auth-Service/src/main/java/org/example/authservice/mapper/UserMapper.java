package org.example.authservice.mapper;

import org.example.authservice.dto.UserProfileDto;
import org.example.authservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserProfileDto toUserProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getIsPublic()  // Add this
        );
    }
}