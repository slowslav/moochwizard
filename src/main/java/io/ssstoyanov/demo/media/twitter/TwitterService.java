package io.ssstoyanov.demo.media.twitter;

import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.MediaEntity;
import twitter4j.v1.Status;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class TwitterService implements MediaService {

    private static final String VIDEO = "video";
    private static final String PHOTO = "photo";
    private final Twitter twitter;

    @Override
    public Content getContent(String url) {
        try {
            Status tweet = twitter.v1().tweets().showStatus(extractTweetID(url));
            var type = getType(tweet);
            return parseContent(tweet, type);
        } catch (TwitterException e) {
            log.error(e);
        }
        return null;
    }

    private Content parseContent(Status tweet, ContentType type) {
        Content content = new Content();
        if (type == ContentType.TEXT) {
            content.setText(tweet.getText());
            content.setType(ContentType.TEXT);
            return content;
        }
        List<MediaEntity> entities = Arrays.asList(tweet.getMediaEntities());
        if (type == ContentType.PHOTO_VIDEO_SERIES) {
            content.setText(tweet.getText());
            content.setType(ContentType.PHOTO_VIDEO_SERIES);
            content.setMedia(entities.stream().filter(entity -> entity.getType().equals(VIDEO)).map(entity -> {
                Media media = new Media();
                media.setUrl(entity.getVideoVariants()[0].getUrl());
                media.setThumbnailUrl(entity.getMediaURLHttps());
                media.setType(MediaType.VIDEO);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            content.setMedia(entities.stream().filter(entity -> entity.getType().equals(PHOTO)).map(entity -> {
                Media media = new Media();
                media.setUrl(entity.getMediaURLHttps());
                media.setThumbnailUrl(entity.getMediaURLHttps());
                media.setType(MediaType.IMAGE);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            return content;
        }
        if (type == ContentType.VIDEO) {
            content.setText(tweet.getText());
            content.setType(ContentType.VIDEO);
            content.setMedia(entities.stream().map(entity -> {
                Media media = new Media();
                MediaEntity.Variant videoVariant = Arrays.stream(entity.getVideoVariants()).max(Comparator.comparingInt(MediaEntity.Variant::getBitrate)).get();
                media.setUrl(videoVariant.getUrl());
                media.setThumbnailUrl(entity.getMediaURL());
                media.setType(MediaType.VIDEO);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            return content;
        }
        if (type == ContentType.VIDEO_SERIES) {
            content.setText(tweet.getText());
            content.setType(ContentType.VIDEO_SERIES);
            content.setMedia(entities.stream().map(entity -> {
                Media media = new Media();
                media.setUrl(entity.getVideoVariants()[0].getUrl());
                media.setThumbnailUrl(entity.getMediaURLHttps());
                media.setType(MediaType.VIDEO);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            return content;
        }
        if (type == ContentType.PHOTO) {
            content.setText(tweet.getText());
            content.setType(ContentType.PHOTO);
            content.setMedia(entities.stream().map(entity -> {
                Media media = new Media();
                media.setUrl(entity.getMediaURLHttps());
                media.setThumbnailUrl(entity.getMediaURLHttps());
                media.setType(MediaType.IMAGE);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            return content;
        }
        if (type == ContentType.PHOTO_SERIES) {
            content.setText(tweet.getText());
            content.setType(ContentType.PHOTO_SERIES);
            content.setMedia(entities.stream().map(entity -> {
                Media media = new Media();
                media.setUrl(entity.getMediaURLHttps());
                media.setThumbnailUrl(entity.getMediaURLHttps());
                media.setType(MediaType.IMAGE);
                content.setText(content.getText().replace(entity.getURL(), ""));
                return media;
            }).toList());
            return content;
        }
        return content;
    }

    private ContentType getType(Status tweet) {
        MediaEntity[] entities = tweet.getMediaEntities();
        if (entities == null) {
            return ContentType.TEXT;
        }
        if (Arrays.stream(entities).anyMatch(media -> media.getType().equals(VIDEO)) &&
                Arrays.stream(entities).anyMatch(media -> media.getType().equals(PHOTO))) {
            return ContentType.PHOTO_VIDEO_SERIES;
        }
        if (Arrays.stream(entities).anyMatch(media -> media.getType().equals(VIDEO))) {
            return entities.length > 1 ? ContentType.VIDEO_SERIES : ContentType.VIDEO;
        }
        if (Arrays.stream(entities).anyMatch(media -> media.getType().equals(PHOTO))) {
            return entities.length > 1 ? ContentType.PHOTO_SERIES : ContentType.PHOTO;
        }
        return ContentType.TEXT;
    }

    private long extractTweetID(String url) {
        Pattern pattern = Pattern.compile("status/(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        } else {
            return 0L;
        }
    }

}
