package org.example.mediaconnect.controller;

import org.example.mediaconnect.dto.UserShowDTO;
import org.example.mediaconnect.service.UserShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user/shows")
public class UserShowController {

    private final UserShowService userShowService;

    public UserShowController(UserShowService userShowService) {
        this.userShowService = userShowService;
    }

    // Get all saved shows for user (with full show details)
    @GetMapping
    public ResponseEntity<List<UserShowDTO>> getUserShows(@RequestParam String userId) {
        List<UserShowDTO> shows = userShowService.getUserShows(userId);
        return ResponseEntity.ok(shows);
    }

    // Get single user-show
    @GetMapping("/{tmdbId}")
    public ResponseEntity<UserShowDTO> getUserShow(
            @RequestParam String userId,
            @PathVariable Integer tmdbId) {
        return userShowService.getUserShow(userId, tmdbId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Save a show to user's list
    @PostMapping("/{tmdbId}")
    public ResponseEntity<UserShowDTO> saveShow(
            @RequestParam String userId,
            @PathVariable Integer tmdbId) {
        UserShowDTO saved = userShowService.saveShow(userId, tmdbId);
        return ResponseEntity.ok(saved);
    }

    // Check if show is saved
    @GetMapping("/{tmdbId}/exists")
    public ResponseEntity<Boolean> isShowSaved(
            @RequestParam String userId,
            @PathVariable Integer tmdbId) {
        boolean exists = userShowService.isShowSaved(userId, tmdbId);
        return ResponseEntity.ok(exists);
    }

    // Update user-specific data (rating, notes, watchStatus)
    @PutMapping("/{tmdbId}")
    public ResponseEntity<UserShowDTO> updateUserShow(
            @RequestParam String userId,
            @PathVariable Integer tmdbId,
            @RequestBody UserShowDTO updateData) {
        UserShowDTO updated = userShowService.updateUserShow(userId, tmdbId, updateData);
        return ResponseEntity.ok(updated);
    }

    // Remove show from user's list
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> removeShow(
            @RequestParam String userId,
            @PathVariable Integer tmdbId) {
        userShowService.removeShow(userId, tmdbId);
        return ResponseEntity.noContent().build();
    }
}