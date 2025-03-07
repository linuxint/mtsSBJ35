package com.devkbil.mtssbj.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일 관련 핵심 설정을 관리하는 유틸리티 클래스.
 * 버퍼 크기와 같은 설정을 관리하기 위한 기본값과 메서드를 제공합니다.
 */
@Slf4j
@Setter
@Getter
public class FileCore {
    static final Integer imgWidth = 100;
    static final Integer imgHeight = 100;
    public static int nSuccess = 1;
    public static int nException = 0;
    public static int nFailer = -1;
    static int bufferSize = 1024; /** 파일 복사 BUFFER_SIZE **/
}