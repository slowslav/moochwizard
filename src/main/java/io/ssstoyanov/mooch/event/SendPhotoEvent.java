package io.ssstoyanov.mooch.event;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class SendPhotoEvent extends AppEvent<SendPhoto> {
    private final Update message;

    public SendPhotoEvent(SendPhoto photo, Update message) {
        super(photo);
        this.message = message;
    }
}
