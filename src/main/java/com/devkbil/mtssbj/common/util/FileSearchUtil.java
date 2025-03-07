package com.devkbil.mtssbj.common.util;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for searching and processing files and directories.
 * Extends {@link SimpleFileVisitor} to provide custom file and directory handling.
 */
@Slf4j
public class FileSearchUtil extends SimpleFileVisitor<Path> {

    /**
     * 디렉토리 내 파일 리스트 출력 (재귀적으로 검색)
     *
     * @param dirPath 디렉토리 경로
     */
    public static void showFilesInDIr(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.isDirectory()) {
            log.warn("The specified path is not a directory: {}", dirPath);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            log.warn("No files found in the directory: {}", dirPath);
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                showFilesInDIr(file.getAbsolutePath());
            } else {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    /**
     * 특정 확장자를 가진 파일 리스트 출력
     *
     * @param dirPath 디렉토리 경로
     * @param fileExt 파일 확장자 (e.g., '.txt')
     */
    public static void showFilesInDIr(String dirPath, String fileExt) {
        File directory = new File(dirPath);

        if (!directory.isDirectory()) {
            log.warn("The specified path is not a directory: {}", dirPath);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            log.warn("No files found in the directory: {}", dirPath);
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                showFilesInDIr(file.getAbsolutePath(), fileExt);
            } else if (file.getName().endsWith(fileExt)) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    /**
     * 파일 리스트
     *
     * @param dirPath 파일패스
     * @return 파일 리스트
     */
    public static List<FileVO> showFIlesInDir3(String dirPath) {
        if (!StringUtils.hasText(dirPath)) {
            return new ArrayList<>();
        }

        List<FileVO> filelist = new ArrayList<>();
        Path path = Paths.get(dirPath);

        try (DirectoryStream<Path> dir = Files.newDirectoryStream(path)) {
            for (Path file : dir) {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);

                if (attrs.isDirectory()) {
                    log.info("Directory found: {}", file.toAbsolutePath());
                    filelist.addAll(showFIlesInDir3(file.toString())); // 재귀 호출로 하위 디렉토리 탐색
                } else if (!file.getFileName().toString().startsWith(".") // 숨김 파일 제외
                    && file.getFileName().toString().matches("^[0-9]+$")) { // 숫자 파일만
                    log.info("File found: {}", file.toAbsolutePath());

                    FileVO fileVo = new FileVO();
                    fileVo.setFilename(file.getFileName().toString());
                    fileVo.setRealname(file.getFileName().toString());

                    // 파일 크기 계산
                    try (FileChannel channel = FileChannel.open(file)) {
                        fileVo.setFilesize(channel.size() / 1024); // 파일 크기를 Kbyte 단위로 저장
                    }
                    fileVo.setUri(file.toUri().toString());
                    fileVo.setFilepath(file.getParent().toString());
                    filelist.add(fileVo);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read directory: {}", dirPath, e);
        }
        return filelist;
    }

    /**
     * 방문한 파일 처리
     *
     * @param file 파일 경로
     * @param attr 파일 속성
     * @return 결과
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        System.out.println("Visited file: " + file);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 방문한 디렉토리 처리
     *
     * @param dir 디렉토리 경로
     * @param exc 예외
     * @return 결과
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Visited directory: " + dir);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 파일 접근 실패 처리
     *
     * @param file 파일 경로
     * @param exc  예외
     * @return 결과
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println("Failed to visit file: " + file + " (" + exc + ")");
        return FileVisitResult.CONTINUE;
    }

}