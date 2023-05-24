package io.ssstoyanov.mooch.event;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;

public class SendAudioEvent extends AppEvent<SendAudio> {
    public SendAudioEvent(SendAudio audio) {
        super(audio);
    }
}
