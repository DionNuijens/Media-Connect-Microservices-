package org.example.mediaconnect.service;

import org.example.mediaconnect.dto.ShowDTO;
import org.example.mediaconnect.dto.TMDBShowDetail;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final TVShowService tvShowService;

    public ShowService(ShowRepository showRepository, TVShowService tvShowService) {
        this.showRepository = showRepository;
        this.tvShowService = tvShowService;
    }

    // Get or create show (from cache or TMDB)
    @Transactional
    public Show getOrCreateShow(Integer tmdbId) {
        Optional<Show> existing = showRepository.findByTmdbId(tmdbId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Fetch from TMDB
        TMDBShowDetail tmdbShow = tvShowService.getShowDetails(tmdbId);

        // Save to database
        Show show = new Show();
        show.setTmdbId(tmdbId);
        show.setName(tmdbShow.getName());
        show.setOverview(tmdbShow.getOverview());
        show.setPosterPath(tmdbShow.getPosterPath());
        show.setBackdropPath(tmdbShow.getBackdropPath());
        show.setVoteAverage(tmdbShow.getVoteAverage());
        show.setFirstAirDate(tmdbShow.getFirstAirDate());
        show.setNumberOfSeasons(tmdbShow.getNumberOfSeasons());
        show.setNumberOfEpisodes(tmdbShow.getNumberOfEpisodes());
        show.setStatus(tmdbShow.getStatus());
        show.setType(tmdbShow.getType());
        show.setCreatedAt(LocalDateTime.now());
        show.setUpdatedAt(LocalDateTime.now());

        return showRepository.save(show);
    }

    // Get show from cache
    @Transactional(readOnly = true)
    public Optional<Show> getShow(Integer tmdbId) {
        return showRepository.findByTmdbId(tmdbId);
    }

    // Refresh show data from TMDB (optional, for periodic updates)
    @Transactional
    public Show refreshShowData(Integer tmdbId) {
        Show show = showRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new RuntimeException("Show not found in cache"));

        TMDBShowDetail tmdbShow = tvShowService.getShowDetails(tmdbId);

        show.setName(tmdbShow.getName());
        show.setOverview(tmdbShow.getOverview());
        show.setVoteAverage(tmdbShow.getVoteAverage());
        show.setNumberOfSeasons(tmdbShow.getNumberOfSeasons());
        show.setNumberOfEpisodes(tmdbShow.getNumberOfEpisodes());
        show.setStatus(tmdbShow.getStatus());
        show.setUpdatedAt(LocalDateTime.now());

        return showRepository.save(show);
    }
}
