package io.ssstoyanov.mooch.event;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class SendMessageEvent extends AppEvent<SendMessage> {
    private final Update message;

    public SendMessageEvent(SendMessage message, Update update) {
        super(message);
        this.message = update;
    }
}
