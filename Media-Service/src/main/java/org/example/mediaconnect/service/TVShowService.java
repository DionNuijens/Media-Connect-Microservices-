package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TVShowService {

    private final WebClient tmdbClient;

    public TVShowService(WebClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    public TMDBSearchResponse searchShow(String showName) {
        if (showName == null || showName.isBlank()) {
            return new TMDBSearchResponse();
        }

        return tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("query", showName)
                        .queryParam("language", "en-US")
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .bodyToMono(TMDBSearchResponse.class)
                .block();
    }

    public TMDBShowDetail getShowDetails(Integer showId) {
        if (showId == null) {
            throw new IllegalArgumentException("Show ID cannot be null");
        }

        return tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}")
                        .queryParam("language", "en-US")
                        .build(showId))
                .retrieve()
                .bodyToMono(TMDBShowDetail.class)
                .block();
    }

    public TMDBWatchProviders getWatchProviders(Integer showId) {
        if (showId == null) {
            throw new IllegalArgumentException("Show ID cannot be null");
        }

        return tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/watch/providers")
                        .build(showId))
                .retrieve()
                .bodyToMono(TMDBWatchProviders.class)
                .block();
    }

    // NEW COMBINED METHOD
    public TVShowWithProvidersDTO getShowWithProviders(Integer showId, String regionCode) {
        if (showId == null) {
            throw new IllegalArgumentException("Show ID cannot be null");
        }
        if (regionCode == null || regionCode.isBlank()) {
            regionCode = "US";
        }

        try {
            // Fetch both in parallel
            TMDBShowDetail showDetails = getShowDetails(showId);
            TMDBWatchProviders allProviders = getWatchProviders(showId);

            // Extract providers for the user's region
            TMDBWatchProviders.RegionProviders regionProviders = null;
            if (allProviders != null && allProviders.getResults() != null) {
                regionProviders = allProviders.getResults().get(regionCode);
            }

            return new TVShowWithProvidersDTO(showDetails, regionProviders);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch show details and providers", e);
        }
    }
}