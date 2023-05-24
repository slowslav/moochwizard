package io.ssstoyanov.mooch.event;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SendMessageEvent extends AppEvent<SendMessage> {
    public SendMessageEvent(SendMessage message) {
        super(message);
    }
}
