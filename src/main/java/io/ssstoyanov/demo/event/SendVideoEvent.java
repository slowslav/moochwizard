package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

public class SendVideoEvent extends AppEvent<SendVideo> {
    public SendVideoEvent(SendVideo video) {
        super(video);
    }
}
