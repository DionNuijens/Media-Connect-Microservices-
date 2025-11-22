package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.TMDBShowDetail;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private TVShowService tvShowService;

    @InjectMocks
    private ShowService showService;

    private Show mockShow;
    private TMDBShowDetail mockTmdbShow;

    @BeforeEach
    public void setUp() {
        mockShow = new Show();
        mockShow.setTmdbId(1399);
        mockShow.setName("Breaking Bad");
        mockShow.setOverview("A high school chemistry teacher...");
        mockShow.setPosterPath("/ggFHVNvVeFwOOSN3DXsFIAGVM1.jpg");
        mockShow.setBackdropPath("/xnopI5Cer5bBjHJsXiFQa9xAaP.jpg");
        mockShow.setVoteAverage(9.5f);
        mockShow.setFirstAirDate("2008-01-20");
        mockShow.setNumberOfSeasons(5);
        mockShow.setNumberOfEpisodes(62);
        mockShow.setStatus("Ended");
        mockShow.setType("Scripted");
        mockShow.setCreatedAt(LocalDateTime.now());
        mockShow.setUpdatedAt(LocalDateTime.now());

        mockTmdbShow = new TMDBShowDetail();
        mockTmdbShow.setName("Breaking Bad");
        mockTmdbShow.setOverview("A high school chemistry teacher...");
        mockTmdbShow.setPosterPath("/ggFHVNvVeFwOOSN3DXsFIAGVM1.jpg");
        mockTmdbShow.setBackdropPath("/xnopI5Cer5bBjHJsXiFQa9xAaP.jpg");
        mockTmdbShow.setVoteAverage(9.5f);
        mockTmdbShow.setFirstAirDate("2008-01-20");
        mockTmdbShow.setNumberOfSeasons(5);
        mockTmdbShow.setNumberOfEpisodes(62);
        mockTmdbShow.setStatus("Ended");
        mockTmdbShow.setType("Scripted");
    }

    @Test
    public void testGetOrCreateShow_ShowExists() {
        // Arrange
        when(showRepository.findByTmdbId(1399)).thenReturn(Optional.of(mockShow));

        // Act
        Show result = showService.getOrCreateShow(1399);

        // Assert
        assertNotNull(result);
        assertEquals(1399, result.getTmdbId());
        assertEquals("Breaking Bad", result.getName());
        verify(showRepository, times(1)).findByTmdbId(1399);
        verify(tvShowService, never()).getShowDetails(anyInt());
    }

    @Test
    public void testGetOrCreateShow_ShowDoesNotExist() {
        // Arrange
        when(showRepository.findByTmdbId(1399)).thenReturn(Optional.empty());
        when(tvShowService.getShowDetails(1399)).thenReturn(mockTmdbShow);
        when(showRepository.save(any(Show.class))).thenReturn(mockShow);

        // Act
        Show result = showService.getOrCreateShow(1399);

        // Assert
        assertNotNull(result);
        assertEquals(1399, result.getTmdbId());
        assertEquals("Breaking Bad", result.getName());
        verify(showRepository, times(1)).findByTmdbId(1399);
        verify(tvShowService, times(1)).getShowDetails(1399);
        verify(showRepository, times(1)).save(any(Show.class));
    }

    @Test
    public void testGetShow_ShowExists() {
        // Arrange
        when(showRepository.findByTmdbId(1399)).thenReturn(Optional.of(mockShow));

        // Act
        Optional<Show> result = showService.getShow(1399);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Breaking Bad", result.get().getName());
        verify(showRepository, times(1)).findByTmdbId(1399);
    }

    @Test
    public void testGetShow_ShowDoesNotExist() {
        // Arrange
        when(showRepository.findByTmdbId(9999)).thenReturn(Optional.empty());

        // Act
        Optional<Show> result = showService.getShow(9999);

        // Assert
        assertFalse(result.isPresent());
        verify(showRepository, times(1)).findByTmdbId(9999);
    }

    @Test
    public void testRefreshShowData_Success() {
        // Arrange
        when(showRepository.findByTmdbId(1399)).thenReturn(Optional.of(mockShow));
        when(tvShowService.getShowDetails(1399)).thenReturn(mockTmdbShow);
        when(showRepository.save(any(Show.class))).thenReturn(mockShow);

        // Act
        Show result = showService.refreshShowData(1399);

        // Assert
        assertNotNull(result);
        assertEquals("Breaking Bad", result.getName());
        verify(showRepository, times(1)).findByTmdbId(1399);
        verify(tvShowService, times(1)).getShowDetails(1399);
        verify(showRepository, times(1)).save(any(Show.class));
    }

    @Test
    public void testRefreshShowData_ShowNotFound() {
        // Arrange
        when(showRepository.findByTmdbId(9999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> showService.refreshShowData(9999));
        verify(showRepository, times(1)).findByTmdbId(9999);
        verify(tvShowService, never()).getShowDetails(anyInt());
    }
}
