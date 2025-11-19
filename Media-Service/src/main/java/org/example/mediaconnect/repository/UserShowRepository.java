package org.example.mediaconnect.repository;

import org.example.mediaconnect.model.UserShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserShowRepository extends JpaRepository<UserShow, Long> {

    List<UserShow> findByUserId(String userId);

    Optional<UserShow> findByUserIdAndTmdbId(String userId, Integer tmdbId);

    boolean existsByUserIdAndTmdbId(String userId, Integer tmdbId);

    @Query("SELECT us FROM UserShow us JOIN FETCH us.show WHERE us.userId = :userId")
    List<UserShow> findByUserIdWithShowDetails(String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserShow us WHERE us.userId = :userId")
    int deleteByUserId(String userId);
}