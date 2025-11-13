package com.kapil.verbametrics.services.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WordListFileLoader.
 *
 * @author Kapil Garg
 */
class WordListFileLoaderTest {

    private WordListFileLoader fileLoader;

    @BeforeEach
    void setUp() {
        fileLoader = new WordListFileLoader();
    }

    @Test
    @DisplayName("loadWordsFromFile should load words from valid classpath resource")
    void loadWordsFromFile_validClasspathResource_loadsWords() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertFalse(words.isEmpty(), "Should load words from file");
    }

    @Test
    @DisplayName("loadWordsFromFile should handle classpath: prefix")
    void loadWordsFromFile_withClasspathPrefix_loadsWords() {
        String filePath = "classpath:wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertFalse(words.isEmpty(), "Should load words from file");
    }

    @Test
    @DisplayName("loadWordsFromFile should handle classpath: with leading slash")
    void loadWordsFromFile_withClasspathAndLeadingSlash_loadsWords() {
        String filePath = "classpath:/wordlists/negative-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertFalse(words.isEmpty(), "Should load words from file");
    }

    @Test
    @DisplayName("loadWordsFromFile should filter out comments")
    void loadWordsFromFile_withComments_filtersComments() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.stream().noneMatch(word -> word.startsWith("#")),
                "Should filter out comment lines");
    }

    @Test
    @DisplayName("loadWordsFromFile should trim whitespace from words")
    void loadWordsFromFile_withWhitespace_trimsWords() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.stream().allMatch(word -> word.equals(word.trim())), "Should trim all words");
    }

    @Test
    @DisplayName("loadWordsFromFile should filter out empty lines")
    void loadWordsFromFile_withEmptyLines_filtersEmptyLines() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.stream().noneMatch(String::isEmpty), "Should filter out empty lines");
    }

    @Test
    @DisplayName("loadWordsFromFile should return empty list for null file path")
    void loadWordsFromFile_nullFilePath_returnsEmptyList() {
        List<String> words = fileLoader.loadWordsFromFile(null);
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.isEmpty(), "Should return empty list for null path");
    }

    @Test
    @DisplayName("loadWordsFromFile should return empty list for empty file path")
    void loadWordsFromFile_emptyFilePath_returnsEmptyList() {
        List<String> words = fileLoader.loadWordsFromFile("");
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.isEmpty(), "Should return empty list for empty path");
    }

    @Test
    @DisplayName("loadWordsFromFile should return empty list for blank file path")
    void loadWordsFromFile_blankFilePath_returnsEmptyList() {
        List<String> words = fileLoader.loadWordsFromFile("   ");
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.isEmpty(), "Should return empty list for blank path");
    }

    @Test
    @DisplayName("loadWordsFromFile should return empty list for non-existent file")
    void loadWordsFromFile_nonExistentFile_returnsEmptyList() {
        String filePath = "non-existent-file.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertTrue(words.isEmpty(), "Should return empty list for non-existent file");
    }

    @Test
    @DisplayName("loadWordsFromFile should handle file with only comments")
    void loadWordsFromFile_onlyComments_returnsEmptyList() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
    }

    @Test
    @DisplayName("loadWordsFromFile should use UTF-8 encoding")
    void loadWordsFromFile_usesUtf8Encoding() {
        String filePath = "wordlists/negative-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertFalse(words.isEmpty(), "Should load words with UTF-8 encoding");
        assertTrue(words.stream().noneMatch(word -> word.contains("�")),
                "Should not contain encoding error characters");
    }

    @Test
    @DisplayName("loadWordsFromFile should handle multiple paths")
    void loadWordsFromFile_multiplePaths_loadsEachIndependently() {
        String positivePath = "wordlists/positive-words.txt";
        String negativePath = "wordlists/negative-words.txt";
        List<String> positiveWords = fileLoader.loadWordsFromFile(positivePath);
        List<String> negativeWords = fileLoader.loadWordsFromFile(negativePath);
        assertNotNull(positiveWords, "Should return non-null positive list");
        assertNotNull(negativeWords, "Should return non-null negative list");
        assertFalse(positiveWords.isEmpty(), "Should load positive words");
        assertFalse(negativeWords.isEmpty(), "Should load negative words");
    }

    @Test
    @DisplayName("loadWordsFromFile should return immutable list")
    void loadWordsFromFile_returnsImmutableList() {
        String filePath = "wordlists/positive-words.txt";
        List<String> words = fileLoader.loadWordsFromFile(filePath);
        assertNotNull(words, "Should return non-null list");
        assertThrows(UnsupportedOperationException.class,
                () -> words.add("new-word"),
                "Should return immutable list");
    }

}
