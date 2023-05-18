package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;

public class SendMediaGroupEvent extends AppEvent<SendMediaGroup> {
    public SendMediaGroupEvent(SendMediaGroup mediaGroup) {
        super(mediaGroup);
    }
}
