package io.ssstoyanov.demo.media.music.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Service
@RequiredArgsConstructor
public class OdesliApi {
    private static final String API_URL = "https://api.song.link/v1-alpha.1/links";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public JsonNode getSongLinks(String url) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("url", url);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return null;
    }
}
