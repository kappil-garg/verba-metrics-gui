package com.kapil.verbametrics.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BasicTextStatistics domain record.
 *
 * @author Kapil Garg
 */
class BasicTextStatisticsTest {

    @Test
    @DisplayName("Constructor creates valid statistics")
    void constructor_validValues() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        assertEquals(100, stats.wordCount());
        assertEquals(5, stats.sentenceCount());
        assertEquals(500, stats.characterCount());
        assertEquals(400, stats.characterCountNoSpaces());
        assertEquals(3, stats.paragraphCount());
    }

    @Test
    @DisplayName("Constructor accepts zero values")
    void constructor_zeroValues() {
        BasicTextStatistics stats = new BasicTextStatistics(0, 0, 0, 0, 0);
        assertEquals(0, stats.wordCount());
        assertEquals(0, stats.sentenceCount());
        assertEquals(0, stats.characterCount());
        assertEquals(0, stats.characterCountNoSpaces());
        assertEquals(0, stats.paragraphCount());
    }

    @Test
    @DisplayName("Constructor rejects negative word count")
    void constructor_negativeWordCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(-1, 5, 500, 400, 3)
        );
        assertEquals("Word count cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative sentence count")
    void constructor_negativeSentenceCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(100, -1, 500, 400, 3)
        );
        assertEquals("Sentence count cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative character count")
    void constructor_negativeCharacterCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(100, 5, -1, 400, 3)
        );
        assertEquals("Character count cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative characterCountNoSpaces")
    void constructor_negativeCharacterCountNoSpaces() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(100, 5, 500, -1, 3)
        );
        assertEquals("Character count without spaces cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative paragraph count")
    void constructor_negativeParagraphCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(100, 5, 500, 400, -1)
        );
        assertEquals("Paragraph count cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects characterCountNoSpaces > characterCount")
    void constructor_invalidCharacterCounts() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BasicTextStatistics(100, 5, 400, 500, 3)
        );
        assertEquals("Character count without spaces cannot exceed total character count", exception.getMessage());
    }

    @Test
    @DisplayName("averageWordsPerSentence calculates correctly")
    void averageWordsPerSentence_valid() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        assertEquals(20.0, stats.averageWordsPerSentence(), 0.001);
    }

    @Test
    @DisplayName("averageWordsPerSentence returns zero for no sentences")
    void averageWordsPerSentence_zeroSentences() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 0, 500, 400, 3);
        assertEquals(0.0, stats.averageWordsPerSentence(), 0.001);
    }

    @Test
    @DisplayName("averageCharactersPerWord calculates correctly")
    void averageCharactersPerWord_valid() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        assertEquals(4.0, stats.averageCharactersPerWord(), 0.001);
    }

    @Test
    @DisplayName("averageCharactersPerWord returns zero for no words")
    void averageCharactersPerWord_zeroWords() {
        BasicTextStatistics stats = new BasicTextStatistics(0, 5, 500, 400, 3);
        assertEquals(0.0, stats.averageCharactersPerWord(), 0.001);
    }

    @Test
    @DisplayName("toString contains all field values")
    void testToString() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        String result = stats.toString();
        assertTrue(result.contains("BasicTextStatistics"));
        assertTrue(result.contains("wordCount=100"));
        assertTrue(result.contains("sentenceCount=5"));
        assertTrue(result.contains("characterCount=500"));
        assertTrue(result.contains("characterCountNoSpaces=400"));
        assertTrue(result.contains("paragraphCount=3"));
        assertTrue(result.contains("avgWordsPerSentence=20.00"));
        assertTrue(result.contains("avgCharactersPerWord=4.00"));
    }

    @Test
    @DisplayName("Record equality works correctly")
    void testEquals() {
        BasicTextStatistics stats1 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats2 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats3 = new BasicTextStatistics(100, 5, 500, 401, 3);
        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
    }

    @Test
    @DisplayName("Record hashCode works correctly")
    void testHashCode() {
        BasicTextStatistics stats1 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats2 = new BasicTextStatistics(100, 5, 500, 400, 3);
        assertEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    @DisplayName("Edge case: single word, single sentence, single paragraph")
    void edgeCase_singleEverything() {
        BasicTextStatistics stats = new BasicTextStatistics(1, 1, 5, 5, 1);
        assertEquals(1.0, stats.averageWordsPerSentence(), 0.001);
        assertEquals(5.0, stats.averageCharactersPerWord(), 0.001);
    }

    @Test
    @DisplayName("Edge case: text with all spaces")
    void edgeCase_allSpaces() {
        BasicTextStatistics stats = new BasicTextStatistics(0, 0, 10, 0, 0);
        assertEquals(10, stats.characterCount());
        assertEquals(0, stats.characterCountNoSpaces());
        assertEquals(0.0, stats.averageCharactersPerWord(), 0.001);
    }

}
