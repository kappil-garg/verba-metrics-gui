package com.kapil.verbametrics.services.managers;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.services.models.ProcessedWordLists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WordListCacheManager.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class WordListCacheManagerTest {

    @Mock
    private SentimentAnalysisProperties properties;

    private WordListCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new WordListCacheManager(properties);
    }

    @Test
    @DisplayName("Constructor should initialize with empty cache")
    void constructor_initializesWithEmptyCache() {
        assertNotNull(cacheManager.getPositiveWords(), "Positive words set should not be null");
        assertNotNull(cacheManager.getNegativeWords(), "Negative words set should not be null");
        assertTrue(cacheManager.getPositiveWords().isEmpty(), "Positive words should be empty initially");
        assertTrue(cacheManager.getNegativeWords().isEmpty(), "Negative words should be empty initially");
    }

    @Test
    @DisplayName("updateCache should populate positive and negative words")
    void updateCache_populatesWords() {
        Set<String> positiveWords = Set.of("good", "great", "excellent");
        Set<String> negativeWords = Set.of("bad", "terrible", "awful");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
        cacheManager.updateCache(wordLists);
        assertEquals(3, cacheManager.getPositiveWords().size(), "Should have 3 positive words");
        assertEquals(3, cacheManager.getNegativeWords().size(), "Should have 3 negative words");
        assertTrue(cacheManager.getPositiveWords().containsAll(positiveWords), "Should contain all positive words");
        assertTrue(cacheManager.getNegativeWords().containsAll(negativeWords), "Should contain all negative words");
    }

    @Test
    @DisplayName("updateCache should clear previous cache before updating")
    void updateCache_clearsPreviousCache() {
        Set<String> initialPositive = Set.of("happy", "joy");
        Set<String> initialNegative = Set.of("sad", "angry");
        ProcessedWordLists initialLists = new ProcessedWordLists(initialPositive, initialNegative);
        cacheManager.updateCache(initialLists);
        Set<String> newPositive = Set.of("fantastic", "wonderful");
        Set<String> newNegative = Set.of("horrible", "dreadful");
        ProcessedWordLists newLists = new ProcessedWordLists(newPositive, newNegative);
        cacheManager.updateCache(newLists);
        assertEquals(2, cacheManager.getPositiveWords().size(), "Should have only new positive words");
        assertEquals(2, cacheManager.getNegativeWords().size(), "Should have only new negative words");
        assertFalse(cacheManager.getPositiveWords().contains("happy"), "Should not contain old positive words");
        assertFalse(cacheManager.getNegativeWords().contains("sad"), "Should not contain old negative words");
        assertTrue(cacheManager.getPositiveWords().contains("fantastic"), "Should contain new positive words");
        assertTrue(cacheManager.getNegativeWords().contains("horrible"), "Should contain new negative words");
    }

    @Test
    @DisplayName("clearCache should remove all cached words")
    void clearCache_removesAllWords() {
        Set<String> positiveWords = Set.of("good", "great");
        Set<String> negativeWords = Set.of("bad", "terrible");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
        cacheManager.updateCache(wordLists);
        cacheManager.clearCache();
        assertTrue(cacheManager.getPositiveWords().isEmpty(), "Positive words should be empty after clear");
        assertTrue(cacheManager.getNegativeWords().isEmpty(), "Negative words should be empty after clear");
    }

    @Test
    @DisplayName("updateCache should handle empty word lists")
    void updateCache_handlesEmptyWordLists() {
        ProcessedWordLists emptyLists = new ProcessedWordLists(Set.of(), Set.of());
        cacheManager.updateCache(emptyLists);
        assertTrue(cacheManager.getPositiveWords().isEmpty(), "Positive words should be empty");
        assertTrue(cacheManager.getNegativeWords().isEmpty(), "Negative words should be empty");
    }

    @Test
    @DisplayName("getPositiveWords should return reference to internal set")
    void getPositiveWords_returnsInternalSet() {
        Set<String> positiveWords = Set.of("good");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, Set.of());
        cacheManager.updateCache(wordLists);
        Set<String> retrievedWords = cacheManager.getPositiveWords();
        assertSame(cacheManager.getPositiveWords(), retrievedWords, "Should return same set reference");
    }

    @Test
    @DisplayName("getNegativeWords should return reference to internal set")
    void getNegativeWords_returnsInternalSet() {
        Set<String> negativeWords = Set.of("bad");
        ProcessedWordLists wordLists = new ProcessedWordLists(Set.of(), negativeWords);
        cacheManager.updateCache(wordLists);
        Set<String> retrievedWords = cacheManager.getNegativeWords();
        assertSame(cacheManager.getNegativeWords(), retrievedWords, "Should return same set reference");
    }

    @Test
    @DisplayName("updateCache should handle large word lists")
    void updateCache_handlesLargeWordLists() {
        Set<String> largePositiveSet = Set.of("word1", "word2", "word3", "word4", "word5",
                "word6", "word7", "word8", "word9", "word10");
        Set<String> largeNegativeSet = Set.of("neg1", "neg2", "neg3", "neg4", "neg5",
                "neg6", "neg7", "neg8", "neg9", "neg10");
        ProcessedWordLists largeLists = new ProcessedWordLists(largePositiveSet, largeNegativeSet);
        cacheManager.updateCache(largeLists);
        assertEquals(10, cacheManager.getPositiveWords().size(), "Should handle large positive list");
        assertEquals(10, cacheManager.getNegativeWords().size(), "Should handle large negative list");
    }

    @Test
    @DisplayName("Cache should support multiple update cycles")
    void cache_supportsMultipleUpdateCycles() {
        for (int i = 0; i < 5; i++) {
            Set<String> positiveWords = Set.of("positive" + i);
            Set<String> negativeWords = Set.of("negative" + i);
            ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
            cacheManager.updateCache(wordLists);
        }
        assertTrue(cacheManager.getPositiveWords().contains("positive4"), "Should have last positive update");
        assertTrue(cacheManager.getNegativeWords().contains("negative4"), "Should have last negative update");
        assertEquals(1, cacheManager.getPositiveWords().size(), "Should only have last update");
        assertEquals(1, cacheManager.getNegativeWords().size(), "Should only have last update");
    }

    @Test
    @DisplayName("getPositiveWords should be callable multiple times")
    void getPositiveWords_callableMultipleTimes() {
        Set<String> positiveWords = Set.of("good");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, Set.of());
        cacheManager.updateCache(wordLists);
        for (int i = 0; i < 10; i++) {
            Set<String> words = cacheManager.getPositiveWords();
            assertEquals(1, words.size(), "Should return consistent results");
            assertTrue(words.contains("good"), "Should contain expected word");
        }
    }

    @Test
    @DisplayName("getNegativeWords should be callable multiple times")
    void getNegativeWords_callableMultipleTimes() {
        Set<String> negativeWords = Set.of("bad");
        ProcessedWordLists wordLists = new ProcessedWordLists(Set.of(), negativeWords);
        cacheManager.updateCache(wordLists);
        for (int i = 0; i < 10; i++) {
            Set<String> words = cacheManager.getNegativeWords();
            assertEquals(1, words.size(), "Should return consistent results");
            assertTrue(words.contains("bad"), "Should contain expected word");
        }
    }

    @Test
    @DisplayName("updateCache should handle single word lists")
    void updateCache_handlesSingleWordLists() {
        ProcessedWordLists singleWordLists = new ProcessedWordLists(
                Set.of("good"),
                Set.of("bad")
        );
        cacheManager.updateCache(singleWordLists);
        assertEquals(1, cacheManager.getPositiveWords().size(), "Should have 1 positive word");
        assertEquals(1, cacheManager.getNegativeWords().size(), "Should have 1 negative word");
        assertTrue(cacheManager.getPositiveWords().contains("good"), "Should contain positive word");
        assertTrue(cacheManager.getNegativeWords().contains("bad"), "Should contain negative word");
    }

    @Test
    @DisplayName("Cache should maintain word case")
    void cache_maintainsWordCase() {
        Set<String> positiveWords = Set.of("Good", "GREAT", "excellent");
        Set<String> negativeWords = Set.of("Bad", "TERRIBLE", "awful");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
        cacheManager.updateCache(wordLists);
        assertTrue(cacheManager.getPositiveWords().contains("Good"), "Should maintain 'Good' case");
        assertTrue(cacheManager.getPositiveWords().contains("GREAT"), "Should maintain 'GREAT' case");
        assertTrue(cacheManager.getNegativeWords().contains("Bad"), "Should maintain 'Bad' case");
        assertTrue(cacheManager.getNegativeWords().contains("TERRIBLE"), "Should maintain 'TERRIBLE' case");
    }

}
