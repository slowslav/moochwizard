package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.objects.Update;

public class IncomingInlineEvent extends AppEvent<Update> {
    public IncomingInlineEvent(Update update) {
        super(update);
    }
}
