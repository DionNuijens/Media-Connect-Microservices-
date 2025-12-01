package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.UserShowDTO;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.model.UserShow;
import org.example.mediaconnect.repository.UserShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserShowServiceTest {

    @Mock
    private UserShowRepository userShowRepository;

    @Mock
    private ShowService showService;

    @InjectMocks
    private UserShowService userShowService;

    private Show mockShow;
    private UserShow mockUserShow;

    @BeforeEach
    void setUp() {
        mockShow = new Show();
        mockShow.setTmdbId(1);
        mockShow.setTmdbId(1396);
        mockShow.setName("Breaking Bad");
        mockShow.setOverview("A chemistry teacher...");
        mockShow.setVoteAverage(9.0f);

        mockUserShow = new UserShow();
        mockUserShow.setId(1L);
        mockUserShow.setUserId("user123");
        mockUserShow.setTmdbId(1396);
        mockUserShow.setShow(mockShow);
        mockUserShow.setSavedAt(LocalDateTime.now());
        mockUserShow.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSaveShow_NewShow() {
        String userId = "user123";
        Integer tmdbId = 1396;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.empty());
        when(showService.getOrCreateShow(tmdbId)).thenReturn(mockShow);
        when(userShowRepository.save(any(UserShow.class))).thenReturn(mockUserShow);

        UserShowDTO result = userShowService.saveShow(userId, tmdbId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(tmdbId, result.getTmdbId());
        verify(userShowRepository, times(1)).save(any(UserShow.class));
    }

    @Test
    void testSaveShow_AlreadyExists() {
        String userId = "user123";
        Integer tmdbId = 1396;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.of(mockUserShow));

        UserShowDTO result = userShowService.saveShow(userId, tmdbId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userShowRepository, never()).save(any(UserShow.class));
    }

    @Test
    void testGetUserShows() {
        String userId = "user123";
        List<UserShow> userShows = Arrays.asList(mockUserShow);

        when(userShowRepository.findByUserIdWithShowDetails(userId))
                .thenReturn(userShows);

        List<UserShowDTO> result = userShowService.getUserShows(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void testGetUserShows_Empty() {
        String userId = "user456";

        when(userShowRepository.findByUserIdWithShowDetails(userId))
                .thenReturn(new ArrayList<>());

        List<UserShowDTO> result = userShowService.getUserShows(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserShow_Found() {
        String userId = "user123";
        Integer tmdbId = 1396;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.of(mockUserShow));

        Optional<UserShowDTO> result = userShowService.getUserShow(userId, tmdbId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

    @Test
    void testGetUserShow_NotFound() {
        String userId = "user123";
        Integer tmdbId = 9999;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.empty());

        Optional<UserShowDTO> result = userShowService.getUserShow(userId, tmdbId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testIsShowSaved_True() {
        String userId = "user123";
        Integer tmdbId = 1396;

        when(userShowRepository.existsByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(true);

        boolean result = userShowService.isShowSaved(userId, tmdbId);

        assertTrue(result);
    }

    @Test
    void testIsShowSaved_False() {
        String userId = "user123";
        Integer tmdbId = 9999;

        when(userShowRepository.existsByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(false);

        boolean result = userShowService.isShowSaved(userId, tmdbId);

        assertFalse(result);
    }

    @Test
    void testRemoveShow_Success() {
        String userId = "user123";
        Integer tmdbId = 1396;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.of(mockUserShow));

        userShowService.removeShow(userId, tmdbId);

        verify(userShowRepository, times(1)).delete(mockUserShow);
    }

    @Test
    void testRemoveShow_NotFound() {
        String userId = "user123";
        Integer tmdbId = 9999;

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.empty());

        userShowService.removeShow(userId, tmdbId);

        verify(userShowRepository, never()).delete(any(UserShow.class));
    }

    @Test
    void testUpdateUserShow_Success() {
        String userId = "user123";
        Integer tmdbId = 1396;

        UserShowDTO updateData = new UserShowDTO();
        updateData.setPersonalRating(8);
        updateData.setNotes("Great show!");
        updateData.setWatchStatus("WATCHING");

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.of(mockUserShow));
        when(userShowRepository.save(any(UserShow.class))).thenReturn(mockUserShow);

        UserShowDTO result = userShowService.updateUserShow(userId, tmdbId, updateData);

        assertNotNull(result);
        verify(userShowRepository, times(1)).save(any(UserShow.class));
    }

    @Test
    void testUpdateUserShow_NotFound() {
        String userId = "user123";
        Integer tmdbId = 9999;

        UserShowDTO updateData = new UserShowDTO();

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userShowService.updateUserShow(userId, tmdbId, updateData);
        });
    }

    @Test
    void testUpdateUserShow_PartialUpdate() {
        String userId = "user123";
        Integer tmdbId = 1396;

        UserShowDTO updateData = new UserShowDTO();
        updateData.setPersonalRating(7);

        when(userShowRepository.findByUserIdAndTmdbId(userId, tmdbId))
                .thenReturn(Optional.of(mockUserShow));
        when(userShowRepository.save(any(UserShow.class))).thenReturn(mockUserShow);

        UserShowDTO result = userShowService.updateUserShow(userId, tmdbId, updateData);

        assertNotNull(result);
        verify(userShowRepository, times(1)).save(any(UserShow.class));
    }
}