package org.example.authservice.controller;

import org.example.authservice.service.UserDeletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDeletionService userDeletionService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            userDeletionService.initiateUserDeletion(userId);
            return ResponseEntity.accepted().body("User deletion initiated");
        } catch (Exception e) {
            log.error("Error deleting user: {}", userId, e);
            return ResponseEntity.status(500).body("Failed to delete user");
        }
    }
}
