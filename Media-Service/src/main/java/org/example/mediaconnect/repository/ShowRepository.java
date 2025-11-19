package org.example.mediaconnect.repository;

import org.example.mediaconnect.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Integer> {
    Optional<Show> findByTmdbId(Integer tmdbId);
    boolean existsByTmdbId(Integer tmdbId);
}
