package io.ssstoyanov.mooch.event;

import io.ssstoyanov.mooch.media.entity.Content;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class ParsedInlineContentEvent extends AppEvent<Content> {
    private final Update message;

    public ParsedInlineContentEvent(Content source, Update message) {
        super(source);
        this.message = message;
    }

}
