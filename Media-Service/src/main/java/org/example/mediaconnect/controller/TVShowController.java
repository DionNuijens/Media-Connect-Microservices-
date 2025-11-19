package org.example.mediaconnect.controller;

import org.example.mediaconnect.dto.*;
import org.example.mediaconnect.service.TVShowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tv")
public class TVShowController {

    private final TVShowService tvShowService;

    public TVShowController(TVShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    @GetMapping("/search")
    public TMDBSearchResponse searchShow(@RequestParam String query) {
        return tvShowService.searchShow(query);
    }

    @GetMapping("/{id}")
    public TMDBShowDetail getShowDetails(@PathVariable Integer id) {
        return tvShowService.getShowDetails(id);
    }

    @GetMapping("/{id}/providers")
    public TMDBWatchProviders getWatchProviders(@PathVariable Integer id) {
        return tvShowService.getWatchProviders(id);
    }

    // NEW COMBINED ENDPOINT - call this instead of the above two
    @GetMapping("/{id}/full")
    public TVShowWithProvidersDTO getShowWithProviders(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "US") String region) {
        return tvShowService.getShowWithProviders(id, region);
    }
}