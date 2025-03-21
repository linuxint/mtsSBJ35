package com.devkbil.common.util;

import com.devkbil.mtssbj.common.FileVO;

import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 업로드 및 관련 작업을 처리하기 위한 유틸리티 클래스입니다.
 * 단일 파일 저장, 다중 파일 업로드, 이미지 파일 업로드 및 리사이즈 기능 포함.
 */
@Slf4j
public class FileUpload {


    // 기본 파일 저장 경로 설정
    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "fileupload" + File.separator;
    // 파일 업로드용 스레드 풀 생성
    private final ExecutorService executorService;

    public FileUpload() {
        this.executorService = Executors.newFixedThreadPool(10); // 스레드 풀 초기화
    }

    /**
     * 단일 파일을 주어진 경로와 파일명으로 저장합니다.
     * 디렉토리가 없는 경우 자동으로 생성합니다.
     *
     * @param file     저장할 MultipartFile 객체
     * @param basePath 파일이 저장될 기본 경로
     * @param fileName 저장될 파일 이름
     * @return 저장된 파일의 전체 경로
     * @throws IOException 파일 저장 중 오류 발생 시 예외 처리
     */
    public static String saveFileOne(MultipartFile file, String basePath, String fileName) throws IOException {

        FileDirectory.makeBasePath(basePath); // 경로 생성(없으면 생성)

        File destFile = new File(basePath + File.separator + fileName); // 실제 파일 저장
        file.transferTo(destFile);

        return destFile.getAbsolutePath(); // 저장된 파일의 절대 경로 반환
    }

    /**
     * 주어진 이미지를 지정된 너비와 높이로 리사이즈합니다.
     *
     * @param srcImage 원본 이미지
     * @param targetWidth 목표 너비
     * @param targetHeight 목표 높이
     * @return 리사이즈된 이미지를 BufferedImage로 반환
     * @throws IOException 이미지 리사이즈 중 오류 발생 시 예외 처리
     */
    private static BufferedImage resizeImage(BufferedImage srcImage, int targetWidth, int targetHeight) throws IOException {
        return Scalr.resize(srcImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight); // 고품질 리사이즈를 수행
    }

    /**
     * 단일 파일을 업로드하고 파일 메타데이터를 FileVO 객체로 반환합니다.
     *
     * @param uploadfile 업로드할 MultipartFile 객체
     * @return 업로드된 파일의 메타데이터
     */
    public FileVO saveFile(MultipartFile uploadfile) {
        // 파일이 없거나 비어 있는 경우 null 반환
        if (uploadfile == null || uploadfile.isEmpty()) {
            return null;
        }

        FileVO fileVO = new FileVO();
        try {
            String newName = FileUtil.getNewName(); // 새로운 파일 이름 생성
            String basePath = FileUtil.getRealPath(BASE_PATH, newName); // 실제 파일 경로 생성

            String savedPath = saveFileOne(uploadfile, basePath, newName); // 파일 저장

            fileVO.setFilepath(savedPath); // 파일 메타데이터 설정
            fileVO.setFilename(uploadfile.getOriginalFilename());
            fileVO.setRealname(newName);
            fileVO.setFilesize(uploadfile.getSize());

            return fileVO;

        } catch (IOException e) {
            // 오류 로그 출력
            log.error("Failed to save image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 다중 파일을 업로드하고 각 파일의 메타데이터를 리스트로 반환합니다.
     *
     * @param upfiles 업로드할 MultipartFile 리스트
     * @return 업로드된 파일들의 메타데이터 리스트
     */
    public List<FileVO> saveAllFiles(List<MultipartFile> upfiles) {
        List<FileVO> fileVOList = new ArrayList<>();
        // 파일 리스트가 null인 경우 빈 리스트 반환
        if (upfiles == null) {
            return fileVOList;
        }

        for (MultipartFile file : upfiles) {
            // 스레드 풀을 사용하여 비동기적으로 파일 처리
            executorService.submit(() -> {
                FileVO savedFile = saveFile(file);
                // 파일 저장 성공 시 리스트에 추가
                if (savedFile != null) {
                    synchronized (fileVOList) {
                        fileVOList.add(savedFile);
                    }
                }
            });
        }
        // 스레드 풀 종료
        executorService.shutdown();
        return fileVOList;
    }

    /**
     * 이미지 파일을 업로드하고 설정한 크기로 리사이즈하여 저장합니다.
     * 리사이즈된 이미지는 원본과 동일한 경로에 저장됩니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드 및 리사이즈된 이미지 파일의 메타데이터
     */
    public FileVO saveImage(MultipartFile file) {
        // 파일이 없거나 비어 있는 경우 null 반환
        if (file == null || file.isEmpty()) {
            return null;
        }

        FileVO fileVO = new FileVO();
        try {
            String originalFilename = file.getOriginalFilename(); // 원본 파일 이름 가져오기
            String savedPath = saveFileOne(file, BASE_PATH, originalFilename); // 파일 저장

            BufferedImage originalImage = ImageIO.read(new File(savedPath)); // 원본 이미지 읽기
            if (originalImage == null) {
                return null;
            }

            fileVO.setFilepath(savedPath); // 메타데이터 설정
            fileVO.setFilename(originalFilename);
            fileVO.setFilesize(file.getSize());

            // 이미지가 100x100보다 클 경우 리사이즈 진행
            if (originalImage.getWidth() > 100 || originalImage.getHeight() > 100) {
                BufferedImage resizedImage = resizeImage(originalImage, 100, 100);
                String resizedName = originalFilename + "1"; // 리사이즈된 파일 이름과 경로 설정
                String resizedPath = BASE_PATH + File.separator + resizedName;
                File resizedFile = new File(resizedPath);
                ImageIO.write(resizedImage, "jpg", resizedFile); // 리사이즈된 이미지 저장
                fileVO.setRealname(resizedName);
            } else {
                // 리사이즈 필요가 없을 경우 원본 이름 그대로 사용
                fileVO.setRealname(originalFilename);
            }

            return fileVO;
        } catch (IOException e) {
            // 오류 로그 출력
            log.error("Failed to save image: " + e.getMessage(), e);
            return null;
        }
    }
}