package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TVShowServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient tmdbClient;

    @InjectMocks
    private TVShowService tvShowService;

    @Test
    void testSearchShow_Success() {
        TMDBSearchResponse mockResponse = new TMDBSearchResponse();
        mockResponse.setResults(new ArrayList<>());

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(TMDBSearchResponse.class))
                .thenReturn(Mono.just(mockResponse));

        TMDBSearchResponse result = tvShowService.searchShow("Breaking Bad");

        assertNotNull(result);
        verify(tmdbClient.get(), times(1)).uri(any(Function.class));
    }

    @Test
    void testSearchShow_NullShowName() {
        TMDBSearchResponse result = tvShowService.searchShow(null);

        assertNotNull(result);
        verify(tmdbClient, never()).get();
    }

    @Test
    void testSearchShow_BlankShowName() {
        TMDBSearchResponse result = tvShowService.searchShow("   ");

        assertNotNull(result);
        verify(tmdbClient, never()).get();
    }

    @Test
    void testGetShowDetails_Success() {
        Integer showId = 1396;
        TMDBShowDetail mockDetails = new TMDBShowDetail();
        mockDetails.setId(showId);
        mockDetails.setName("Breaking Bad");

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(TMDBShowDetail.class))
                .thenReturn(Mono.just(mockDetails));

        TMDBShowDetail result = tvShowService.getShowDetails(showId);

        assertNotNull(result);
        assertEquals(showId, result.getId());
    }

    @Test
    void testGetShowDetails_NullShowId() {
        assertThrows(IllegalArgumentException.class, () -> tvShowService.getShowDetails(null));
    }

    @Test
    void testGetWatchProviders_Success() {
        Integer showId = 1396;
        TMDBWatchProviders mockProviders = new TMDBWatchProviders();
        mockProviders.setId(showId);

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(TMDBWatchProviders.class))
                .thenReturn(Mono.just(mockProviders));

        TMDBWatchProviders result = tvShowService.getWatchProviders(showId);

        assertNotNull(result);
        assertEquals(showId, result.getId());
    }

    @Test
    void testGetWatchProviders_NullShowId() {
        assertThrows(IllegalArgumentException.class, () -> tvShowService.getWatchProviders(null));
    }

    @Test
    void testGetShowWithProviders_Success() {
        Integer showId = 1396;
        String region = "US";

        TMDBShowDetail mockDetails = new TMDBShowDetail();
        mockDetails.setId(showId);

        TMDBWatchProviders watchProviders = new TMDBWatchProviders();
        Map<String, TMDBWatchProviders.RegionProviders> results = new HashMap<>();
        results.put(region, new TMDBWatchProviders.RegionProviders());
        watchProviders.setResults(results);

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(any(Class.class)))
                .thenReturn(Mono.just(mockDetails))
                .thenReturn(Mono.just(watchProviders));

        TVShowWithProvidersDTO result = tvShowService.getShowWithProviders(showId, region);

        assertNotNull(result);
        assertEquals(mockDetails, result.getShowDetails());
    }

    @Test
    void testGetShowWithProviders_NullShowId() {
        assertThrows(IllegalArgumentException.class, () -> tvShowService.getShowWithProviders(null, "US"));
    }

    @Test
    void testGetShowWithProviders_NullRegion_DefaultsUS() {
        Integer showId = 1396;

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(any(Class.class)))
                .thenReturn(Mono.just(new TMDBShowDetail()))
                .thenReturn(Mono.just(new TMDBWatchProviders()));

        TVShowWithProvidersDTO result = tvShowService.getShowWithProviders(showId, null);

        assertNotNull(result);
    }

    @Test
    void testGetShowWithProviders_Exception() {
        Integer showId = 1396;

        when(tmdbClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(TMDBShowDetail.class))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        assertThrows(RuntimeException.class, () -> tvShowService.getShowWithProviders(showId, "US"));
    }
}
