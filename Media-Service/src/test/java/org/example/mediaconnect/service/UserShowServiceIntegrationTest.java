package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.UserShowDTO;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.model.UserShow;
import org.example.mediaconnect.repository.ShowRepository;
import org.example.mediaconnect.repository.UserShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserShowService.class)
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
class UserShowServiceIntegrationTest {

    @Autowired
    private UserShowRepository userShowRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserShowService userShowService;

    @MockitoBean
    private ShowService showService;

    private Show testShow;

    @BeforeEach
    void setUp() {
        // Create and save a test show to the database
        testShow = new Show();
        testShow.setTmdbId(1396);
        testShow.setName("Breaking Bad");
        testShow.setOverview("A chemistry teacher turns to cooking methamphetamine");
        testShow.setPosterPath("/ggFHVNvVeFwOOSN3DXsFIAGVM1.jpg");
        testShow.setBackdropPath("/xnopI5Cer5bBjHJsXiFQa9xAaP.jpg");
        testShow.setVoteAverage(9.5f);
        testShow.setFirstAirDate("2008-01-20");
        testShow.setNumberOfSeasons(5);
        testShow.setNumberOfEpisodes(62);
        testShow.setStatus("Ended");
        testShow.setType("Scripted");
        testShow.setCreatedAt(LocalDateTime.now());
        testShow.setUpdatedAt(LocalDateTime.now());

        testShow = showRepository.save(testShow);
    }

    @Test
    void testSaveShow_UserShowIsPersisted() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Act: Save a show for the user
        UserShowDTO result = userShowService.saveShow(userId, tmdbId);

        // Assert: Verify the DTO was returned
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(tmdbId, result.getTmdbId());
        assertNotNull(result.getSavedAt());

        // Assert: Verify it was actually saved to the database
        Optional<UserShow> savedUserShow = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId);
        assertTrue(savedUserShow.isPresent());
        assertEquals(userId, savedUserShow.get().getUserId());
    }

    @Test
    void testSaveShow_DuplicateSave_ReturnsExisting() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Act: Save the same show twice
        UserShowDTO firstSave = userShowService.saveShow(userId, tmdbId);
        UserShowDTO secondSave = userShowService.saveShow(userId, tmdbId);

        // Assert: Both should return the same show
        assertEquals(firstSave.getId(), secondSave.getId());

        // Assert: Only one record should exist in database
        List<UserShow> userShows = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId).stream().toList();
        assertEquals(1, userShows.size());
    }

    @Test
    void testGetUserShows_ReturnsAllSavedShows() {
        String userId = "user123";

        Show show2 = new Show();
        show2.setTmdbId(1399);
        show2.setName("Game of Thrones");
        show2.setOverview("Medieval fantasy");
        show2.setStatus("Ended");
        show2.setType("Scripted");
        show2.setCreatedAt(LocalDateTime.now());
        show2.setUpdatedAt(LocalDateTime.now());

        showRepository.save(show2);

        userShowService.saveShow(userId, testShow.getTmdbId());
        userShowService.saveShow(userId, show2.getTmdbId());

        List<UserShowDTO> result = userShowService.getUserShows(userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getTmdbId().equals(testShow.getTmdbId())));
        assertTrue(result.stream().anyMatch(s -> s.getTmdbId().equals(show2.getTmdbId())));
    }

    @Test
    void testGetUserShows_EmptyListForNewUser() {
        String userId = "newUser";

        // Act: Get shows for user who hasn't saved any
        List<UserShowDTO> result = userShowService.getUserShows(userId);

        // Assert: Should be empty
        assertTrue(result.isEmpty());
    }

//    @Test
//    void testGetUserShow_Found() {
//        String userId = "user123";
//        Integer tmdbId = testShow.getTmdbId();
//
//        // Arrange: Save a show
//        userShowService.saveShow(userId, tmdbId);
//
//        // Act: Get the specific show
//        Optional<UserShowDTO> result = userShowService.getUserShow(userId, tmdbId);
//
//        // Assert: Show should be found
//        assertTrue(result.isPresent());
//        assertEquals(userId, result.get().getUserId());
//        assertEquals(tmdbId, result.get().getTmdbId());
//        assertEquals("Breaking Bad", result.get().getShow().getName());
//    }

    @Test
    void testGetUserShow_NotFound() {
        String userId = "user123";

        // Act: Get a show that wasn't saved
        Optional<UserShowDTO> result = userShowService.getUserShow(userId, 9999);

        // Assert: Should not be found
        assertTrue(result.isEmpty());
    }

    @Test
    void testIsShowSaved_True() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Arrange: Save a show
        userShowService.saveShow(userId, tmdbId);

        // Act: Check if show is saved
        boolean result = userShowService.isShowSaved(userId, tmdbId);

        // Assert: Should be true
        assertTrue(result);
    }

    @Test
    void testIsShowSaved_False() {
        String userId = "user123";

        // Act: Check if unsaved show exists
        boolean result = userShowService.isShowSaved(userId, 9999);

        // Assert: Should be false
        assertFalse(result);
    }

    @Test
    void testRemoveShow_ShowIsDeleted() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Arrange: Save a show
        userShowService.saveShow(userId, tmdbId);
        assertTrue(userShowService.isShowSaved(userId, tmdbId));

        // Act: Remove the show
        userShowService.removeShow(userId, tmdbId);

        // Assert: Show should no longer be in database
        assertFalse(userShowService.isShowSaved(userId, tmdbId));
        Optional<UserShow> deleted = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId);
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testRemoveShow_NonexistentShow_NoError() {
        String userId = "user123";

        // Act: Remove a show that doesn't exist (should not throw)
        assertDoesNotThrow(() -> userShowService.removeShow(userId, 9999));

        // Assert: No changes to database
        List<UserShow> userShows = userShowRepository.findByUserId(userId);
        assertEquals(0, userShows.size());
    }

    @Test
    void testUpdateUserShow_Success() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Arrange: Save a show
        userShowService.saveShow(userId, tmdbId);

        UserShowDTO updateData = new UserShowDTO();
        updateData.setPersonalRating(8);
        updateData.setNotes("Amazing show!");
        updateData.setWatchStatus("WATCHING");

        // Act: Update the show
        UserShowDTO result = userShowService.updateUserShow(userId, tmdbId, updateData);

        // Assert: DTO reflects updates
        assertEquals(8, result.getPersonalRating());
        assertEquals("Amazing show!", result.getNotes());
        assertEquals("WATCHING", result.getWatchStatus());

        // Assert: Database reflects updates
        Optional<UserShow> updated = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId);
        assertTrue(updated.isPresent());
        assertEquals(8, updated.get().getPersonalRating());
        assertEquals("Amazing show!", updated.get().getNotes());
        assertEquals("WATCHING", updated.get().getWatchStatus());
    }

    @Test
    void testUpdateUserShow_PartialUpdate() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Arrange: Save a show with initial data
        UserShowDTO initialData = new UserShowDTO();
        initialData.setPersonalRating(7);
        initialData.setNotes("Initial notes");
        initialData.setWatchStatus("NOT_STARTED");

        userShowService.saveShow(userId, tmdbId);
        userShowService.updateUserShow(userId, tmdbId, initialData);

        // Update only the rating
        UserShowDTO partialUpdate = new UserShowDTO();
        partialUpdate.setPersonalRating(9);

        // Act: Apply partial update
        UserShowDTO result = userShowService.updateUserShow(userId, tmdbId, partialUpdate);

        // Assert: Only rating changed, others remain
        assertEquals(9, result.getPersonalRating());
        assertEquals("Initial notes", result.getNotes());
        assertEquals("NOT_STARTED", result.getWatchStatus());
    }

    @Test
    void testUpdateUserShow_NotFound() {
        String userId = "user123";

        UserShowDTO updateData = new UserShowDTO();
        updateData.setPersonalRating(8);

        // Act & Assert: Should throw exception for non-existent show
        assertThrows(RuntimeException.class, () -> {
            userShowService.updateUserShow(userId, 9999, updateData);
        });
    }

    @Test
    void testCompleteWorkflow_SaveUpdateRetrieveDelete() {
        String userId = "user123";
        Integer tmdbId = testShow.getTmdbId();

        // Step 1: Save a show
        UserShowDTO saved = userShowService.saveShow(userId, tmdbId);
        assertNotNull(saved.getId());

        // Step 2: Update it
        UserShowDTO updateData = new UserShowDTO();
        updateData.setPersonalRating(9);
        updateData.setWatchStatus("COMPLETED");
        UserShowDTO updated = userShowService.updateUserShow(userId, tmdbId, updateData);
        assertEquals(9, updated.getPersonalRating());

        // Step 3: Retrieve it
        Optional<UserShowDTO> retrieved = userShowService.getUserShow(userId, tmdbId);
        assertTrue(retrieved.isPresent());
        assertEquals(9, retrieved.get().getPersonalRating());

        // Step 4: Delete it
        userShowService.removeShow(userId, tmdbId);
        Optional<UserShowDTO> afterDelete = userShowService.getUserShow(userId, tmdbId);
        assertTrue(afterDelete.isEmpty());
    }
}