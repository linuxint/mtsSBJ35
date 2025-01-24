package com.devkbil.mtssbj.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
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

        List<FileVO> filelist = new ArrayList<FileVO>();

        Path path = Paths.get(dirPath);
        DirectoryStream<Path> dir = null;
        BasicFileAttributes attrs;
        FileVO filedo;
        FileChannel fileChannel;
        try {
            dir = Files.newDirectoryStream(path);
            for (Path file : dir) {
                attrs = Files.readAttributes(file, BasicFileAttributes.class);
                if (attrs.isDirectory()) {
                    log.info("dir : {}", file);
                    filelist = showFIlesInDir3(file.toString());
                    //} else if(file.getFileName().endsWith(".txt")) {
                    //    log.info("1");
                } else {
                    filedo = new FileVO();
                    filedo.setFilename(file.getFileName().toString());
                    filedo.setRealname(file.getFileName().toString());

                    fileChannel = FileChannel.open(Paths.get(path + "/" + filedo.getRealname()));
                    filedo.setFilesize(fileChannel.size() / 1024); // Kbyte

                    filedo.setUri(file.toUri().toString());
                    filedo.setFilepath(file.getParent().toString());
                    filelist.add(filedo);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (dir != null) {
                    dir.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
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
        log.error(exc.getMessage());
        return CONTINUE;
    }

}
