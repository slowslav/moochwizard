package io.ssstoyanov.mooch.event;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class SendPhotoEvent extends AppEvent<SendPhoto> {
    public SendPhotoEvent(SendPhoto photo) {
        super(photo);
    }
}
