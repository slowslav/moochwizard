package io.ssstoyanov.mooch.event;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class SendAudioEvent extends AppEvent<SendAudio> {
    private final Update message;

    public SendAudioEvent(SendAudio audio, Update message) {
        super(audio);
        this.message = message;
    }
}
