package com.devkbil.mtssbj.common.util;

import java.net.Socket;

public class SocketUtil {
    /**
     * 특정 IP와 포트에서 서비스가 실행 중인지 확인합니다.
     *
     * @param ip   확인할 IP 주소
     * @param port 확인할 포트 번호
     * @return 서비스가 실행 중이면 true, 그렇지 않으면 false
     */
    public static boolean isServiceRunning(String ip, int port) {
        try (Socket socket = new Socket(ip, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
