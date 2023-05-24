package io.ssstoyanov.mooch.telegram.service;

import io.ssstoyanov.mooch.event.AnswerInlineQueryEvent;
import io.ssstoyanov.mooch.event.ParsedInlineContentEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultAudio;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultVideo;

import java.util.Collections;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TelegramInlineService extends TelegramService {

    private final ApplicationEventPublisher publisher;

    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).PHOTO")
    public void preparePhoto(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var object = event.getObject();
        var url = object.getFirstMedia().getUrl();
        var thumb = object.getFirstMedia().getThumbnailUrl();

        var res = new InlineQueryResultPhoto();
        setPhotoFields(event, update, url, thumb, res);
        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setResults(Collections.singletonList(res));
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }

    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).VIDEO")
    public void prepareVideo(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var content = event.getObject();
        var url = content.getFirstMedia().getUrl();
        var thumb = content.getFirstMedia().getThumbnailUrl();

        var res = new InlineQueryResultVideo();
        res.setVideoUrl(url);
        res.setThumbUrl(thumb);
        res.setId(UUID.randomUUID().toString());
        setCaption(event, update, res);
        res.setTitle(UUID.randomUUID().toString());
        res.setMimeType(VIDEO_MP_4);
        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setResults(Collections.singletonList(res));
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }


    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).AUDIO")
    public void prepareAudio(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var content = event.getObject();
        var url = content.getFirstMedia().getUrl();
        var res = new InlineQueryResultAudio();

        if (url != null) {
            res.setAudioUrl(url);
            setCaption(update, content, res);
            res.setParseMode(MARKDOWN);
            res.setId(UUID.randomUUID().toString());
            res.setTitle(content.getName());
            answer.setResults(Collections.singletonList(res));
        } else {
            var inputMessageContent = new InputTextMessageContent();
            inputMessageContent.setParseMode(MARKDOWN);
            inputMessageContent.setMessageText(setSearchRequestInlineAudio(update) + content.getText());
            inputMessageContent.setDisableWebPagePreview(false);
            res.setInputMessageContent(inputMessageContent);
        }
        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }


    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).PHOTO_SERIES")
    public void preparePhotoSeries(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var content = event.getObject();
        var photos = content.getMedia().stream().map(media -> {
            var url = media.getUrl();
            var thumb = media.getThumbnailUrl();
            var res = new InlineQueryResultPhoto();
            setPhotoFields(event, update, url, thumb, res);
            return res;
        }).toList();

        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setResults(Collections.unmodifiableList(photos));
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }


    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).VIDEO_SERIES")
    public void prepareVideoSeries(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var content = event.getObject();
        var videos = content.getMedia().stream().map(media -> {
            var url = media.getUrl();
            var thumb = media.getThumbnailUrl();
            var res = new InlineQueryResultVideo();
            res.setVideoUrl(url);
            res.setThumbUrl(thumb);
            res.setId(UUID.randomUUID().toString());
            res.setTitle(UUID.randomUUID().toString());
            res.setMimeType(VIDEO_MP_4);
            return res;
        }).toList();

        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setResults(Collections.unmodifiableList(videos));
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }


    @EventListener(value = ParsedInlineContentEvent.class,
            condition = "#event.object.type == T(io.ssstoyanov.mooch.media.entity.ContentType).TEXT")
    public void prepareText(ParsedInlineContentEvent event) {
        AnswerInlineQuery answer = new AnswerInlineQuery();

        var update = event.getMessage();
        var content = event.getObject();

        var res = new InlineQueryResultArticle();

        InputTextMessageContent inputTextMessageContent = new InputTextMessageContent();
        setCaption(update, inputTextMessageContent, content);

        res.setTitle("Twitter");
        res.setDescription(content.getText());
        res.setInputMessageContent(inputTextMessageContent);
        res.setId(UUID.randomUUID().toString());

        answer.setInlineQueryId(update.getInlineQuery().getId());
        answer.setResults(Collections.singletonList(res));
        answer.setCacheTime(15);

        publisher.publishEvent(new AnswerInlineQueryEvent(answer));
    }


}
