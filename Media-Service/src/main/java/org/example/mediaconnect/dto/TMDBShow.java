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
public class TMDBShow {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("overview")
    private String overview;
    @JsonProperty("poster_path")
    private String poster_path;
    @JsonProperty("genre_ids")
    private List<Integer> genre_ids;
    @JsonProperty("vote_average")
    private Float vote_average;
    @JsonProperty("first_air_date")
    private String first_air_date;

    @JsonProperty("fullPosterUrl")
    private String fullPosterUrl;

    @JsonIgnore
    public void computeFullPosterUrl() {
        if (poster_path != null && !poster_path.isEmpty()) {
            this.fullPosterUrl = "https://image.tmdb.org/t/p/w500" + poster_path;
        } else {
            this.fullPosterUrl = null;
        }
    }
}