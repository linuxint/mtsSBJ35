package com.devkbil.mtssbj;

import java.util.EnumSet;

import lombok.Getter;

@Getter
public enum EnumPort {
    CARD("04", 18911), DEP("02", 18912);

    EnumPort(String bwcd, int port) {
        this.bwcd = bwcd;
        this.port = port;
    }

    private final String bwcd;
    private final int port;

    static int getBwcdOfPort(String bwcd) {
        if (bwcd == null) {
            return 0;
        }
        final int[] port = {0};
        EnumSet.allOf(EnumPort.class)
            .forEach(
                enumPort -> {
                    if (bwcd.equals(enumPort.getBwcd())) {
                        port[0] = enumPort.getPort();
                    }
                });
        return port[0];
    }
}