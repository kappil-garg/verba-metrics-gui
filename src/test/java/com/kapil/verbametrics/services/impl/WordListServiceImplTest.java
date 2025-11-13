package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.services.managers.WordListCacheManager;
import com.kapil.verbametrics.services.managers.WordListFileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for WordListServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WordListServiceImplTest {

    @Mock
    private SentimentAnalysisProperties properties;

    @Mock
    private SentimentAnalysisProperties.WordLists wordListsConfig;

    @Mock
    private SentimentAnalysisProperties.TextProcessing textProcessingConfig;

    @Mock
    private WordListFileLoader fileLoader;

    @Mock
    private WordListCacheManager cacheManager;

    private WordListServiceImpl service;

    @BeforeEach
    void setUp() {
        when(properties.getWordLists()).thenReturn(wordListsConfig);
        when(properties.getTextProcessing()).thenReturn(textProcessingConfig);
        when(wordListsConfig.getPositiveWordsPath()).thenReturn("positive.txt");
        when(wordListsConfig.getNegativeWordsPath()).thenReturn("negative.txt");
        when(wordListsConfig.getPositiveWords()).thenReturn(List.of());
        when(wordListsConfig.getNegativeWords()).thenReturn(List.of());
        when(textProcessingConfig.isCaseSensitive()).thenReturn(false);
        when(fileLoader.loadWordsFromFile(anyString())).thenReturn(List.of());
    }

    @Test
    @DisplayName("getPositiveWords should delegate to cache manager")
    void getPositiveWords_delegatesToCacheManager() {
        Set<String> expectedWords = Set.of("good", "great", "excellent");
        when(cacheManager.getPositiveWords()).thenReturn(expectedWords);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        Set<String> actualWords = service.getPositiveWords();
        assertEquals(expectedWords, actualWords, "Should return positive words from cache");
        verify(cacheManager).getPositiveWords();
    }

    @Test
    @DisplayName("getNegativeWords should delegate to cache manager")
    void getNegativeWords_delegatesToCacheManager() {
        Set<String> expectedWords = Set.of("bad", "terrible", "awful");
        when(cacheManager.getNegativeWords()).thenReturn(expectedWords);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        Set<String> actualWords = service.getNegativeWords();
        assertEquals(expectedWords, actualWords, "Should return negative words from cache");
        verify(cacheManager).getNegativeWords();
    }

    @Test
    @DisplayName("loadWordsFromFile should delegate to file loader")
    void loadWordsFromFile_delegatesToFileLoader() {
        String filePath = "custom-words.txt";
        List<String> expectedWords = List.of("word1", "word2", "word3");
        when(fileLoader.loadWordsFromFile(filePath)).thenReturn(expectedWords);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        List<String> actualWords = service.loadWordsFromFile(filePath);
        assertEquals(expectedWords, actualWords, "Should return words from file loader");
        verify(fileLoader).loadWordsFromFile(filePath);
    }

    @Test
    @DisplayName("Constructor should load word lists from configuration")
    void constructor_loadsWordLists() {
        List<String> positiveWords = List.of("happy", "joy");
        List<String> negativeWords = List.of("sad", "angry");
        when(fileLoader.loadWordsFromFile("positive.txt")).thenReturn(positiveWords);
        when(fileLoader.loadWordsFromFile("negative.txt")).thenReturn(negativeWords);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        verify(fileLoader).loadWordsFromFile("positive.txt");
        verify(fileLoader).loadWordsFromFile("negative.txt");
        verify(cacheManager).updateCache(any());
    }

    @Test
    @DisplayName("refreshWordLists should reload word lists")
    void refreshWordLists_reloadsLists() {
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        clearInvocations(fileLoader, cacheManager);
        List<String> newPositiveWords = List.of("new", "positive");
        List<String> newNegativeWords = List.of("new", "negative");
        when(fileLoader.loadWordsFromFile("positive.txt")).thenReturn(newPositiveWords);
        when(fileLoader.loadWordsFromFile("negative.txt")).thenReturn(newNegativeWords);
        service.refreshWordLists();
        verify(fileLoader, atLeastOnce()).loadWordsFromFile("positive.txt");
        verify(fileLoader, atLeastOnce()).loadWordsFromFile("negative.txt");
        verify(cacheManager, atLeastOnce()).updateCache(any());
    }

    @Test
    @DisplayName("Constructor should handle null file paths gracefully")
    void constructor_withNullFilePaths_handlesGracefully() {
        when(wordListsConfig.getPositiveWordsPath()).thenReturn(null);
        when(wordListsConfig.getNegativeWordsPath()).thenReturn(null);
        when(wordListsConfig.getPositiveWords()).thenReturn(List.of("good"));
        when(wordListsConfig.getNegativeWords()).thenReturn(List.of("bad"));
        assertDoesNotThrow(() -> new WordListServiceImpl(properties, fileLoader, cacheManager),
                "Should handle null file paths gracefully");
    }

    @Test
    @DisplayName("Constructor should use configuration word lists when paths are empty")
    void constructor_withEmptyPaths_usesConfigLists() {
        when(wordListsConfig.getPositiveWordsPath()).thenReturn("");
        when(wordListsConfig.getNegativeWordsPath()).thenReturn("");
        when(wordListsConfig.getPositiveWords()).thenReturn(List.of("positive1", "positive2"));
        when(wordListsConfig.getNegativeWords()).thenReturn(List.of("negative1", "negative2"));
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        verify(cacheManager).updateCache(any());
    }

    @Test
    @DisplayName("Service should respect case sensitivity setting")
    void service_respectsCaseSensitivity() {
        when(textProcessingConfig.isCaseSensitive()).thenReturn(true);
        List<String> words = List.of("Good", "GREAT", "excellent");
        when(fileLoader.loadWordsFromFile(anyString())).thenReturn(words);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        verify(cacheManager).updateCache(any());
    }

    @Test
    @DisplayName("Service should convert to lowercase when case insensitive")
    void service_convertsToLowercaseWhenCaseInsensitive() {
        when(textProcessingConfig.isCaseSensitive()).thenReturn(false);
        List<String> words = List.of("Good", "GREAT", "excellent");
        when(fileLoader.loadWordsFromFile(anyString())).thenReturn(words);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        verify(cacheManager).updateCache(any());
    }

    @Test
    @DisplayName("loadWordsFromFile should handle file loader exceptions")
    void loadWordsFromFile_handlesExceptions() {
        String filePath = "invalid.txt";
        RuntimeException expectedException = new RuntimeException("File not found");
        when(fileLoader.loadWordsFromFile(filePath)).thenThrow(expectedException);
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        assertThrows(RuntimeException.class,
                () -> service.loadWordsFromFile(filePath),
                "Should propagate file loader exceptions");
        verify(fileLoader).loadWordsFromFile(filePath);
    }

    @Test
    @DisplayName("refreshWordLists should update cache with new words")
    void refreshWordLists_updatesCacheWithNewWords() {
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        clearInvocations(cacheManager);
        when(fileLoader.loadWordsFromFile(anyString())).thenReturn(List.of("refreshed"));
        service.refreshWordLists();
        verify(cacheManager, atLeastOnce()).updateCache(any());
    }

    @Test
    @DisplayName("Service should handle empty word lists")
    void service_handlesEmptyWordLists() {
        when(fileLoader.loadWordsFromFile(anyString())).thenReturn(List.of());
        when(wordListsConfig.getPositiveWords()).thenReturn(List.of());
        when(wordListsConfig.getNegativeWords()).thenReturn(List.of());
        assertDoesNotThrow(() -> new WordListServiceImpl(properties, fileLoader, cacheManager),
                "Should handle empty word lists gracefully");
    }

    @Test
    @DisplayName("getPositiveWords should return empty set when cache is empty")
    void getPositiveWords_whenCacheEmpty_returnsEmptySet() {
        when(cacheManager.getPositiveWords()).thenReturn(Set.of());
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        Set<String> words = service.getPositiveWords();
        assertNotNull(words, "Should return non-null set");
        assertTrue(words.isEmpty(), "Should return empty set");
    }

    @Test
    @DisplayName("getNegativeWords should return empty set when cache is empty")
    void getNegativeWords_whenCacheEmpty_returnsEmptySet() {
        when(cacheManager.getNegativeWords()).thenReturn(Set.of());
        service = new WordListServiceImpl(properties, fileLoader, cacheManager);
        Set<String> words = service.getNegativeWords();
        assertNotNull(words, "Should return non-null set");
        assertTrue(words.isEmpty(), "Should return empty set");
    }

}
