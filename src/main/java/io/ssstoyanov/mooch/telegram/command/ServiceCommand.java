package io.ssstoyanov.mooch.telegram.command;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Log4j2
public abstract class ServiceCommand extends BotCommand {

    ServiceCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    /**
     * Отправка ответа пользователю
     */
    void sendAnswer(AbsSender absSender, Long chatId) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText("I can parse content: Pinterest, TikTok, Instagram, YouTube Shorts, Spotify, News and other");
        message.setParseMode(MARKDOWN);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.warn("Невозможно отправить сообщение на /help", e);
        }
    }
}
