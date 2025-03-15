package com.devkbil.common.util;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 및 디렉토리 작업을 수행하는 유틸리티 클래스입니다.
 * 파일 및 디렉토리 생성, 삭제, 이동 기능을 포함합니다.
 */
@Slf4j
public class FileDirectory {

    /**
     * 주어진 경로에 디렉토리 구조를 생성합니다. 디렉토리가 이미 존재하는 경우 아무 작업도 하지 않습니다.
     *
     * @param path 생성할 디렉토리의 경로. 이 경로가 존재하지 않는 경우 필요한 디렉토리가 생성됩니다.
     */
    public static void makeBasePath(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 하위의 모든 파일과 디렉토리를 삭제합니다.
     *
     * @param file 삭제할 파일 또는 디렉토리
     */
    public static void removeDir(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] list = file.listFiles();
                for (File f : list) {
                    removeDir(f);
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    /**
     * 하위의 모든 파일과 디렉토리를 삭제합니다.
     *
     * @param fileStr 삭제할 디렉토리의 경로 문자열
     */
    public static void removeDir(String fileStr) {
        removeDir(new File(fileStr));
    }

    /**
     * 지정된 경로에 디렉토리를 생성합니다. 디렉토리가 이미 존재하는 경우 아무 작업도 하지 않습니다.
     *
     * @param path 생성할 디렉토리의 경로
     * @return 디렉토리가 성공적으로 생성되었거나 이미 존재하면 true, 그렇지 않으면 false
     */
    public static boolean makeDirectory(String path) {
        boolean success = true;
        File directory = new File(path);

        if (!directory.exists()) {
            success = directory.mkdirs();
        }

        return success;
    }

    /**
     * 파일을 지정된 대상 디렉토리로 이동합니다.
     * 소스 파일이 존재하지 않거나 대상 디렉토리가 존재하지 않는 경우,
     * 메서드는 아무 작업도 하지 않거나 예외를 던집니다.
     *
     * @param src       이동할 소스 파일의 경로
     * @param targetDir 파일이 이동할 대상 디렉토리의 경로
     * @throws Exception 대상 디렉토리가 없거나 파일 이동에 문제가 발생한 경우
     */
    public static void move(String src, String targetDir) throws Exception {
        if (src == null || src.equals("") || targetDir == null || targetDir.equals("")) {
            return;
        }

        File srcFile = new File(src);
        if (!srcFile.exists()) {
            return;
        }

        File targetPDir = new File(targetDir);
        if (!targetPDir.exists()) {
            throw new IOException("Destination dir not exists");
        }

        File targetFile = new File(targetDir + File.separator + srcFile.getName());
        srcFile.renameTo(targetFile);
    }

    /**
     * 전체 트리(디렉토리 및 하위 내용)를 지정된 소스 경로에서 대상 디렉토리로 이동합니다.
     *
     * @param src       이동할 디렉토리의 경로
     * @param targetDir 디렉토리가 이동할 대상 디렉토리의 경로
     * @throws Exception 경로가 유효하지 않거나 권한이 부족한 경우와 같은 이유로 작업에 실패한 경우
     */
    public static void moveTree(String src, String targetDir) throws Exception {
        move(src, targetDir);
    }

    /**
     * 파일을 소스 디렉토리에서 대상 디렉토리로 이동합니다.
     * 대상 디렉토리가 존재하지 않으면 생성합니다.
     * 소스 또는 대상 경로가 null이거나 비어있으면 아무 작업도 하지 않습니다.
     * 소스 파일이 존재하지 않는 경우 메서드는 아무 작업도 하지 않습니다.
     *
     * @param src    이동할 소스 파일의 경로
     * @param target 파일이 이동할 대상 디렉토리의 경로
     * @throws Exception 파일 이동 작업 중 오류가 발생한 경우
     */
    public static void makeAndMove(String src, String target) throws Exception {
        if (src == null || src.equals("") || target == null || target.equals("")) {
            return;
        }

        File srcFile = new File(src);
        if (!srcFile.exists()) {
            return;
        }

        File targetPDir = new File(target);
        if (!targetPDir.exists()) {
            targetPDir.mkdirs();
        }

        File targetFile = new File(target + File.separator + srcFile.getName());
        srcFile.renameTo(targetFile);
    }

    /**
     * 지정된 소스 위치에 디렉토리 트리 구조를 생성하고 대상 위치로 이동합니다.
     *
     * @param src    디렉토리 트리 구조가 생성될 소스 경로
     * @param target 디렉토리 트리 구조가 이동할 대상 경로
     * @throws Exception 생성 또는 이동 작업 중 오류가 발생한 경우
     */
    public static void makeAndMoveTree(String src, String target) throws Exception {
        makeAndMove(src, target);
    }
}