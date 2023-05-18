package io.ssstoyanov.demo.event;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;

public class AnswerInlineQueryEvent extends AppEvent<AnswerInlineQuery> {
    public AnswerInlineQueryEvent(AnswerInlineQuery source) {
        super(source);
    }
}
