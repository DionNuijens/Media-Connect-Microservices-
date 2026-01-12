package org.example.mediaconnect.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDTO {
    private Integer tmdbId;
    private String name;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private Float voteAverage;
    private String firstAirDate;
    private Integer numberOfSeasons;
    private Integer numberOfEpisodes;
    private String status;
    private String type;

    @JsonIgnore
    public String getFullPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    @JsonIgnore
    public String getFullBackdropUrl() {
        if (backdropPath == null || backdropPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/original" + backdropPath;
    }
}
