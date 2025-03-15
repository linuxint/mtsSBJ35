package com.devkbil.common.util;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 시스템에서 파일 크기와 개수를 다루기 위한 유틸리티 메서드를 제공합니다.
 * 이 클래스는 파일 및 디렉터리의 크기와 개수를 직접 또는 재귀적으로 계산하는 메서드를 포함합니다.
 */
@Slf4j
public class FileSize {

    /**
     * 해당 경로(src)의 크기(바이트)를 반환
     * - 파일인 경우: 파일 크기 반환
     * - 디렉토리인 경우: 직속 파일들의 합 반환
     *
     * @param src 파일 또는 디렉토리 경로
     * @return 크기(바이트)
     * @throws Exception 예외 발생 시
     */
    public static long size(String src) throws Exception {
        long result = 0;

        File file = new File(src);
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            result = file.length();
        } else {
            File[] subs = file.listFiles();
            for (int i = 0; i < subs.length; i++) {
                if (subs[i].isFile()) {
                    result += subs[i].length();
                }
            }
        }

        return result;
    }

    /**
     * 해당 경로(src)의 크기(바이트)를 반환
     * - 파일인 경우: 파일 크기 반환
     * - 디렉토리인 경우: 하위 모든 파일들(재귀 탐색)의 크기 합 반환
     *
     * @param src 파일 또는 디렉토리 경로
     * @return 크기(바이트)
     * @throws Exception 예외 발생 시
     */
    public static long sizeTree(String src) throws Exception {
        long result = 0;

        File file = new File(src);
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            result = file.length();
        } else if (file.isDirectory()) {
            String[] subs = file.list();
            for (int i = 0; subs != null && i < subs.length; i++) {
                result += sizeTree(src + File.separator + subs[i]);
            }
        }

        return result;
    }

    /**
     * 지정된 경로(src)의 파일 수를 반환
     * - 파일인 경우: 1 반환
     * - 디렉토리인 경우: 직속 파일의 개수 반환
     *
     * @param src 파일 또는 디렉토리 경로
     * @return 파일 개수
     * @throws Exception 예외 발생 시
     */
    public static int countFile(String src) throws Exception {
        int result = 0;

        File file = new File(src);
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            result = 1;
        } else if (file.isDirectory()) {
            File[] subs = file.listFiles();
            for (int i = 0; i < subs.length; i++) {
                if (subs[i].isFile()) {
                    result++;
                }
            }
        }

        return result;
    }

    /**
     * 지정된 경로(src)의 파일 수를 반환
     * - 파일인 경우: 1 반환
     * - 디렉토리인 경우: 하위 모든 파일들(재귀 탐색)의 개수 반환
     *
     * @param src 파일 또는 디렉토리 경로
     * @return 파일 개수
     * @throws Exception 예외 발생 시
     */
    public static int countFileTree(String src) throws Exception {
        int result = 0;

        File file = new File(src);
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            result = 1;
        } else if (file.isDirectory()) {
            String[] subs = file.list();
            for (int i = 0; i < subs.length; i++) {
                result += countFileTree(src + File.separator + subs[i]);
            }
        }

        return result;
    }
}