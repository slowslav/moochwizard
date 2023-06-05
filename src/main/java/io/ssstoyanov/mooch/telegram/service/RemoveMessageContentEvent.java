package io.ssstoyanov.mooch.telegram.service;

import io.ssstoyanov.mooch.event.AppEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RemoveMessageContentEvent extends AppEvent<Update> {
    public RemoveMessageContentEvent(Update message) {
        super(message);
    }
}
