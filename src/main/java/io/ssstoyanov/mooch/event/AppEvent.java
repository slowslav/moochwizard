package io.ssstoyanov.mooch.event;

import org.springframework.context.ApplicationEvent;

public class AppEvent<T> extends ApplicationEvent {
    public AppEvent(T source) {
        super(source);
    }

    public T getObject() {
        return (T) getSource();
    }
}
