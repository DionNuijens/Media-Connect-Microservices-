package org.example.authservice.controller;

import org.example.authservice.service.UserDeletionService;
import org.example.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDeletionService userDeletionService;
    @Autowired
    private UserService userService;

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

    @PutMapping("/{userId}/public-status")
    public ResponseEntity<?> updatePublicStatus(
            @PathVariable String userId,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean isPublic = request.get("isPublic");
            // You may need to find user by ID instead of email here
            userService.updatePublicStatus(userId, isPublic);
            return ResponseEntity.ok("Public status updated");
        } catch (Exception e) {
            log.error("Error updating public status for user: {}", userId, e);
            return ResponseEntity.status(500).body("Failed to update public status");
        }
    }
}
