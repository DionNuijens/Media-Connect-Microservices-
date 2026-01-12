package org.example.mediaconnect.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBShowDetail {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    @JsonProperty("genres")
    private List<Genre> genres;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("first_air_date")
    private String firstAirDate;

    @JsonProperty("number_of_seasons")
    private Integer numberOfSeasons;

    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("created_by")
    private List<Creator> createdBy;

    @JsonProperty("networks")
    private List<Network> networks;

    // Nested classes for complex fields
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Genre {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creator {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("profile_path")
        private String profilePath;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Network {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("logo_path")
        private String logoPath;

        @JsonProperty("origin_country")
        private String originCountry;
    }

    // Helper methods for full URLs
    @JsonProperty("fullPosterUrl")
    public String getFullPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }


    @JsonProperty("fullBackdropUrl")
    public String getFullBackdropUrl() {
        if (backdropPath == null || backdropPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/original" + backdropPath;
    }
}