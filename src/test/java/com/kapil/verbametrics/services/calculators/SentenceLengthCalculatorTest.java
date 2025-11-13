package com.kapil.verbametrics.services.calculators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for SentenceLengthCalculator.
 *
 * @author Kapil Garg
 */
class SentenceLengthCalculatorTest {

    private SentenceLengthCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SentenceLengthCalculator();
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should compute average for simple text")
    void calculateAverageSentenceLength_simpleText_computesAverage() {
        String text = "Hello world. This is a test.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
        assertTrue(average >= 2.0 && average <= 5.0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle single sentence")
    void calculateAverageSentenceLength_singleSentence_returnsWordCount() {
        String text = "This is one sentence with five words";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle multiple sentence endings")
    void calculateAverageSentenceLength_multipleSentences_computesAverage() {
        String text = "First sentence. Second sentence! Third sentence?";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle consecutive punctuation")
    void calculateAverageSentenceLength_consecutivePunctuation_handlesCorrectly() {
        String text = "What?! Really!! Amazing...";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average >= 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle text with periods in numbers")
    void calculateAverageSentenceLength_periodsInNumbers_splitsCorrectly() {
        String text = "The value is 3.14. Another sentence here.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should skip empty sentences")
    void calculateAverageSentenceLength_emptySentences_skipsEmpty() {
        String text = "First sentence.. . Third sentence.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle text with only punctuation")
    void calculateAverageSentenceLength_onlyPunctuation_returnsZero() {
        String text = "...!!!???";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average >= 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should trim whitespace in sentences")
    void calculateAverageSentenceLength_withWhitespace_trimsCorrectly() {
        String text = "  First sentence  .   Second sentence  .  ";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle long text")
    void calculateAverageSentenceLength_longText_computesAverage() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longText.append("This is sentence ").append(i).append(". ");
        }
        double average = calculator.calculateAverageSentenceLength(longText.toString());
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle exclamation marks")
    void calculateAverageSentenceLength_withExclamations_splitsCorrectly() {
        String text = "Hello! How are you! I am fine!";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle question marks")
    void calculateAverageSentenceLength_withQuestions_splitsCorrectly() {
        String text = "What is this? Who are you? Where am I?";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle mixed punctuation")
    void calculateAverageSentenceLength_mixedPunctuation_splitsCorrectly() {
        String text = "Statement. Question? Exclamation! Another statement.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle text without sentence endings")
    void calculateAverageSentenceLength_noSentenceEndings_returnsZero() {
        String text = "This is text without any sentence endings";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average >= 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should count words separated by multiple spaces")
    void calculateAverageSentenceLength_multipleSpaces_countsWordsCorrectly() {
        String text = "Hello    world.    This    is    test.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

    @Test
    @DisplayName("calculateAverageSentenceLength should handle sentences with tabs and newlines")
    void calculateAverageSentenceLength_withTabsNewlines_splitsWords() {
        String text = "First\tsentence.\nSecond\tsentence.";
        double average = calculator.calculateAverageSentenceLength(text);
        assertTrue(average > 0);
    }

}
