package org.example.mediaconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_shows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tmdb_id"})
})
//@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "tmdb_id", nullable = false)
    private Integer tmdbId;

    // ✨ USER-SPECIFIC DATA (extensible for future fields like ratings, notes, etc.)
    private Integer personalRating;  // User's rating (1-10), nullable if not rated
    private String notes;             // Optional personal notes
    private String watchStatus;       // "watching", "completed", "planning", null

    @Column(nullable = false)
    private LocalDateTime savedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Foreign key reference to Show (optional, for convenience)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tmdb_id", insertable = false, updatable = false)
    private Show show;
}

