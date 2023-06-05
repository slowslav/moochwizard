package io.ssstoyanov.mooch.telegram.service;

import io.ssstoyanov.mooch.event.AppEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ErrorMessageContentEvent extends AppEvent<Update> {
    public ErrorMessageContentEvent(Update source) {
        super(source);
    }
}
