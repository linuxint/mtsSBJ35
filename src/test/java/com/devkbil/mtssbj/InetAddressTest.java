package com.devkbil.mtssbj;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {

    public static void main(String[] args) {
        try {
            String sHostName = InetAddress.getLocalHost().getHostName();
            System.out.println(sHostName);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

}
