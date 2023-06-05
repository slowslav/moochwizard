package io.ssstoyanov.mooch.event;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class SendMediaGroupEvent extends AppEvent<SendMediaGroup> {
    private final Update message;

    public SendMediaGroupEvent(SendMediaGroup mediaGroup, Update message) {
        super(mediaGroup);
        this.message = message;
    }
}
