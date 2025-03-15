package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileUpload;
import com.devkbil.mtssbj.common.FileVO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUploadTest {

    @TempDir
    Path tempDir;

    private FileUpload fileUpload;
    private String originalFilename;
    private byte[] content;

    @BeforeEach
    void setUp() {
        fileUpload = new FileUpload();
        originalFilename = "test.txt";
        content = "Test content".getBytes();
        System.setProperty("user.dir", tempDir.toFile().getAbsolutePath());
    }

    @Test
    void saveFileOne() throws IOException {
        MultipartFile file = new MockMultipartFile("file", originalFilename, "text/plain", content);
        String basePath = tempDir.toFile().getAbsolutePath() + "/upload/";
        String fileName = "testfile.txt";

        String savedPath = FileUpload.saveFileOne(file, basePath, fileName);

        assertNotNull(savedPath);
        assertTrue(new File(savedPath).exists());
    }

    @Test
    void saveFile() {
        MultipartFile file = new MockMultipartFile("file", originalFilename, "text/plain", content);

        FileVO result = fileUpload.saveFile(file);

        assertNotNull(result);
        assertEquals(originalFilename, result.getFilename());
        assertEquals(content.length, result.getFilesize());
        assertTrue(new File(tempDir.toFile(), "fileupload").exists());
    }

    @Test
    void saveAllFiles() {
        MultipartFile file1 = new MockMultipartFile("file1", "test1.txt", "text/plain", "content1".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "test2.txt", "text/plain", "content2".getBytes());
        List<MultipartFile> files = Arrays.asList(file1, file2);

        List<FileVO> results = fileUpload.saveAllFiles(files);

        assertEquals(2, results.size());
        assertEquals("test1.txt", results.get(0).getFilename());
        assertEquals("test2.txt", results.get(1).getFilename());
    }

    @Test
    void saveImage() throws IOException {
        // Create a test image
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes;
        try (var baos = new java.io.ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            imageBytes = baos.toByteArray();
        }

        MultipartFile file = new MockMultipartFile("image", "back.jpg", "image/jpeg", imageBytes);

        FileVO result = fileUpload.saveImage(file);

        assertNotNull(result);
        assertEquals("back.jpg", result.getFilename());
        assertTrue(result.getRealname().endsWith("1")); // Resized image has "1" appended
    }

    @Test
    void saveImage_SmallImage() throws IOException {
        // Create a small test image
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes;
        try (var baos = new java.io.ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            imageBytes = baos.toByteArray();
        }

        MultipartFile file = new MockMultipartFile("image", "back.jpg", "image/jpeg", imageBytes);

        FileVO result = fileUpload.saveImage(file);

        assertNotNull(result);
        assertEquals("back.jpg", result.getFilename());
        assertFalse(result.getRealname().endsWith("1")); // Small image should not be resized
    }

    @Test
    void saveFile_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        FileVO result = fileUpload.saveFile(emptyFile);

        assertNull(result);
    }

    @Test
    void saveAllFiles_WithEmptyFiles() {
        MultipartFile file1 = new MockMultipartFile("file1", "test1.txt", "text/plain", "content1".getBytes());
        MultipartFile emptyFile = new MockMultipartFile("file2", "empty.txt", "text/plain", new byte[0]);
        List<MultipartFile> files = Arrays.asList(file1, emptyFile);

        List<FileVO> results = fileUpload.saveAllFiles(files);

        assertEquals(1, results.size());
        assertEquals("test1.txt", results.get(0).getFilename());
    }
}