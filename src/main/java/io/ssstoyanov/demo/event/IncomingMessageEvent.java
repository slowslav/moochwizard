package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.objects.Update;

public class IncomingMessageEvent extends AppEvent<Update> {
    public IncomingMessageEvent(Update source) {
        super(source);
    }
}
