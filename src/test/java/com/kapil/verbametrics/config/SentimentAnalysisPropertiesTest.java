package com.kapil.verbametrics.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SentimentAnalysisProperties configuration.
 *
 * @author Kapil Garg
 */
@SpringBootTest
@TestPropertySource(properties = {
        "sentiment.analysis.thresholds.positive=0.1",
        "sentiment.analysis.thresholds.negative=-0.1",
        "sentiment.analysis.confidence-levels.high=0.8",
        "sentiment.analysis.confidence-levels.medium=0.6",
        "sentiment.analysis.text-processing.word-separator=\\\\W+",
        "sentiment.analysis.text-processing.case-sensitive=false",
        "sentiment.analysis.text-processing.remove-punctuation=true",
        "sentiment.analysis.text-processing.normalize-hyphens=true"
})
class SentimentAnalysisPropertiesTest {

    @Autowired
    private SentimentAnalysisProperties properties;

    @Test
    @DisplayName("Properties bean is loaded")
    void propertiesLoaded() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("Sentiment thresholds are loaded correctly")
    void sentimentThresholds() {
        assertNotNull(properties.getThresholds());
        assertEquals(0.1, properties.getThresholds().getPositive(), 0.001);
        assertEquals(-0.1, properties.getThresholds().getNegative(), 0.001);
    }

    @Test
    @DisplayName("Confidence level thresholds are loaded correctly")
    void confidenceLevelThresholds() {
        assertNotNull(properties.getConfidenceLevels());
        assertEquals(0.8, properties.getConfidenceLevels().getHigh(), 0.001);
        assertEquals(0.6, properties.getConfidenceLevels().getMedium(), 0.001);
    }

    @Test
    @DisplayName("Text processing properties are loaded correctly")
    void textProcessingProperties() {
        assertNotNull(properties.getTextProcessing());
        assertEquals("\\W+", properties.getTextProcessing().getWordSeparator());
        assertFalse(properties.getTextProcessing().isCaseSensitive());
        assertTrue(properties.getTextProcessing().isRemovePunctuation());
        assertTrue(properties.getTextProcessing().isNormalizeHyphens());
    }

    @Test
    @DisplayName("Word lists configuration is initialized")
    void wordListsConfiguration() {
        assertNotNull(properties.getWordLists());
        assertNotNull(properties.getWordLists().getPositiveWords());
        assertNotNull(properties.getWordLists().getNegativeWords());
    }

    @Test
    @DisplayName("Positive threshold is greater than negative threshold")
    void thresholdsAreOrdered() {
        assertTrue(properties.getThresholds().getPositive() > properties.getThresholds().getNegative());
    }

    @Test
    @DisplayName("High confidence threshold is greater than medium")
    void confidenceLevelsAreOrdered() {
        assertTrue(properties.getConfidenceLevels().getHigh() > properties.getConfidenceLevels().getMedium());
    }

    @Test
    @DisplayName("Confidence thresholds are within valid range [0,1]")
    void confidenceThresholdsAreValid() {
        assertTrue(properties.getConfidenceLevels().getHigh() >= 0.0);
        assertTrue(properties.getConfidenceLevels().getHigh() <= 1.0);
        assertTrue(properties.getConfidenceLevels().getMedium() >= 0.0);
        assertTrue(properties.getConfidenceLevels().getMedium() <= 1.0);
    }

    @Test
    @DisplayName("Word separator is not null or empty")
    void wordSeparatorIsValid() {
        assertNotNull(properties.getTextProcessing().getWordSeparator());
        assertFalse(properties.getTextProcessing().getWordSeparator().isEmpty());
    }

    @Test
    @DisplayName("Word lists can be set and retrieved")
    void wordListsCanBeModified() {
        List<String> testPositive = List.of("good", "great", "excellent");
        List<String> testNegative = List.of("bad", "terrible", "awful");
        properties.getWordLists().setPositiveWords(testPositive);
        properties.getWordLists().setNegativeWords(testNegative);
        assertEquals(testPositive, properties.getWordLists().getPositiveWords());
        assertEquals(testNegative, properties.getWordLists().getNegativeWords());
    }

    @Test
    @DisplayName("Word list paths can be set and retrieved")
    void wordListPathsCanBeModified() {
        String positivePath = "/path/to/positive.txt";
        String negativePath = "/path/to/negative.txt";
        properties.getWordLists().setPositiveWordsPath(positivePath);
        properties.getWordLists().setNegativeWordsPath(negativePath);
        assertEquals(positivePath, properties.getWordLists().getPositiveWordsPath());
        assertEquals(negativePath, properties.getWordLists().getNegativeWordsPath());
    }

}
