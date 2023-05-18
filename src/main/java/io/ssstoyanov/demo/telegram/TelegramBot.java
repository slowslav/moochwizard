package io.ssstoyanov.demo.telegram;

import io.ssstoyanov.demo.event.*;
import io.ssstoyanov.demo.telegram.command.HelpCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Service
@RequiredArgsConstructor
public final class TelegramBot extends TelegramLongPollingCommandBot {

    private final ApplicationEventPublisher publisher;

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onRegister() {
        register(new HelpCommand("help", "Помощь"));
        super.onRegister();
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasInlineQuery()) {
            if (update.getInlineQuery().getQuery().contains(": ")) {
                return;
            }
            publisher.publishEvent(new IncomingInlineEvent(update));
            return;
        }
        Message message = update.getMessage();
        if (message != null && message.getText() != null && message.getText().contains(": ")) {
            return;
        }
        publisher.publishEvent(new IncomingMessageEvent(update));
    }

    @EventListener(AnswerInlineQueryEvent.class)
    public void send(AnswerInlineQueryEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(SendPhotoEvent.class)
    public void send(SendPhotoEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(SendVideoEvent.class)
    public void send(SendVideoEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(SendAudioEvent.class)
    public void send(SendAudioEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(SendMediaGroupEvent.class)
    public void send(SendMediaGroupEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(SendMessageEvent.class)
    public void send(SendMessageEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @EventListener(DeleteMessageEvent.class)
    public void send(DeleteMessageEvent event) {
        try {
            super.execute(event.getObject());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }


}
