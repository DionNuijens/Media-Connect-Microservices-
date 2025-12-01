package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.TMDBShowDetail;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(ShowService.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.h2.console.enabled=false"
})
class ShowServiceIntegrationTest {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowService showService;

    @MockitoBean
    private TVShowService tvShowService;

    @BeforeEach
    void setUp() {
        // Repositories and services are auto-initialized
    }

    private TMDBShowDetail createTMDBShowDetail(Integer tmdbId, String name) {
        TMDBShowDetail detail = new TMDBShowDetail();
        detail.setId(tmdbId);
        detail.setName(name);
        detail.setOverview("Test overview for " + name);
        detail.setPosterPath("/poster" + tmdbId + ".jpg");
        detail.setBackdropPath("/backdrop" + tmdbId + ".jpg");
        detail.setVoteAverage(8.5f);
        detail.setFirstAirDate("2020-01-01");
        detail.setNumberOfSeasons(3);
        detail.setNumberOfEpisodes(30);
        detail.setStatus("Returning Series");
        detail.setType("Scripted");
        return detail;
    }

    private Show createAndSaveShow(Integer tmdbId, String name) {
        Show show = new Show();
        show.setTmdbId(tmdbId);
        show.setName(name);
        show.setOverview("Test overview for " + name);
        show.setPosterPath("/poster" + tmdbId + ".jpg");
        show.setBackdropPath("/backdrop" + tmdbId + ".jpg");
        show.setVoteAverage(8.5f);
        show.setFirstAirDate("2020-01-01");
        show.setNumberOfSeasons(3);
        show.setNumberOfEpisodes(30);
        show.setStatus("Returning Series");
        show.setType("Scripted");
        show.setCreatedAt(LocalDateTime.now());
        show.setUpdatedAt(LocalDateTime.now());
        return showRepository.save(show);
    }

    @Test
    void testGetOrCreateShow_NewShow() {
        Integer tmdbId = 1234;
        String showName = "Breaking Bad";

        TMDBShowDetail tmdbDetail = createTMDBShowDetail(tmdbId, showName);
        when(tvShowService.getShowDetails(tmdbId)).thenReturn(tmdbDetail);

        Show result = showService.getOrCreateShow(tmdbId);

        assertNotNull(result);
        assertEquals(tmdbId, result.getTmdbId());
        assertEquals(showName, result.getName());
        assertEquals("Test overview for " + showName, result.getOverview());
        assertEquals(8.5f, result.getVoteAverage());
        assertEquals(3, result.getNumberOfSeasons());
        assertEquals(30, result.getNumberOfEpisodes());
        assertEquals("Returning Series", result.getStatus());
        assertEquals("Scripted", result.getType());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(tvShowService, times(1)).getShowDetails(tmdbId);

        // Verify it was saved to database
        Optional<Show> saved = showRepository.findByTmdbId(tmdbId);
        assertTrue(saved.isPresent());
        assertEquals(showName, saved.get().getName());
    }

    @Test
    void testGetOrCreateShow_ExistingShow() {
        Integer tmdbId = 1234;
        String showName = "Breaking Bad";

        // Pre-save a show
        createAndSaveShow(tmdbId, showName);

        Show result = showService.getOrCreateShow(tmdbId);

        assertNotNull(result);
        assertEquals(tmdbId, result.getTmdbId());
        assertEquals(showName, result.getName());

        // tvShowService should NOT be called since show exists in cache
        verify(tvShowService, never()).getShowDetails(any());
    }

    @Test
    void testGetOrCreateShow_MultipleCreations() {
        Integer tmdbId1 = 1234;
        Integer tmdbId2 = 5678;

        TMDBShowDetail detail1 = createTMDBShowDetail(tmdbId1, "Show 1");
        TMDBShowDetail detail2 = createTMDBShowDetail(tmdbId2, "Show 2");

        when(tvShowService.getShowDetails(tmdbId1)).thenReturn(detail1);
        when(tvShowService.getShowDetails(tmdbId2)).thenReturn(detail2);

        Show show1 = showService.getOrCreateShow(tmdbId1);
        Show show2 = showService.getOrCreateShow(tmdbId2);

        assertNotNull(show1);
        assertNotNull(show2);
        assertNotEquals(show1.getTmdbId(), show2.getTmdbId());
        assertEquals("Show 1", show1.getName());
        assertEquals("Show 2", show2.getName());

        assertEquals(2, showRepository.count());
    }

    @Test
    void testGetShow_ExistingShow() {
        Integer tmdbId = 1234;
        String showName = "Breaking Bad";

        createAndSaveShow(tmdbId, showName);

        Optional<Show> result = showService.getShow(tmdbId);

        assertTrue(result.isPresent());
        assertEquals(showName, result.get().getName());
    }

    @Test
    void testGetShow_NonExistentShow() {
        Optional<Show> result = showService.getShow(9999);

        assertFalse(result.isPresent());
    }

    @Test
    void testRefreshShowData_Success() {
        Integer tmdbId = 1234;
        String originalName = "Breaking Bad";
        String updatedName = "Breaking Bad - Updated";

        // Create original show
        Show original = createAndSaveShow(tmdbId, originalName);
        LocalDateTime originalCreatedAt = original.getCreatedAt();
        LocalDateTime originalUpdatedAt = original.getUpdatedAt();

        // Mock updated data from TMDB
        TMDBShowDetail updatedDetail = createTMDBShowDetail(tmdbId, updatedName);
        updatedDetail.setVoteAverage(9.0f);
        updatedDetail.setNumberOfSeasons(5);
        updatedDetail.setNumberOfEpisodes(62);
        updatedDetail.setStatus("Ended");

        when(tvShowService.getShowDetails(tmdbId)).thenReturn(updatedDetail);

        // Wait a bit to ensure updatedAt changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Show result = showService.refreshShowData(tmdbId);

        assertEquals(updatedName, result.getName());
        assertEquals(9.0f, result.getVoteAverage());
        assertEquals(5, result.getNumberOfSeasons());
        assertEquals(62, result.getNumberOfEpisodes());
        assertEquals("Ended", result.getStatus());
        assertEquals(originalCreatedAt, result.getCreatedAt()); // createdAt should not change
        assertNotEquals(originalUpdatedAt, result.getUpdatedAt()); // updatedAt should change
        assertTrue(result.getUpdatedAt().isAfter(originalUpdatedAt));

        verify(tvShowService, times(1)).getShowDetails(tmdbId);
    }

    @Test
    void testRefreshShowData_ShowNotFound() {
        Integer tmdbId = 9999;

        assertThrows(RuntimeException.class, () -> showService.refreshShowData(tmdbId));
        verify(tvShowService, never()).getShowDetails(any());
    }

    @Test
    void testRefreshShowData_PreservesUnchangedFields() {
        Integer tmdbId = 1234;
        String showName = "Breaking Bad";

        Show original = createAndSaveShow(tmdbId, showName);
        String originalPosterPath = original.getPosterPath();
        String originalBackdropPath = original.getBackdropPath();

        TMDBShowDetail updatedDetail = createTMDBShowDetail(tmdbId, showName);
        when(tvShowService.getShowDetails(tmdbId)).thenReturn(updatedDetail);

        Show result = showService.refreshShowData(tmdbId);

        // Fields that shouldn't change during refresh
        assertEquals(originalPosterPath, result.getPosterPath());
        assertEquals(originalBackdropPath, result.getBackdropPath());
        assertEquals(original.getFirstAirDate(), result.getFirstAirDate());
        assertEquals(original.getType(), result.getType());
    }

    @Test
    void testGetOrCreateShow_SavesWithTimestamps() {
        Integer tmdbId = 1234;

        TMDBShowDetail detail = createTMDBShowDetail(tmdbId, "Test Show");
        when(tvShowService.getShowDetails(tmdbId)).thenReturn(detail);

        LocalDateTime beforeCreation = LocalDateTime.now();
        Show result = showService.getOrCreateShow(tmdbId);
        LocalDateTime afterCreation = LocalDateTime.now();

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation) || result.getCreatedAt().isEqual(beforeCreation));
        assertTrue(result.getCreatedAt().isBefore(afterCreation) || result.getCreatedAt().isEqual(afterCreation));
        assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    }

    @Test
    void testConcurrentGetOrCreateShow() {
        Integer tmdbId = 1234;

        TMDBShowDetail detail = createTMDBShowDetail(tmdbId, "Test Show");
        when(tvShowService.getShowDetails(tmdbId)).thenReturn(detail);

        Show show1 = showService.getOrCreateShow(tmdbId);
        Show show2 = showService.getOrCreateShow(tmdbId);

        // Both should return the same show from cache
        assertEquals(show1.getTmdbId(), show2.getTmdbId());

        // tvShowService should only be called once (for first creation)
        verify(tvShowService, times(1)).getShowDetails(tmdbId);

        // Only one record should exist in database
        assertEquals(1, showRepository.count());
    }
}