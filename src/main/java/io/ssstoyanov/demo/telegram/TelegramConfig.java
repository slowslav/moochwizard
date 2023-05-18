package io.ssstoyanov.demo.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    private final TelegramBot telegramBot;

    @Bean
    public TelegramBotsApi getBotApi() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            log.error(e);
            throw new RuntimeException();
        }
    }

}
