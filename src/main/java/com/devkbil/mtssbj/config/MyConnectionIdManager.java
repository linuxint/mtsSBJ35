package com.devkbil.mtssbj.config;

import net.ttddyy.dsproxy.ConnectionIdManager;

import java.sql.Connection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MyConnectionIdManager implements ConnectionIdManager {
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String getId(Connection connection) {
        // 고유 ID 생성
        String connectionId = "Conn-" + counter.incrementAndGet();
        System.out.println("Generated Connection ID: " + connectionId);
        return connectionId;
    }

    @Override
    public void addClosedId(String connectionId) {

    }

    @Override
    public Set<String> getOpenConnectionIds() {
        return Set.of();
    }
}