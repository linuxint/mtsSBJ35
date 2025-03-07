package com.devkbil.mtssbj.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

/**
 * 호스트 관련 작업을 처리하기 위한 유틸리티 클래스입니다.
 * 호스트명을 조회하고 검증하는 다양한 메서드를 제공합니다.
 */
@Slf4j
public class HostUtil {

    /**
     * 기본 생성자입니다.
     * 유틸리티 클래스의 인스턴스화를 방지하기 위해 private으로 선언하는 것이 권장됩니다.
     */
    public HostUtil() {
        // 유틸리티 클래스 생성자
    }

    /**
     * Runtime을 사용하여 시스템의 호스트명을 조회합니다.
     * 'hostname' 명령어를 실행하여 시스템에서 직접 호스트명을 가져옵니다.
     *
     * @return 시스템의 호스트명. 오류 발생 시 빈 문자열 반환
     */
    public static String getHostNameRt() {
        String hostName = "";
        String[] cmdArray = {"hostname"};

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmdArray);
            int inp;
            while ((inp = proc.getInputStream().read()) != -1) {
                hostName += (char) inp;
            }
            proc.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return hostName.trim();
    }

    /**
     * InetAddress를 사용하여 시스템의 호스트명을 조회합니다.
     * Java의 네트워킹 API를 통해 로컬 호스트의 정보를 가져옵니다.
     *
     * @return 시스템의 호스트명
     * @throws UnknownHostException 호스트 정보를 가져올 수 없는 경우 발생
     */
    public static String getHostNameInet() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        return hostName;
    }

    /**
     * 주어진 호스트명과 현재 시스템의 호스트명을 비교합니다.
     * InetAddress를 사용하여 현재 시스템의 호스트명을 조회하고,
     * 입력받은 호스트명과 일치하는지 확인합니다.
     *
     * @param indexingHost 비교할 호스트명
     * @return 호스트명이 일치하면 true, 그렇지 않으면 false. 입력된 호스트명이 null이거나 빈 문자열인 경우 false 반환
     * @throws UnknownHostException 현재 호스트 정보를 가져올 수 없는 경우 발생
     */
    public static boolean hostCheck(String indexingHost) throws UnknownHostException {
        // 입력값이 null이거나 빈 문자열인 경우 false 반환
        if (indexingHost == null || indexingHost.trim().isEmpty()) {
            return false;
        }

        // 현재 호스트명과 비교
        String hostName = getHostNameInet();
        return indexingHost.equals(hostName);
    }
}
