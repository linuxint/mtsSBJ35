package com.devkbil.common.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 시스템에서 파일 크기와 개수를 다루기 위한 서비스를 제공합니다.
 * 이 클래스는 파일 및 디렉터리의 크기와 개수를 직접 또는 재귀적으로 계산하는 메서드를 포함합니다.
 */
@Slf4j
public class FileSize {

    /**
     * 해당 경로(src)의 크기(바이트)를 반환
     * - 파일인 경우: 파일 크기 반환
     * - 디렉토리인 경우: 직속 파일들의 합 반환
     *
     * @param path 파일 또는 디렉토리 경로
     * @return 크기(바이트)
     * @throws IOException 파일 접근 오류 발생 시
     */
    public long size(String path) throws IOException {
        Assert.hasText(path, "Path must not be empty");
        log.debug("Calculating size for path: {}", path);

        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            log.warn("Path does not exist: {}", path);
            return 0;
        }

        File file = resource.getFile();
        if (file.isFile()) {
            return file.length();
        } else {
            return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(File::isFile)
                .mapToLong(File::length)
                .sum();
        }
    }

    /**
     * 해당 경로(src)의 크기(바이트)를 반환
     * - 파일인 경우: 파일 크기 반환
     * - 디렉토리인 경우: 하위 모든 파일들(재귀 탐색)의 크기 합 반환
     *
     * @param path 파일 또는 디렉토리 경로
     * @return 크기(바이트)
     * @throws IOException 파일 접근 오류 발생 시
     */
    public long sizeTree(String path) throws IOException {
        Assert.hasText(path, "Path must not be empty");
        log.debug("Calculating tree size for path: {}", path);

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            log.warn("Path does not exist: {}", path);
            return 0;
        }

        if (Files.isRegularFile(filePath)) {
            return Files.size(filePath);
        } else {
            try (Stream<Path> pathStream = Files.walk(filePath)) {
                return pathStream
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            log.error("Error getting size of file: {}", p, e);
                            return 0;
                        }
                    })
                    .sum();
            }
        }
    }

    /**
     * 지정된 경로(src)의 파일 수를 반환
     * - 파일인 경우: 1 반환
     * - 디렉토리인 경우: 직속 파일의 개수 반환
     *
     * @param path 파일 또는 디렉토리 경로
     * @return 파일 개수
     * @throws IOException 파일 접근 오류 발생 시
     */
    public int countFile(String path) throws IOException {
        Assert.hasText(path, "Path must not be empty");
        log.debug("Counting files for path: {}", path);

        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            log.warn("Path does not exist: {}", path);
            return 0;
        }

        File file = resource.getFile();
        if (file.isFile()) {
            return 1;
        } else {
            return (int)Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(File::isFile)
                .count();
        }
    }

    /**
     * 지정된 경로(src)의 파일 수를 반환
     * - 파일인 경우: 1 반환
     * - 디렉토리인 경우: 하위 모든 파일들(재귀 탐색)의 개수 반환
     *
     * @param path 파일 또는 디렉토리 경로
     * @return 파일 개수
     * @throws IOException 파일 접근 오류 발생 시
     */
    public int countFileTree(String path) throws IOException {
        Assert.hasText(path, "Path must not be empty");
        log.debug("Counting files in tree for path: {}", path);

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            log.warn("Path does not exist: {}", path);
            return 0;
        }

        if (Files.isRegularFile(filePath)) {
            return 1;
        } else {
            try (Stream<Path> pathStream = Files.walk(filePath)) {
                return (int)pathStream
                    .filter(Files::isRegularFile)
                    .count();
            }
        }
    }
}
