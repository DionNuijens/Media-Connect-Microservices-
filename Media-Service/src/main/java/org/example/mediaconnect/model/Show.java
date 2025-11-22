package org.example.mediaconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
//@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    @Id
    @Column(name = "tmdb_id")
    private Integer tmdbId;  // Primary key is TMDB ID

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String overview;

    private String posterPath;
    private String backdropPath;
    private Float voteAverage;
    private String firstAirDate;
    private Integer numberOfSeasons;
    private Integer numberOfEpisodes;
    private String status;
    private String type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
