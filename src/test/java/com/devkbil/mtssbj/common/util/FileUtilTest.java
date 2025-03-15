package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void getNewName() {
        String name1 = FileUtil.getNewName();
        String name2 = FileUtil.getNewName();

        assertNotNull(name1);
        assertNotNull(name2);
        assertNotEquals(name1, name2);
        assertEquals(18, name1.length()); // yyyyMMddhhmmssSSS + 1 random digit
    }

    @Test
    void getFileExtension() {
        assertEquals("txt", FileUtil.getFileExtension("test.txt"));
        assertEquals("jpg", FileUtil.getFileExtension("path/to/image.jpg"));
        assertEquals("gz", FileUtil.getFileExtension("archive.tar.gz"));
    }

    @Test
    void ltrim() {
        assertEquals("test", FileUtil.ltrim("/test", "/"));
        assertEquals("test", FileUtil.ltrim("test", "/"));
        assertEquals("test", FileUtil.ltrim("/path/to/test", "/path/to/"));
        assertEquals("test", FileUtil.ltrim("test", ""));
    }

    @Test
    void getRealPath() {
        String filename = "20230615123456789";
        String path = "/upload/";
        String expected = "/upload/2023/06/15/";
        assertEquals(expected, FileUtil.getRealPath(path, filename));
    }

    @Test
    void existFile() throws Exception {
        File testFile = new File(tempDir.toFile(), "test.txt");
        testFile.createNewFile();

        assertTrue(FileUtil.existFile(testFile.getAbsolutePath()));
        assertTrue(FileUtil.existFile("test.txt", tempDir.toString()));
        assertFalse(FileUtil.existFile("nonexistent.txt", tempDir.toString()));
    }

    @Test
    void existDirectory() {
        File testDir = new File(tempDir.toFile(), "testDir");
        testDir.mkdir();

        assertTrue(FileUtil.existDirectory(testDir.getAbsolutePath()));
        assertFalse(FileUtil.existDirectory(tempDir + "/nonexistent"));
    }

    @Test
    void renameFile() throws Exception {
        assertEquals("test1.txt", FileUtil.renameFile("test.txt"));
        assertEquals("test1", FileUtil.renameFile("test"));
        assertEquals("test11.txt", FileUtil.renameFile("test1.txt"));
    }
}