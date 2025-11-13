package com.kapil.verbametrics.services.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProcessedWordLists record.
 *
 * @author Kapil Garg
 */
class ProcessedWordListsTest {

    @Test
    @DisplayName("ProcessedWordLists should create with valid sets")
    void processedWordLists_validSets_createsSuccessfully() {
        Set<String> positiveWords = Set.of("good", "great", "excellent");
        Set<String> negativeWords = Set.of("bad", "terrible", "awful");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
        assertNotNull(wordLists, "Word lists should not be null");
        assertEquals(positiveWords, wordLists.positiveWords(), "Should have correct positive words");
        assertEquals(negativeWords, wordLists.negativeWords(), "Should have correct negative words");
    }

    @Test
    @DisplayName("ProcessedWordLists should handle empty sets")
    void processedWordLists_emptySets_createsSuccessfully() {
        Set<String> emptyPositive = Set.of();
        Set<String> emptyNegative = Set.of();
        ProcessedWordLists wordLists = new ProcessedWordLists(emptyPositive, emptyNegative);
        assertNotNull(wordLists, "Word lists should not be null");
        assertTrue(wordLists.positiveWords().isEmpty(), "Positive words should be empty");
        assertTrue(wordLists.negativeWords().isEmpty(), "Negative words should be empty");
    }

    @Test
    @DisplayName("ProcessedWordLists should support null sets")
    void processedWordLists_nullSets_createsSuccessfully() {
        ProcessedWordLists wordLists = new ProcessedWordLists(null, null);
        assertNotNull(wordLists, "Word lists should not be null");
        assertNull(wordLists.positiveWords(), "Positive words can be null");
        assertNull(wordLists.negativeWords(), "Negative words can be null");
    }

    @Test
    @DisplayName("ProcessedWordLists should support single word sets")
    void processedWordLists_singleWordSets_createsSuccessfully() {
        Set<String> singlePositive = Set.of("good");
        Set<String> singleNegative = Set.of("bad");
        ProcessedWordLists wordLists = new ProcessedWordLists(singlePositive, singleNegative);
        assertEquals(1, wordLists.positiveWords().size(), "Should have 1 positive word");
        assertEquals(1, wordLists.negativeWords().size(), "Should have 1 negative word");
    }

    @Test
    @DisplayName("ProcessedWordLists should support large sets")
    void processedWordLists_largeSets_createsSuccessfully() {
        Set<String> largePositive = Set.of("word1", "word2", "word3", "word4", "word5",
                "word6", "word7", "word8", "word9", "word10");
        Set<String> largeNegative = Set.of("neg1", "neg2", "neg3", "neg4", "neg5",
                "neg6", "neg7", "neg8", "neg9", "neg10");
        ProcessedWordLists wordLists = new ProcessedWordLists(largePositive, largeNegative);
        assertEquals(10, wordLists.positiveWords().size(), "Should have 10 positive words");
        assertEquals(10, wordLists.negativeWords().size(), "Should have 10 negative words");
    }

    @Test
    @DisplayName("ProcessedWordLists should implement equals correctly")
    void processedWordLists_equals_worksCorrectly() {
        Set<String> positiveWords = Set.of("good", "great");
        Set<String> negativeWords = Set.of("bad", "terrible");
        ProcessedWordLists wordLists1 = new ProcessedWordLists(positiveWords, negativeWords);
        ProcessedWordLists wordLists2 = new ProcessedWordLists(positiveWords, negativeWords);
        assertEquals(wordLists1, wordLists2, "Equal records should be equal");
    }

    @Test
    @DisplayName("ProcessedWordLists should implement hashCode correctly")
    void processedWordLists_hashCode_worksCorrectly() {
        Set<String> positiveWords = Set.of("good", "great");
        Set<String> negativeWords = Set.of("bad", "terrible");
        ProcessedWordLists wordLists1 = new ProcessedWordLists(positiveWords, negativeWords);
        ProcessedWordLists wordLists2 = new ProcessedWordLists(positiveWords, negativeWords);
        assertEquals(wordLists1.hashCode(), wordLists2.hashCode(), "Equal records should have equal hash codes");
    }

    @Test
    @DisplayName("ProcessedWordLists toString should include both fields")
    void processedWordLists_toString_includesFields() {
        Set<String> positiveWords = Set.of("good");
        Set<String> negativeWords = Set.of("bad");
        ProcessedWordLists wordLists = new ProcessedWordLists(positiveWords, negativeWords);
        String toString = wordLists.toString();
        assertNotNull(toString, "toString should not be null");
        assertTrue(toString.contains("positiveWords") || toString.contains("good"),
                "toString should include positive words info");
        assertTrue(toString.contains("negativeWords") || toString.contains("bad"),
                "toString should include negative words info");
    }

}
