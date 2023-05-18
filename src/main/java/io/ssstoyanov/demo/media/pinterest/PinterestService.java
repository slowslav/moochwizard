package io.ssstoyanov.demo.media.pinterest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssstoyanov.demo.Utils;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class PinterestService implements MediaService {

    private static final Pattern jsonExtractor = Pattern.compile("(\\{.+})");

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SCRIPT_VALUE = "__PWS_DATA__";
    private static final String SCRIPT_KEY = "id";
    private static final String PAGES_FIELD = "pages";
    private static final String VIDEO_TAG = "video";
    private static final String VIDEO_LIST_VALUE = "video_list";
    private static final String V_EXP_3_VALUE = "V_EXP3";
    private static final String THUMBNAIL_VALUE = "thumbnail";
    private static final String URL_VALUE = "url";
    private static final String BLOCKS_VALUE = "blocks";

    @NotNull
    private static Content setAndReturn(ContentType type, Set<String> videoUrls) {
        return new Content().setMedia(videoUrls.stream()
                        .map(url -> new Media(MediaType.IMAGE, url).setThumbnailUrl(url))
                        .toList())
                .setType(type);
    }

    @NotNull
    private static Content setAndReturnWithThumb(ContentType type, Set<AbstractMap.SimpleEntry<String, String>> urls) {
        return new Content().setMedia(urls.stream()
                        .map(url -> new Media(MediaType.VIDEO, url.getKey(), url.getValue()))
                        .toList())
                .setType(type);
    }

    @Nullable
    public Content getContent(String url) {
        try {
            url = normalizeUrl(url);
            var page = Utils.getPage(url);
            var type = getType(page);
            return parseContent(page, type);
        } catch (JsonProcessingException ex) {
            log.error("Error occurred while parsing pinterest {}", url, ex);
        } catch (IOException ex) {
            log.error("Error occurred while getting pinterest {}", url, ex);
        }
        return null;
    }

    private String normalizeUrl(String url) throws IOException {
        if (url.contains("pinterest.com/pin/")) {
            return url;
        }
        var page = Utils.getPage(url);
        var script = String.valueOf(page.getElementsByAttributeValue(SCRIPT_KEY, SCRIPT_VALUE));
        url = extractUrl(script);
        return url;
    }

    @Nullable
    private Content parseContent(Document page, ContentType type) throws JsonProcessingException {
        if (type == ContentType.VIDEO) {
            JsonNode json = extractJson(page);
            var videoUrl = json.findValues(VIDEO_LIST_VALUE).get(0).findValue(URL_VALUE).asText();
            var thumbnailUrl = json.findValues(VIDEO_LIST_VALUE).get(0).findValue(THUMBNAIL_VALUE).asText();
            return setAndReturnWithThumb(type, Set.of(new AbstractMap.SimpleEntry<>(videoUrl, thumbnailUrl)));
        }
        if (type == ContentType.PHOTO) {
            JsonNode json = extractJson(page);
            var imageUrl = json.findValue("images").findValue("orig").get(URL_VALUE).asText();
            return setAndReturn(type, Collections.singleton(imageUrl));
        }
        if (type == ContentType.PHOTO_SERIES) {
            JsonNode json = extractJson(page);
            Set<String> imageUrls = new HashSet<>();
            var pages = json.findValues(PAGES_FIELD).get(0);
            for (JsonNode node : pages) {
                imageUrls.add(node.findValue("image").findValue(URL_VALUE).asText());
            }
            return setAndReturn(type, imageUrls);
        }
        if (type == ContentType.VIDEO_SERIES) {
            JsonNode json = extractJson(page);
            Set<AbstractMap.SimpleEntry<String, String>> videoUrls = new HashSet<>();
            var pages = json.findValues(PAGES_FIELD).get(0);
            if (pages.size() == 1) {
                type = ContentType.VIDEO; // если вдруг у нас одна страница с видео, костыль
            }
            for (JsonNode node : pages) {
                videoUrls.add(new AbstractMap.SimpleEntry<>(node.get(BLOCKS_VALUE).findValue(VIDEO_LIST_VALUE).get(V_EXP_3_VALUE).get(URL_VALUE).asText(),
                        node.get(BLOCKS_VALUE).findValue(VIDEO_LIST_VALUE).get(V_EXP_3_VALUE).get(THUMBNAIL_VALUE).asText()));
            }
            return PinterestService.setAndReturnWithThumb(type, videoUrls);
        }
        return null;
    }

    @NotNull
    private ContentType getType(Document page) {
        var isVideo = page.getElementsByTag(VIDEO_TAG);
        if (isVideo.isEmpty()) {
            return !page.toString().contains(PAGES_FIELD) ? ContentType.PHOTO : ContentType.PHOTO_SERIES;
        }
        return !page.toString().contains(PAGES_FIELD) ? ContentType.VIDEO : ContentType.VIDEO_SERIES;
    }

    @NotNull
    private JsonNode extractJson(Document page) throws JsonProcessingException {
        String script = page.getElementsByAttributeValue(SCRIPT_KEY, SCRIPT_VALUE).toString();
        Matcher matcher = jsonExtractor.matcher(script);
        if (matcher.find()) {
            var jsonString = matcher.group();
            return mapper.readTree(jsonString);
        } else {
            String message = "Error occurred while parsing pinterest json";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    @NotNull
    private String extractUrl(String script) throws JsonProcessingException {
        var matcher = jsonExtractor.matcher(script);
        if (matcher.find()) {
            var jsonString = matcher.group();
            var json = mapper.readTree(jsonString);
            String url = json.get("props").get("context").get("current_url").asText();
            return url.substring(0, 49);
        } else {
            String message = "Error occurred while extract pinterest url";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

}
