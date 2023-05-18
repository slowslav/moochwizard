package io.ssstoyanov.demo.media.music;

import com.fasterxml.jackson.databind.JsonNode;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import io.ssstoyanov.demo.media.music.api.OdesliApi;
import io.ssstoyanov.demo.media.music.api.SpotifyApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MusicService implements MediaService {

    private final SpotifyApi spotify;
    private final OdesliApi odesli;

    @Override
    public Content getContent(String url) {
        var json = odesli.getSongLinks(url);
        var previewUrl = spotify.getPreviewUrl(getSpotifyId(json));
        return new Content().setType(ContentType.AUDIO)
                .setText(makeText(json))
                .setName(makeName(json))
                .setMedia(Collections.singletonList(new Media().setThumbnailUrl(getThumbnailUrl(json))
                        .setType(MediaType.AUDIO)
                        .setUrl(previewUrl)));
    }

    private String makeName(JsonNode json) {
        return json.findValue("artistName").asText() + " - " + json.findValue("title").asText();
    }

    private String getThumbnailUrl(JsonNode json) {
        return json.findValue("thumbnailUrl").asText();
    }

    private String getSpotifyId(JsonNode json) {
        JsonNode entitiesByUniqueId = json.get("entitiesByUniqueId");
        Iterator<Map.Entry<String, JsonNode>> fields = entitiesByUniqueId.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().startsWith("SPOTIFY_SONG::")) {
                return field.getKey().split("SPOTIFY_SONG::", 2)[1];
            }
        }
        return null;
    }

    private String makeText(JsonNode json) {
        StringBuilder sb = new StringBuilder();
        if (json.findValue("spotify") != null) {
            sb.append(String.format("[Spotify](%s)", getUrl("spotify", json)));
        }
        if (json.findValue("yandex") != null) {
            sb.append(" | ");
            sb.append(String.format("[Yandex Music](%s)", getUrl("yandex", json)));
        }
        if (json.findValue("youtube") != null) {
            sb.append(" | ");
            sb.append(String.format("[YouTube](%s)", getUrl("youtube", json)));
        }
        if (json.findValue("itunes") != null) {
            sb.append(" | ");
            sb.append(String.format("[iTunes](%s)", getUrl("itunes", json)));
        }
        return sb.toString();
    }

    private String getUrl(String service, JsonNode json) {
        return json.findValue(service).findValue("url").asText();
    }

}
