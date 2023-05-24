package io.ssstoyanov.mooch.telegram.service;

import io.ssstoyanov.mooch.Utils;
import io.ssstoyanov.mooch.event.*;
import io.ssstoyanov.mooch.media.entity.Media;
import io.ssstoyanov.mooch.media.entity.MediaType;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TelegramMessageService extends TelegramService {

    private final ApplicationEventPublisher publisher;

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).PHOTO")
    public void preparePhoto(ParsedContentEvent event) {
        var message = event.getMessage();
        var content = event.getObject();
        var imageUrl = content.getFirstMedia().getUrl();

        var photo = new SendPhoto();
        photo.setPhoto(new InputFile().setMedia(Utils.getInputStream(imageUrl), String.valueOf(UUID.randomUUID())));
        photo.setChatId(message.getMessage().getChatId());
        setCaption(event, message, photo);
        photo.setParseMode(MARKDOWN);

        publisher.publishEvent(new SendPhotoEvent(photo));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).VIDEO")
    public void prepareVideo(ParsedContentEvent event) {
        var message = event.getMessage();
        var content = event.getObject();
        var videoUrl = content.getFirstMedia().getUrl();
        var thumbnailUrl = content.getFirstMedia().getThumbnailUrl();
        var video = new SendVideo();
        if (StringUtils.hasText(thumbnailUrl))
            video.setThumb(new InputFile().setMedia(Utils.getInputStream(thumbnailUrl), String.valueOf(UUID.randomUUID())));
        video.setVideo(new InputFile().setMedia(Utils.getInputStream(videoUrl), String.valueOf(UUID.randomUUID())));
        video.setChatId(message.getMessage().getChatId());
        if (event.getObject().getText() != null) {
            video.setCaption(setSearchRequest(message) + "\n" + event.getObject().getText());
        } else {
            video.setCaption(setSearchRequest(message));
        }
        video.setParseMode(MARKDOWN);
        publisher.publishEvent(new SendVideoEvent(video));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).AUDIO")
    public void prepareAudio(ParsedContentEvent event) {
        var message = event.getMessage();
        var content = event.getObject();
        var audioUrl = content.getFirstMedia().getUrl();
        var thumbnailUrl = content.getFirstMedia().getThumbnailUrl();

        var audio = new SendAudio();
        audio.setCaption(setSearchRequestAudio(message) + content.getText());
        audio.setAudio(new InputFile().setMedia(Utils.getInputStream(audioUrl), content.getName()));
        audio.setThumb(new InputFile().setMedia(Utils.getInputStream(thumbnailUrl), UUID.randomUUID().toString()));
        audio.setChatId(message.getMessage().getChatId());
        audio.setParseMode(MARKDOWN);

        publisher.publishEvent(new SendAudioEvent(audio));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).PHOTO_SERIES")
    public void preparePhotoSeries(ParsedContentEvent event) {
        var message = event.getMessage();
        var content = event.getObject();
        var mediaGroup = new SendMediaGroup();
        List<Media> media = content.getMedia();

        var photos = media.stream().map(Media::getUrl).map(mediaUrl -> {
            var photo = new InputMediaPhoto();
            photo.setMedia(Utils.getInputStream(mediaUrl), String.valueOf(UUID.randomUUID()));
            photo.setParseMode(MARKDOWN);
            return (InputMedia) photo;
        }).toList();
        setCaption(event, message, photos);
        mediaGroup.setMedias(photos);
        mediaGroup.setChatId(message.getMessage().getChatId());

        publisher.publishEvent(new SendMediaGroupEvent(mediaGroup));
    }


    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).VIDEO_SERIES")
    public void prepareVideoSeries(ParsedContentEvent event) {
        var message = event.getMessage();
        var content = event.getObject();
        var mediaGroup = new SendMediaGroup();

        var videos = content.getMedia().stream().map(media -> {
            var video = new InputMediaVideo();
            var thumbnailUrl = media.getThumbnailUrl();
            var videoUrl = media.getUrl();
            if (StringUtils.hasText(thumbnailUrl)) {
                video.setThumb(new InputFile().setMedia(Utils.getInputStream(thumbnailUrl), String.valueOf(UUID.randomUUID())));
            }
            video.setMedia(Utils.getInputStream(videoUrl), String.valueOf(UUID.randomUUID()));
            video.setParseMode(MARKDOWN);
            return (InputMedia) video;
        }).toList();

        mediaGroup.setMedias(videos);
        mediaGroup.setChatId(message.getMessage().getChatId());
        publisher.publishEvent(new SendMediaGroupEvent(mediaGroup));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).TEXT")
    public void prepareText(ParsedContentEvent event) {
        var update = event.getMessage();
        var message = new SendMessage();
        var content = event.getObject();

        setCaption(update, message, content);
        message.setChatId(update.getMessage().getChatId());

        publisher.publishEvent(new SendMessageEvent(message));
    }


    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).PHOTO_VIDEO_SERIES")
    public void preparePhotoVideoSeries(ParsedContentEvent event) {
        var message = event.getMessage();
        var mediaGroup = new SendMediaGroup();
        var content = event.getObject();

        List<InputMedia> medias = content.getMedia().stream().map(media -> {
            InputMedia inputMedia;
            if (media.getType() == MediaType.VIDEO) {
                inputMedia = new InputMediaVideo();
                inputMedia.setMedia(Utils.getInputStream(media.getUrl()), String.valueOf(UUID.randomUUID()));
            } else {
                inputMedia = new InputMediaPhoto();
                inputMedia.setMedia(Utils.getInputStream(media.getUrl()), String.valueOf(UUID.randomUUID()));
            }
            inputMedia.setParseMode(MARKDOWN);
            return inputMedia;
        }).toList();
        setCaption(event, message, medias);
        mediaGroup.setMedias(medias);
        mediaGroup.setChatId(message.getMessage().getChatId());

        publisher.publishEvent(new SendMediaGroupEvent(mediaGroup));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "!(#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).ERROR)")
    public void deleteMessage(ParsedContentEvent event) {
        var message = event.getMessage();
        var delete = new DeleteMessage();
        delete.setMessageId(message.getMessage().getMessageId());
        delete.setChatId(message.getMessage().getChatId());
        publisher.publishEvent(new DeleteMessageEvent(delete));
    }

    @EventListener(value = ParsedContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).ERROR")
    public void prepareError(ParsedContentEvent event) {
        Update update = event.getMessage();
        var message = new SendMessage();
        message.setText(event.getObject().getText());
        message.setChatId(update.getMessage().getChatId());
        publisher.publishEvent(new SendTextEvent(message));
    }

}
