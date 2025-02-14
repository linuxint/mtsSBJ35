package com.devkbil.mtssbj.common.util;

import lombok.extern.slf4j.Slf4j;

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

import static java.nio.file.FileVisitResult.CONTINUE;

@Slf4j
public class FileSearchUtil extends SimpleFileVisitor<Path> {

    /**
     * 파일 리스트 출력 (Recursive, listFiles)
     * ex) System.out.println("2");
     * showFilesInDIr("/home/mjs/test/test");
     *
     * @param dirPath 디렉토리패스
     */
    public static void showFilesInDIr(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                showFilesInDIr(file.getPath());
            } else {
                log.info("file: {}", file);
            }
        }
    }

    /**
     * 파일 리스트 출력(Recursive, Filtering)
     *
     * @param dirPath
     * @param fileExt
     */
    public static void showFilesInDIr(String dirPath, String fileExt) {
        if (!StringUtils.hasText(dirPath)) {
            return;
        }
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files.length == 0) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                showFilesInDIr(file.getPath(), fileExt);
            } else if (file.getName().endsWith(fileExt)) {
                log.info("file: {}", file);
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

    // 방문한 파일 정보
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isSymbolicLink()) {
            log.info("Symbolic link: {} ", file);
        } else if (attr.isRegularFile()) {
            log.info("Regular link: {} ", file);
        } else {
            log.info("Other link: {} ", file);
        }
        log.info("( {} bytes)", attr.size());
        return CONTINUE;
    }

    // 방문한 디렉토리 정보
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        log.info("Directory link: {} ", dir);
        return CONTINUE;
    }

    // 파일을 접근하는 중에 에러가 존재하면, 사용자에게 에러와 예외로 알려줌
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        log.error("Failed to access file: {} (Reason: {})", file.toAbsolutePath(), exc.getMessage());
        log.error(exc.getMessage());
        return CONTINUE;
    }

}
