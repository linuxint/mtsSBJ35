package com.devkbil.mtssbj.network;

import com.devkbil.mtssbj.mail.SendMail;

public class MailClientTest {

    public static void main(String[] args) throws Exception {

        SendMail sm = new SendMail("172.30.1.57", "465", "user01@james.local", "user01@james.local", "1234");
        sm.send(true, new String[] {""}, new String[] {}, new String[] {}, "test", "body1111");
    }

}
