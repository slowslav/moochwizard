package io.ssstoyanov.demo.media.music.config;

import io.ssstoyanov.demo.media.music.api.SpotifyApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyConfig {

    @Value("${spotify.client.id}")
    private String clientId;
    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Bean
    public SpotifyApi spotifyApi() {
        return new SpotifyApi(clientId, clientSecret);
    }

}
