package org.example.mediaconnect.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBWatchProviders {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("results")
    private Map<String, RegionProviders> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionProviders {
        @JsonProperty("link")
        private String link;

        @JsonProperty("flatrate")
        private List<Provider> flatrate;  // Subscription services (Netflix, Disney+, etc.)

        @JsonProperty("buy")
        private List<Provider> buy;  // Purchase options

        @JsonProperty("rent")
        private List<Provider> rent;  // Rental options

        @JsonProperty("free")
        private List<Provider> free;  // Free with ads
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Provider {
        @JsonProperty("logo_path")
        private String logoPath;

        @JsonProperty("provider_id")
        private Integer providerId;

        @JsonProperty("provider_name")
        private String providerName;

        @JsonProperty("display_priority")
        private Integer displayPriority;

        @JsonIgnore
        public String getFullLogoUrl() {
            if (logoPath == null || logoPath.isEmpty()) {
                return null;
            }
            return "https://image.tmdb.org/t/p/original" + logoPath;
        }
    }
}