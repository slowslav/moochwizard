package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class DeleteMessageEvent extends AppEvent<DeleteMessage> {
    public DeleteMessageEvent(DeleteMessage message) {
        super(message);
    }
}
