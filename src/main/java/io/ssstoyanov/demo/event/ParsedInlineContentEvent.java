package io.ssstoyanov.demo.event;

import io.ssstoyanov.demo.media.entity.Content;
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
