package org.example.mediaconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    @Bean
    public WebClient tmdbClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.themoviedb.org/3")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Authorization", "Bearer " + tmdbApiKey)
                .build();
    }
}
