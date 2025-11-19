package org.example.mediaconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShowDTO {
    private Long id;
    private String userId;
    private Integer tmdbId;
    private Integer personalRating;
    private String notes;
    private String watchStatus;
    private LocalDateTime savedAt;
    private LocalDateTime updatedAt;

    // Include full show details
    private ShowDTO show;
}
