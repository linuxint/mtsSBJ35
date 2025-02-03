package com.devkbil.mtssbj.common.Listener;

import com.devkbil.mtssbj.common.events.CustomApplicationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomApplicationEventListener {

    @EventListener
    public void handleCustomEvent(CustomApplicationEvent event) {
        log.info("CustomApplicationEvent received. Message: {}", event.getMessage());
    }
}
