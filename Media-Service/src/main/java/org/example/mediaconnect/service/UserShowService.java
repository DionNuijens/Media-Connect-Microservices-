package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.ShowDTO;
import org.example.mediaconnect.dto.UserShowDTO;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.model.UserShow;
import org.example.mediaconnect.repository.UserShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserShowService {

    private final UserShowRepository userShowRepository;
    private final ShowService showService;

    public UserShowService(UserShowRepository userShowRepository, ShowService showService) {
        this.userShowRepository = userShowRepository;
        this.showService = showService;
    }

    // Save show to user's list
    @Transactional
    public UserShowDTO saveShow(String userId, Integer tmdbId) {
        // Check if already saved
        Optional<UserShow> existing = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId);
        if (existing.isPresent()) {
            return mapToDTO(existing.get());
        }

        // Get or create show in cache
        Show show = showService.getOrCreateShow(tmdbId);

        // Create user-show relationship
        UserShow userShow = new UserShow();
        userShow.setUserId(userId);
        userShow.setTmdbId(tmdbId);
        userShow.setSavedAt(LocalDateTime.now());
        userShow.setUpdatedAt(LocalDateTime.now());
        userShow.setShow(show);

        UserShow saved = userShowRepository.save(userShow);
        return mapToDTO(saved);
    }

    // Get user's saved shows
    @Transactional(readOnly = true)
    public List<UserShowDTO> getUserShows(String userId) {
        return userShowRepository.findByUserIdWithShowDetails(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get single user-show
    @Transactional(readOnly = true)
    public Optional<UserShowDTO> getUserShow(String userId, Integer tmdbId) {
        return userShowRepository.findByUserIdAndTmdbId(userId, tmdbId)
                .map(this::mapToDTO);
    }

    // Check if show is saved
    @Transactional(readOnly = true)
    public boolean isShowSaved(String userId, Integer tmdbId) {
        return userShowRepository.existsByUserIdAndTmdbId(userId, tmdbId);
    }

    // Remove show from user's list
    @Transactional
    public void removeShow(String userId, Integer tmdbId) {
        Optional<UserShow> userShow = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId);
        userShow.ifPresent(userShowRepository::delete);
    }

    // Update user-specific fields (rating, notes, watchStatus, etc.)
    @Transactional
    public UserShowDTO updateUserShow(String userId, Integer tmdbId, UserShowDTO updateData) {
        UserShow userShow = userShowRepository.findByUserIdAndTmdbId(userId, tmdbId)
                .orElseThrow(() -> new RuntimeException("User show not found"));

        if (updateData.getPersonalRating() != null) {
            userShow.setPersonalRating(updateData.getPersonalRating());
        }
        if (updateData.getNotes() != null) {
            userShow.setNotes(updateData.getNotes());
        }
        if (updateData.getWatchStatus() != null) {
            userShow.setWatchStatus(updateData.getWatchStatus());
        }

        userShow.setUpdatedAt(LocalDateTime.now());
        UserShow updated = userShowRepository.save(userShow);
        return mapToDTO(updated);
    }

    private UserShowDTO mapToDTO(UserShow userShow) {
        UserShowDTO dto = new UserShowDTO();
        dto.setId(userShow.getId());
        dto.setUserId(userShow.getUserId());
        dto.setTmdbId(userShow.getTmdbId());
        dto.setPersonalRating(userShow.getPersonalRating());
        dto.setNotes(userShow.getNotes());
        dto.setWatchStatus(userShow.getWatchStatus());
        dto.setSavedAt(userShow.getSavedAt());
        dto.setUpdatedAt(userShow.getUpdatedAt());

        // Map show if it exists
        if (userShow.getShow() != null) {
            Show show = userShow.getShow();
            ShowDTO showDTO = new ShowDTO();
            showDTO.setTmdbId(show.getTmdbId());
            showDTO.setName(show.getName());
            showDTO.setOverview(show.getOverview());
            showDTO.setPosterPath(show.getPosterPath());
            showDTO.setBackdropPath(show.getBackdropPath());
            showDTO.setVoteAverage(show.getVoteAverage());
            showDTO.setFirstAirDate(show.getFirstAirDate());
            showDTO.setNumberOfSeasons(show.getNumberOfSeasons());
            showDTO.setNumberOfEpisodes(show.getNumberOfEpisodes());
            showDTO.setStatus(show.getStatus());
            showDTO.setType(show.getType());
            dto.setShow(showDTO);
        }

        return dto;
    }
}
