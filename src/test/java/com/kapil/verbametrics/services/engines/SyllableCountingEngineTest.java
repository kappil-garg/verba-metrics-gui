package com.kapil.verbametrics.services.engines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for SyllableCountingEngine.
 *
 * @author Kapil Garg
 */
class SyllableCountingEngineTest {

    private SyllableCountingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new SyllableCountingEngine();
    }

    @Test
    @DisplayName("countSyllables single word should count syllables correctly")
    void countSyllables_singleWord_countsCorrectly() {
        assertEquals(1, engine.countSyllables("cat"), "cat should have 1 syllable");
        assertEquals(2, engine.countSyllables("hello"), "hello should have 2 syllables");
        assertEquals(3, engine.countSyllables("beautiful"), "beautiful should have 3 syllables");
        assertEquals(2, engine.countSyllables("running"), "running should have 2 syllables");
    }

    @Test
    @DisplayName("countSyllables should handle words ending in silent e")
    void countSyllables_silentE_countsCorrectly() {
        assertEquals(1, engine.countSyllables("make"), "make should have 1 syllable (silent e)");
        assertEquals(1, engine.countSyllables("time"), "time should have 1 syllable (silent e)");
        assertEquals(1, engine.countSyllables("late"), "late should have 1 syllable (silent e)");
    }

    @Test
    @DisplayName("countSyllables should handle null word")
    void countSyllables_nullWord_returnsZero() {
        int count = engine.countSyllables((String) null);
        assertEquals(0, count, "Null word should return 0 syllables");
    }

    @Test
    @DisplayName("countSyllables should handle empty word")
    void countSyllables_emptyWord_returnsZero() {
        int count = engine.countSyllables("");
        assertEquals(0, count, "Empty word should return 0 syllables");
    }

    @Test
    @DisplayName("countSyllables should handle blank word")
    void countSyllables_blankWord_returnsZero() {
        int count = engine.countSyllables("   ");
        assertEquals(0, count, "Blank word should return 0 syllables");
    }

    @Test
    @DisplayName("countSyllables should handle single vowel")
    void countSyllables_singleVowel_returnsOne() {
        assertEquals(1, engine.countSyllables("a"), "Single vowel should have 1 syllable");
        assertEquals(1, engine.countSyllables("I"), "Single vowel should have 1 syllable");
    }

    @Test
    @DisplayName("countSyllables should handle words with consecutive vowels")
    void countSyllables_consecutiveVowels_countsAsOneGroup() {
        assertEquals(1, engine.countSyllables("eat"), "eat should have 1 syllable (ea is one group)");
        assertEquals(2, engine.countSyllables("idea"), "idea should have 2 syllables");
    }

    @Test
    @DisplayName("countSyllables should handle words with y as vowel")
    void countSyllables_yAsVowel_countsCorrectly() {
        assertEquals(1, engine.countSyllables("my"), "my should have 1 syllable");
        assertEquals(2, engine.countSyllables("happy"), "happy should have 2 syllables");
    }

    @Test
    @DisplayName("countSyllables should normalize case")
    void countSyllables_mixedCase_normalizesCorrectly() {
        assertEquals(engine.countSyllables("hello"), engine.countSyllables("HELLO"),
                "Case should not affect syllable count");
        assertEquals(engine.countSyllables("beautiful"), engine.countSyllables("BeAuTiFuL"),
                "Case should not affect syllable count");
    }

    @Test
    @DisplayName("countSyllables should handle words with non-alphabetic characters")
    void countSyllables_withSpecialChars_removesNonLetters() {
        assertEquals(engine.countSyllables("hello"), engine.countSyllables("hello!"),
                "Special characters should be removed");
        assertEquals(engine.countSyllables("beautiful"), engine.countSyllables("beautiful123"),
                "Numbers should be removed");
        assertEquals(engine.countSyllables("running"), engine.countSyllables("run-ning"),
                "Hyphens should be removed");
    }

    @Test
    @DisplayName("countSyllables array should count total syllables")
    void countSyllables_array_countsTotalSyllables() {
        String[] words = {"cat", "dog", "beautiful", "running"};
        int total = engine.countSyllables(words);
        assertTrue(total >= 6, "Should count syllables for all words");
    }

    @Test
    @DisplayName("countSyllables array should handle null array")
    void countSyllables_nullArray_returnsZero() {
        int count = engine.countSyllables((String[]) null);
        assertEquals(0, count, "Null array should return 0");
    }

    @Test
    @DisplayName("countSyllables array should handle empty array")
    void countSyllables_emptyArray_returnsZero() {
        int count = engine.countSyllables(new String[0]);
        assertEquals(0, count, "Empty array should return 0");
    }

    @Test
    @DisplayName("countSyllables array should skip null elements")
    void countSyllables_arrayWithNulls_skipsNulls() {
        String[] words = {"cat", null, "dog", null};
        int count = engine.countSyllables(words);
        assertTrue(count >= 2, "Should skip null elements and count valid words");
    }

    @Test
    @DisplayName("countSyllables array should skip empty elements")
    void countSyllables_arrayWithEmpties_skipsEmpties() {
        String[] words = {"cat", "", "dog", "   "};
        int count = engine.countSyllables(words);
        assertTrue(count >= 2, "Should skip empty/blank elements");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should compute average")
    void calculateAverageSyllablesPerWord_validWords_computesAverage() {
        String[] words = {"cat", "hello", "beautiful"};
        double average = engine.calculateAverageSyllablesPerWord(words);
        assertTrue(average > 0, "Average should be positive");
        assertTrue(average <= 3, "Average should be reasonable for given words");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle null array")
    void calculateAverageSyllablesPerWord_nullArray_returnsZero() {
        double average = engine.calculateAverageSyllablesPerWord(null);
        assertEquals(0.0, average, "Null array should return 0.0");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle empty array")
    void calculateAverageSyllablesPerWord_emptyArray_returnsZero() {
        double average = engine.calculateAverageSyllablesPerWord(new String[0]);
        assertEquals(0.0, average, "Empty array should return 0.0");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should skip null elements")
    void calculateAverageSyllablesPerWord_withNulls_skipsNulls() {
        String[] words = {"cat", null, "dog"};
        double average = engine.calculateAverageSyllablesPerWord(words);
        assertTrue(average > 0, "Should compute average skipping nulls");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should skip empty elements")
    void calculateAverageSyllablesPerWord_withEmpties_skipsEmpties() {
        String[] words = {"cat", "", "dog", "   "};
        double average = engine.calculateAverageSyllablesPerWord(words);
        assertTrue(average > 0, "Should compute average skipping empties");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle all null/empty array")
    void calculateAverageSyllablesPerWord_allInvalid_returnsZero() {
        String[] words = {null, "", "   ", null};
        double average = engine.calculateAverageSyllablesPerWord(words);
        assertEquals(0.0, average, "All invalid elements should return 0.0");
    }

    @Test
    @DisplayName("countSyllables should ensure minimum of 1 syllable for non-empty words")
    void countSyllables_nonEmptyWord_minimumOneSyllable() {
        assertTrue(engine.countSyllables("x") >= 1, "Single consonant should have at least 1 syllable");
        assertTrue(engine.countSyllables("hmm") >= 1, "Word with no vowels should have at least 1 syllable");
    }

    @Test
    @DisplayName("countSyllables should handle complex words")
    void countSyllables_complexWords_countsReasonably() {
        assertTrue(engine.countSyllables("extraordinary") >= 4, "Complex word should have multiple syllables");
        assertTrue(engine.countSyllables("communication") >= 4, "Complex word should have multiple syllables");
        assertTrue(engine.countSyllables("university") >= 3, "Complex word should have multiple syllables");
    }

}
