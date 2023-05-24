package io.ssstoyanov.mooch.parsing;

import io.ssstoyanov.mooch.event.IncomingInlineEvent;
import io.ssstoyanov.mooch.event.IncomingMessageEvent;
import io.ssstoyanov.mooch.event.ParsedContentEvent;
import io.ssstoyanov.mooch.event.ParsedInlineContentEvent;
import io.ssstoyanov.mooch.media.MediaService;
import io.ssstoyanov.mooch.media.ServiceType;
import io.ssstoyanov.mooch.media.entity.Content;
import io.ssstoyanov.mooch.media.entity.ContentType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParsingService {

    private static final Pattern pattern = Pattern.compile("(#.*|\\?.*)");
    private static final List<AbstractMap.SimpleEntry<List<String>, ServiceType>> MAPPING =
            List.of(
                    new AbstractMap.SimpleEntry<>(List.of("pin.it", "pinterest.com/pin"), ServiceType.PINTEREST),
                    new AbstractMap.SimpleEntry<>(List.of("twitter.com"), ServiceType.TWITTER),
                    new AbstractMap.SimpleEntry<>(List.of("tiktok.com"), ServiceType.TIKTOK),
                    new AbstractMap.SimpleEntry<>(List.of("instagram.com"), ServiceType.INSTAGRAM),
                    new AbstractMap.SimpleEntry<>(List.of("meduza.io", "newsmaker.md"), ServiceType.NEWS),
                    new AbstractMap.SimpleEntry<>(List.of("spotify.com", "music.yandex.ru", "music.apple.com"), ServiceType.MUSIC),
                    new AbstractMap.SimpleEntry<>(List.of("youtube.com/shorts"), ServiceType.YOUTUBE)
            );
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private Map<ServiceType, MediaService> services;

    @EventListener(IncomingMessageEvent.class)
    public void parseMessage(IncomingMessageEvent event) {
        var message = event.getObject();
        var urls = getUrls(message);
        if (urls.isEmpty()) {
            return;
        }
        parseAndPublish(message, urls);
    }

    @EventListener(IncomingInlineEvent.class)
    public void parseMessage(IncomingInlineEvent event) {
        var message = event.getObject();
        var url = message.getInlineQuery().getQuery();
        parseAndPublishInline(message, Collections.singletonList(url));
    }

    private void parseAndPublish(Update message, List<String> urls) {
        for (String url : urls) {
            for (AbstractMap.SimpleEntry<List<String>, ServiceType> service : MAPPING) {
                if (service.getKey().stream().anyMatch(url::contains)) {
                    Content content;
                    try {
                        content = services.get(service.getValue()).getContent(url);
                        publisher.publishEvent(new ParsedContentEvent(content, message));
                    } catch (Exception ignored) {
                        content = new Content().setText(String.format("Произошла ошибка при парсинге %s", url))
                                .setType(ContentType.ERROR);
                        publisher.publishEvent(new ParsedContentEvent(content, message));
                    }
                }
            }
        }
    }

    private void parseAndPublishInline(Update message, List<String> urls) {
        for (String url : urls) {
            for (AbstractMap.SimpleEntry<List<String>, ServiceType> service : MAPPING) {
                if (service.getKey().stream().anyMatch(url::contains)) {
                    Content content;
                    try {
                        content = services.get(service.getValue()).getContent(url);
                        publisher.publishEvent(new ParsedInlineContentEvent(content, message));
                    } catch (Exception ignored) {
                        content = new Content().setText(String.format("Произошла ошибка при парсинге %s", url))
                                .setType(ContentType.ERROR);
                        publisher.publishEvent(new ParsedInlineContentEvent(content, message));
                    }
                }
            }
        }
    }

    @NotNull
    private List<String> getUrls(Update update) {
        var message = update.getMessage();
        if (message == null) {
            return Collections.emptyList();
        }
        var entities = message.getEntities();
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream()
                .filter(Objects::nonNull)
                .map(MessageEntity::getText)
                .filter(Objects::nonNull)
                .map(this::clean)
                .toList();
    }

    private String clean(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return url.replace(matcher.group(0), "");
        }
        return url;
    }
}
