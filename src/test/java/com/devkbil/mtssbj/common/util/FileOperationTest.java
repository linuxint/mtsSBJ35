package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileCore;
import com.devkbil.common.util.FileOperation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileOperationTest {

    @TempDir
    Path tempDir;

    private File createTestFile(String name, String content) throws IOException {
        File file = new File(tempDir.toFile(), name);
        try (java.io.Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void fileExist() throws IOException {
        File testFile = createTestFile("test.txt", "test content");
        assertTrue(FileOperation.fileExist(testFile.getAbsolutePath()));
        assertFalse(FileOperation.fileExist(tempDir + "/nonexistent.txt"));
    }

    @Test
    void fileRename() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        File targetFile = new File(tempDir.toFile(), "target.txt");

        assertEquals(FileCore.nSuccess, FileOperation.fileRename(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()));
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
    }

    @Test
    void fileMove() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        File targetFile = new File(tempDir.toFile(), "target.txt");

        assertTrue(FileOperation.fileMove(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()));
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
    }

    @Test
    void copyFile() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        String targetPath = tempDir.toFile().getAbsolutePath() + "/target.txt";

        assertEquals(FileCore.nSuccess, FileOperation.copyFile(sourceFile.getAbsolutePath(), targetPath));
        assertTrue(sourceFile.exists());
        assertTrue(new File(targetPath).exists());
    }

    @Test
    void bufferFileCopy() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        String targetPath = tempDir.toFile().getAbsolutePath() + "/target.txt";

        assertEquals(FileCore.nSuccess, FileOperation.bufferFileCopy(sourceFile.getAbsolutePath(), targetPath));
        assertTrue(sourceFile.exists());
        assertTrue(new File(targetPath).exists());
    }

    @Test
    void channelFileCopy() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        String targetPath = tempDir.toFile().getAbsolutePath() + "/target.txt";

        assertEquals(FileCore.nSuccess, FileOperation.channelFileCopy(sourceFile.getAbsolutePath(), targetPath));
        assertTrue(sourceFile.exists());
        assertTrue(new File(targetPath).exists());
    }

    @Test
    void streamFileCopy() throws IOException {
        File sourceFile = createTestFile("source.txt", "test content");
        String targetPath = tempDir.toFile().getAbsolutePath() + "/target.txt";

        assertEquals(FileCore.nSuccess, FileOperation.streamFileCopy(sourceFile.getAbsolutePath(), targetPath));
        assertTrue(sourceFile.exists());
        assertTrue(new File(targetPath).exists());
    }

    @Test
    void rename() throws Exception {
        File sourceFile = createTestFile("source.txt", "test content");
        File renamedFile = FileOperation.rename(sourceFile, "renamed.txt");

        assertNotNull(renamedFile);
        assertTrue(renamedFile.exists());
        assertFalse(sourceFile.exists());
        assertEquals("renamed.txt", renamedFile.getName());
    }

    @Test
    void copy() throws Exception {
        // Create source file and directory
        File sourceFile = createTestFile("source.txt", "test content");
        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();

        File copiedFile = FileOperation.copy(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());

        assertNotNull(copiedFile);
        assertTrue(copiedFile.exists());
        assertTrue(sourceFile.exists());
        assertEquals(sourceFile.getName(), copiedFile.getName());
    }

    @Test
    void copyTree() throws Exception {
        // Create source directory with files
        File sourceDir = new File(tempDir.toFile(), "source");
        sourceDir.mkdir();
        createTestFile("source/file1.txt", "content1");
        createTestFile("source/file2.txt", "content2");

        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();

        File copiedDir = FileOperation.copyTree(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());

        assertNotNull(copiedDir);
        assertTrue(copiedDir.exists());
        assertTrue(new File(copiedDir, "file1.txt").exists());
        assertTrue(new File(copiedDir, "file2.txt").exists());
    }
}