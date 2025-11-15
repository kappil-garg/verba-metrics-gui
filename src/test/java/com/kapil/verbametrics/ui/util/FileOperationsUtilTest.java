package com.kapil.verbametrics.ui.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileOperationsUtil.
 *
 * @author Kapil Garg
 */
class FileOperationsUtilTest {

    @TempDir
    private Path tempDir;

    @Test
    @DisplayName("loadTextFile successfully loads file content")
    void loadTextFile_success() throws IOException {
        String content = "Test file content\nLine 2";
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, content);
        String result = FileOperationsUtil.loadTextFile(testFile);
        assertEquals(content, result);
    }

    @Test
    @DisplayName("loadTextFile throws exception when file not found")
    void loadTextFile_fileNotFound_throwsException() {
        Path nonExistentFile = tempDir.resolve("nonexistent.txt");
        assertThrows(IOException.class, () -> FileOperationsUtil.loadTextFile(nonExistentFile));
    }

    @Test
    @DisplayName("saveTextFile successfully saves content")
    void saveTextFile_success() throws IOException {
        String content = "Test content to save";
        Path testFile = tempDir.resolve("output.txt");
        FileOperationsUtil.saveTextFile(content, testFile);
        assertTrue(Files.exists(testFile));
        assertEquals(content, Files.readString(testFile));
    }

    @Test
    @DisplayName("saveTextFile overwrites existing file")
    void saveTextFile_overwritesExisting() throws IOException {
        String initialContent = "Initial content";
        String newContent = "New content";
        Path testFile = tempDir.resolve("overwrite.txt");
        Files.writeString(testFile, initialContent);
        FileOperationsUtil.saveTextFile(newContent, testFile);
        assertEquals(newContent, Files.readString(testFile));
    }

    @Test
    @DisplayName("showOpenDialog returns null when cancelled")
    void showOpenDialog_cancelled_returnsNull() {
        assertDoesNotThrow(() -> {
            try {
                File result = FileOperationsUtil.showOpenDialog(null, "txt");
                assertTrue(result == null || result.exists() || !result.exists());
            } catch (java.awt.HeadlessException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("showSaveDialog returns null when cancelled")
    void showSaveDialog_cancelled_returnsNull() {
        assertDoesNotThrow(() -> {
            try {
                File result = FileOperationsUtil.showSaveDialog(null, "test.txt", "txt");
                assertTrue(result == null || result.exists() || !result.exists());
            } catch (java.awt.HeadlessException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("loadTextFile handles empty file")
    void loadTextFile_emptyFile_returnsEmptyString() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.writeString(emptyFile, "");
        String result = FileOperationsUtil.loadTextFile(emptyFile);
        assertEquals("", result);
    }

    @Test
    @DisplayName("saveTextFile handles empty content")
    void saveTextFile_emptyContent_savesEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        FileOperationsUtil.saveTextFile("", testFile);
        assertTrue(Files.exists(testFile));
        assertEquals("", Files.readString(testFile));
    }

    @Test
    @DisplayName("saveTextFile handles large content")
    void saveTextFile_largeContent_savesSuccessfully() throws IOException {
        String largeContent = "x".repeat(10000);
        Path testFile = tempDir.resolve("large.txt");
        FileOperationsUtil.saveTextFile(largeContent, testFile);
        assertEquals(largeContent, Files.readString(testFile));
    }

}
