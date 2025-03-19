package com.devkbil.common.util;

import com.devkbil.mtssbj.common.FileVO;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 업로드 및 관련 작업을 처리하기 위한 유틸리티 메서드를 제공합니다.
 * 단일 파일 저장, 다중 파일 업로드, 이미지 파일 업로드 및 리사이즈 기능을 포함합니다.
 */
@Slf4j
public class FileUpload {

    /**
     * 주어진 경로와 파일명을 사용하여 단일 파일을 저장합니다.
     * 디렉토리가 존재하지 않는 경우 생성합니다.
     *
     * @param file     저장할 MultipartFile
     * @param basePath 파일이 저장될 기본 경로
     * @param fileName 저장될 파일의 이름
     * @return 저장된 파일의 전체 경로
     * @throws IOException 파일 저장 중 I/O 오류가 발생한 경우
     */
    public static String saveFileOne(MultipartFile file, String basePath, String fileName) throws IOException {

        FileDirectory.makeBasePath(basePath);

        File destFile = new File(basePath + File.separator + fileName);
        file.transferTo(destFile);

        return destFile.getAbsolutePath();
    }

    /**
     * 주어진 이미지를 설정된 크기로 리사이즈합니다.
     *
     * @param srcImage 리사이즈할 원본 BufferedImage
     * @param type     리사이즈된 이미지의 타입 (예: BufferedImage.TYPE_INT_RGB)
     * @return 리사이즈된 BufferedImage
     */
    private static BufferedImage resizeImage(BufferedImage srcImage, int type) {
        int targetWidth = 100;
        int targetHeight = 100; // 크기 예시
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(srcImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resizedImage;
    }

    /**
     * 단일 파일을 업로드하고 해당 파일의 메타데이터를 반환합니다.
     *
     * @param uploadfile 업로드할 MultipartFile
     * @return 업로드된 파일의 메타데이터 객체
     */
    public FileVO saveFile(MultipartFile uploadfile) {
        if (uploadfile == null || uploadfile.isEmpty()) {
            return null;
        }

        FileVO fileVO = new FileVO();
        try {
            String basePath = System.getProperty("user.dir") + File.separator + "fileupload" + File.separator;
            String newName = FileUtil.getNewName();
            basePath = FileUtil.getRealPath(basePath, newName);

            String savedPath = saveFileOne(uploadfile, basePath, newName);

            fileVO.setFilepath(savedPath);
            fileVO.setFilename(uploadfile.getOriginalFilename());
            fileVO.setRealname(newName);
            fileVO.setFilesize(uploadfile.getSize());

            return fileVO;

        } catch (Exception e) {
            log.error("Failed to save file: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 다중 파일을 업로드하고 각 파일의 메타데이터를 리스트 형태로 반환합니다.
     *
     * @param upfiles 업로드할 MultipartFile의 리스트
     * @return 업로드된 파일들의 메타데이터 리스트
     */
    public List<FileVO> saveAllFiles(List<MultipartFile> upfiles) {
        List<FileVO> fileVOList = new ArrayList<>();
        if (upfiles == null) {
            return fileVOList;
        }

        for (MultipartFile file : upfiles) {
            FileVO savedFile = saveFile(file);
            if (savedFile != null) {
                fileVOList.add(savedFile);
            }
        }
        return fileVOList;
    }

    /**
     * 이미지 파일을 업로드하고 설정된 크기로 리사이즈한 이미지를 저장합니다.
     * 리사이즈된 이미지는 원본과 동일한 경로에 저장됩니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드 및 리사이즈된 이미지 파일의 메타데이터 객체
     */
    public FileVO saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        FileVO fileVO = new FileVO();
        try {
            String basePath = System.getProperty("user.dir") + File.separator + "fileupload";
            String originalFilename = file.getOriginalFilename();
            String savedPath = saveFileOne(file, basePath, originalFilename);

            BufferedImage originalImage = ImageIO.read(new File(savedPath));
            if (originalImage == null) {
                return null;
            }

            fileVO.setFilepath(savedPath);
            fileVO.setFilename(originalFilename);
            fileVO.setFilesize(file.getSize());

            // Only resize if the image is larger than 100x100
            if (originalImage.getWidth() > 100 || originalImage.getHeight() > 100) {
                BufferedImage resizedImage = resizeImage(originalImage, BufferedImage.TYPE_INT_RGB);
                String resizedName = originalFilename + "1";
                String resizedPath = basePath + File.separator + resizedName;
                File resizedFile = new File(resizedPath);
                ImageIO.write(resizedImage, "jpg", resizedFile);
                fileVO.setRealname(resizedName);
            } else {
                fileVO.setRealname(originalFilename);
            }

            return fileVO;
        } catch (Exception e) {
            log.error("Failed to save image: " + e.getMessage(), e);
            return null;
        }
    }
}