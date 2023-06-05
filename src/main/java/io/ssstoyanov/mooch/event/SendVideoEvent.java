package io.ssstoyanov.mooch.event;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class SendVideoEvent extends AppEvent<SendVideo> {
    private final Update message;

    public SendVideoEvent(SendVideo video, Update message) {
        super(video);
        this.message = message;
    }
}
