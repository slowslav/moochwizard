package io.ssstoyanov.mooch.event;

import io.ssstoyanov.mooch.media.entity.Content;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class ParsedContentEvent extends AppEvent<Content> {
    private final Update message;

    public ParsedContentEvent(Content source, Update message) {
        super(source);
        this.message = message;
    }
}
