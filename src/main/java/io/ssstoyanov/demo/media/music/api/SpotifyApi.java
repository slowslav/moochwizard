package io.ssstoyanov.demo.media.music.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Log4j2
@NoArgsConstructor
public class SpotifyApi {
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search";
    private static final String TRACK_URL = "https://api.spotify.com/v1/tracks/";

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();

    private String clientId;
    private String clientSecret;
    private String accessToken;

    public SpotifyApi(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        obtainAccessToken();
    }

    @Nullable
    public String searchTrack(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("q", query)
                .queryParam("type", "track")
                .queryParam("limit", "1");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rest.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        try {
            JsonNode json = mapper.readTree(response.getBody());
            JsonNode track = json.get("tracks").get("items").get(0);
            return track.get("id").asText();
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return null;
    }

    @Nullable
    public String getPreviewUrl(String trackId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rest.exchange(TRACK_URL + trackId, HttpMethod.GET, entity, String.class);

        try {
            JsonNode json = mapper.readTree(response.getBody());
            return json.get("preview_url").asText();
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return null;
    }

    @Scheduled(fixedRate = 3600000)
    private void obtainAccessToken() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rest.postForEntity(TOKEN_URL, request, String.class);
        try {
            JsonNode json = mapper.readTree(response.getBody());
            accessToken = json.get("access_token").asText();
        } catch (IOException e) {
            log.error(e);
        }
    }

}
