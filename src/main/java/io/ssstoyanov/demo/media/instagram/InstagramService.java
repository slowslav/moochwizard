package io.ssstoyanov.demo.media.instagram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssstoyanov.demo.Utils;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class InstagramService implements MediaService {

    private static final Pattern jsonExtractor = Pattern.compile("(\\{.+})");

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CAROUSEL_MEDIA = "carousel_media";
    private static final String IMAGE_VERSIONS_2 = "image_versions2";
    private static final String JSON_PATH = "?__a=1&__d=dis";
    private static final String VIDEO_VERSIONS = "video_versions";
    private static final String URL = "url";
    private static final String CANDIDATES = "candidates";
    private static final String CAPTION = "caption";
    private static final String TEXT = "text";
    private static final String ITEMS = "items";
    private static final String P = "p";
    private static final String REELS = "reels";
    private static final String REELS_VIDEOS = "reels/videos";
    private static final String REEL = "reel";
    @Value("${instagram.token}")
    private String INSTAGRAM_TOKEN;

    @Nullable
    @Override
    public Content getContent(String url) {
        try {
            if (url.contains("instagram.com/reel")) {
                url = fixUrl(url);
                String body = Utils.getInstagramJson(url + JSON_PATH, INSTAGRAM_TOKEN);
                JsonNode json = extractJson(body);
                var mediaUrl = json.findValue(VIDEO_VERSIONS).get(0).findValue(URL).asText();
                var thumbUrl = json.findValue(IMAGE_VERSIONS_2).findValue(CANDIDATES).get(0).findValue(URL).asText();
                return new Content().setType(ContentType.VIDEO)
                        .setMedia(Collections.singletonList(new Media().setType(MediaType.VIDEO).setUrl(mediaUrl).setThumbnailUrl(thumbUrl)));
            }
            if (url.contains("instagram.com/p")) {
                String body = Utils.getInstagramJson(url + JSON_PATH, INSTAGRAM_TOKEN);
                JsonNode json = extractJson(body);
                ContentType type = getType(json);
                return parseContent(json, type);
            }
            return null;
        } catch (JsonProcessingException ex) {
            log.error("Error occurred while parsing instagram {}", url, ex);
        } catch (IOException ex) {
            log.error("Error occurred while getting instagram {}", url, ex);
        }
        return null;
    }

    private Content parseContent(JsonNode json, ContentType type) {
        if (type == ContentType.PHOTO_VIDEO_SERIES) {
            var content = new Content().setType(ContentType.PHOTO_VIDEO_SERIES);
            json.findValue(CAROUSEL_MEDIA).forEach(media -> {
                String url;
                if (media.findValue(VIDEO_VERSIONS) != null) {
                    url = media.findValue(VIDEO_VERSIONS).get(0).findValue(URL).asText();
                    content.addMedia(new Media().setType(MediaType.VIDEO).setUrl(url));
                } else {
                    url = media.findValue(IMAGE_VERSIONS_2).findValue(CANDIDATES).get(0).findValue(URL).asText();
                    content.addMedia(new Media().setType(MediaType.IMAGE).setUrl(url));
                }
            });
            content.setText(json.findValue(CAPTION).findValue(TEXT).asText());
            return content;
        }
        if (type == ContentType.PHOTO_SERIES) {
            var content = new Content().setType(ContentType.PHOTO_SERIES);
            json.findValue(CAROUSEL_MEDIA).forEach(media -> {
                String url = media.findValue(IMAGE_VERSIONS_2).findValue(CANDIDATES).get(0).findValue(URL).asText();
                String thumbnailUrl = media.findValue(IMAGE_VERSIONS_2).findValue(CANDIDATES).get(media.size() - 1).findValue(URL).asText();
                content.addMedia(new Media().setType(MediaType.IMAGE).setUrl(url).setThumbnailUrl(thumbnailUrl));
            });
            content.setText(json.findValue(CAPTION).findValue(TEXT).asText());
            return content;
        }
        if (type == ContentType.VIDEO_SERIES) {
            var content = new Content().setType(ContentType.VIDEO_SERIES);
            json.findValue(CAROUSEL_MEDIA).forEach(media -> {
                String url = media.findValue(VIDEO_VERSIONS).get(0).findValue(URL).asText();
                content.addMedia(new Media().setType(MediaType.VIDEO).setUrl(url));
            });
            content.setText(json.findValue(CAPTION).findValue(TEXT).asText());
            return content;
        }
        if (type == ContentType.VIDEO) {
            Content content = new Content().setType(ContentType.VIDEO).setMedia(Collections.singletonList(new Media().setType(MediaType.IMAGE).setUrl(json.get(ITEMS)
                    .get(0).get(IMAGE_VERSIONS_2)
                    .get(CANDIDATES).get(0)
                    .get(URL).asText())));
            content.setText(json.findValue(CAPTION).findValue(TEXT).asText());
            return content;
        }
        JsonNode jsonNode = json.findValue(IMAGE_VERSIONS_2).get(CANDIDATES);
        Content content = new Content().setType(ContentType.PHOTO).setMedia(Collections.singletonList(new Media()
                .setType(MediaType.IMAGE)
                .setThumbnailUrl(jsonNode.get(jsonNode.size() - 1).get(URL).asText())
                .setUrl(json.findValue(IMAGE_VERSIONS_2).get(CANDIDATES).get(0).get(URL).asText())));
        content.setText(json.findValue(CAPTION).findValue(TEXT).asText());
        return content;
    }

    private ContentType getType(JsonNode json) {
        if (json.findValue(CAROUSEL_MEDIA) != null) {
            if (json.findValue(VIDEO_VERSIONS) != null && json.findValue(IMAGE_VERSIONS_2) != null)
                return ContentType.PHOTO_VIDEO_SERIES;
            if (json.findValue(VIDEO_VERSIONS) != null)
                return ContentType.VIDEO_SERIES;
            return ContentType.PHOTO_SERIES;
        }
        if (json.findValue(VIDEO_VERSIONS) != null)
            return ContentType.VIDEO;
        return ContentType.PHOTO;
    }

    private String fixUrl(String url) {
        if (url.contains(REELS_VIDEOS)) {
            return url.replace(REELS_VIDEOS, P);
        }
        if (url.contains(REELS)) {
            return url.replace(REELS, P);
        }
        return url.replace(REEL, P);
    }

    @NotNull
    private JsonNode extractJson(String body) throws JsonProcessingException {
        var matcher = jsonExtractor.matcher(body);
        if (matcher.find()) {
            var jsonString = matcher.group();
            return mapper.readTree(jsonString);
        } else {
            String message = "Error occurred while extract instagram url";
            log.error(message);
            throw new RuntimeException(message);
        }
    }
}
