package com.devkbil.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 작업 유틸리티 클래스
 * 존재 확인, 이름 변경, 이동, 복사, 디렉토리 처리 등 다양한 파일 작업을 수행하기 위한 메서드를 제공합니다.
 * 버퍼, 채널, 스트림 기반 기술을 사용한 파일 및 디렉토리 조작 메서드를 포함합니다.
 */
@Slf4j
public class FileOperation {

    /**
     * 파일 존재 여부 체크
     *
     * @param srcFilePath 파일 경로
     * @return 존재 여부
     */
    public static boolean fileExist(String srcFilePath) {
        File file = new File(srcFilePath);
        return file.exists();
    }

    /**
     * 파일 이름 변경
     *
     * @param srcFilePath  원본 파일 경로
     * @param destFilePath 대상 파일 경로
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    public static int fileRename(String srcFilePath, String destFilePath) {
        File oldFile = new File(srcFilePath);
        File newFile = new File(destFilePath);
        return oldFile.renameTo(newFile) ? 1 : 0;
    }

    /**
     * 파일 이동
     *
     * @param source 원본 파일 경로
     * @param target 대상 경로
     * @return 성공 여부
     */
    public static boolean fileMove(String source, String target) {
        File srcFile = new File(source);
        File destFile = new File(target);
        return srcFile.renameTo(destFile);
    }

    /**
     * 파일 복사 (기본 복사 방식)
     *
     * @param source 원본 파일 경로
     * @param target 대상 파일 경로
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    public static int copyFile(String source, String target) {
        return channelFileCopy(source, target);
    }

    /**
     * 파일 복사 (버퍼 방식)
     *
     * @param source 원본 파일 경로
     * @param target 대상 파일 경로
     * @return 성공 여부
     */
    public static int bufferFileCopy(String source, String target) {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return 1; // 성공
        } catch (IOException e) {
            log.error("Buffer File Copy Error: ", e);
            return 0; // 실패
        }
    }

    /**
     * 파일 복사 (채널 방식)
     *
     * @param source 원본 파일 경로
     * @param target 대상 파일 경로
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    public static int channelFileCopy(String source, String target) {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(target);
             FileChannel sourceChannel = fis.getChannel();
             FileChannel targetChannel = fos.getChannel()) {

            sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
            return 1; // 성공
        } catch (IOException e) {
            log.error("Channel File Copy Error: ", e);
            return 0; // 실패
        }
    }

    /**
     * 파일 복사 (스트림 방식)
     *
     * @param source 원본 파일 경로
     * @param target 대상 파일 경로
     * @return 성공 여부
     */
    public static int streamFileCopy(String source, String target) {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(target)) {
            int readByte;
            while ((readByte = is.read()) != -1) {
                os.write(readByte);
            }
            return 1; // 성공
        } catch (IOException e) {
            log.error("Stream File Copy Error: ", e);
            return 0; // 실패
        }
    }

    /**
     * 파일 이름 변경
     *
     * @param file    대상 파일
     * @param newName 변경할 새 이름
     * @return 변경된 파일 객체
     * @throws Exception 예외
     */
    public static File rename(File file, String newName) throws Exception {
        String newPath = file.getParent() + File.separator + newName;
        File renamedFile = new File(newPath);
        if (file.renameTo(renamedFile)) {
            return renamedFile;
        } else {
            throw new IOException("File Renaming Failed");
        }
    }

    /**
     * 파일 복사 (파일 객체)
     *
     * @param src       원본 파일 경로
     * @param targetDir 대상 디렉토리
     * @return 복사된 파일 객체
     * @throws Exception 예외
     */
    public static File copy(String src, String targetDir) throws Exception {
        File srcFile = new File(src);
        File targetFile = new File(targetDir, srcFile.getName());
        if (channelFileCopy(src, targetFile.getPath()) == 1) {
            return targetFile;
        } else {
            throw new IOException("File Copy Failed");
        }
    }

    /**
     * 디렉토리 복사
     *
     * @param dir       원본 디렉토리
     * @param targetDir 대상 디렉토리
     * @return 복사된 디렉토리 객체
     * @throws Exception 예외
     */
    public static File copyTree(String dir, String targetDir) throws Exception {
        File directory = new File(dir);
        File targetDirectory = new File(targetDir, directory.getName());

        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    copyTree(file.getPath(), targetDirectory.getPath());
                } else {
                    copy(file.getPath(), targetDirectory.getPath());
                }
            }
        }
        return targetDirectory;
    }

    /**
     * 파일 복사 (파일 객체 _ 대상 디렉토리)
     *
     * @param src       원본 파일 객체
     * @param targetDir 대상 디렉토리
     * @return 복사된 파일 객체
     * @throws Exception 예외
     */
    public static File copyFile(File src, String targetDir) throws Exception {
        return copy(src.getPath(), targetDir);
    }
}