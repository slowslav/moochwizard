package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SendTextEvent extends AppEvent<SendMessage> {
    public SendTextEvent(SendMessage message) {
        super(message);
    }
}
