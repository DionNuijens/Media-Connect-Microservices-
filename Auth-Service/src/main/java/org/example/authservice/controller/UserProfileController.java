package org.example.authservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.UserProfileDto;
import org.example.authservice.mapper.UserMapper;
import org.example.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        DecodedJWT jwt = JWT.decode(token);

        String username = jwt.getSubject();

        var user = userService.getUserByUsername(username);
        var userDto = userMapper.toUserProfileDto(user);

        return ResponseEntity.ok(userDto);
    }
}
