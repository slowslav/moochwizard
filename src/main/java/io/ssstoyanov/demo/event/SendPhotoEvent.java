package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class SendPhotoEvent extends AppEvent<SendPhoto> {
    public SendPhotoEvent(SendPhoto photo) {
        super(photo);
    }
}
