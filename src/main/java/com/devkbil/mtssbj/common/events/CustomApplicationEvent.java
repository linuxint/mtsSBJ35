package com.devkbil.mtssbj.common.events;

import org.springframework.context.ApplicationEvent;

public class CustomApplicationEvent extends ApplicationEvent {

    private final String message;

    public CustomApplicationEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
