package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileSizeTest {

    private final String testContent = "Test content with known length";
    @TempDir
    Path tempDir;
    private File testDir;
    private File subDir;
    private FileSize fileSize;

    @BeforeEach
    void setUp() throws IOException {
        testDir = new File(tempDir.toFile(), "testDir");
        testDir.mkdir();

        // Create test files
        createTestFile(testDir, "file1.txt", testContent);
        createTestFile(testDir, "file2.txt", testContent);

        // Create subdirectory with files
        subDir = new File(testDir, "subDir");
        subDir.mkdir();
        createTestFile(subDir, "subfile1.txt", testContent);
        createTestFile(subDir, "subfile2.txt", testContent);
    }

    private File createTestFile(File dir, String name, String content) throws IOException {
        File file = new File(dir, name);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void size_File() throws Exception {
        File testFile = new File(testDir, "file1.txt");
        long size = fileSize.size(testFile.getAbsolutePath());
        assertEquals(testContent.length(), size);
    }

    @Test
    void size_Directory() throws Exception {
        long size = fileSize.size(testDir.getAbsolutePath());
        // Directory should contain size of immediate files only
        assertEquals(testContent.length() * 2, size);
    }

    @Test
    void sizeTree() throws Exception {
        long size = fileSize.sizeTree(testDir.getAbsolutePath());
        // Should include all files in directory tree
        assertEquals(testContent.length() * 4, size);
    }

    @Test
    void countFile_File() throws Exception {
        File testFile = new File(testDir, "file1.txt");
        int count = fileSize.countFile(testFile.getAbsolutePath());
        assertEquals(1, count);
    }

    @Test
    void countFile_Directory() throws Exception {
        int count = fileSize.countFile(testDir.getAbsolutePath());
        // Should count only immediate files
        assertEquals(2, count);
    }

    @Test
    void countFileTree() throws Exception {
        int count = fileSize.countFileTree(testDir.getAbsolutePath());
        // Should count all files in directory tree
        assertEquals(4, count);
    }

    @Test
    void size_NonExistentFile() throws Exception {
        long size = fileSize.size(tempDir.toFile().getAbsolutePath() + "/nonexistent");
        assertEquals(0, size);
    }

    @Test
    void sizeTree_NonExistentFile() throws Exception {
        long size = fileSize.sizeTree(tempDir.toFile().getAbsolutePath() + "/nonexistent");
        assertEquals(0, size);
    }

    @Test
    void countFile_NonExistentFile() throws Exception {
        int count = fileSize.countFile(tempDir.toFile().getAbsolutePath() + "/nonexistent");
        assertEquals(0, count);
    }

    @Test
    void countFileTree_NonExistentFile() throws Exception {
        int count = fileSize.countFileTree(tempDir.toFile().getAbsolutePath() + "/nonexistent");
        assertEquals(0, count);
    }
}