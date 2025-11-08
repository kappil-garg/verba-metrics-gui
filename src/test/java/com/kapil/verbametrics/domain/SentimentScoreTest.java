package com.kapil.verbametrics.domain;

import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SentimentScore domain record.
 *
 * @author Kapil Garg
 */
class SentimentScoreTest {

    @Test
    @DisplayName("Constructor creates valid sentiment score")
    void constructor_validValues() {
        SentimentScore score = new SentimentScore("positive", 0.85, 0.75);
        assertEquals("positive", score.label());
        assertEquals(0.85, score.confidence(), 0.001);
        assertEquals(0.75, score.score(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts boundary confidence values")
    void constructor_boundaryConfidence() {
        SentimentScore score1 = new SentimentScore("neutral", 0.0, 0.0);
        SentimentScore score2 = new SentimentScore("positive", 1.0, 1.0);
        assertEquals(0.0, score1.confidence(), 0.001);
        assertEquals(1.0, score2.confidence(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts boundary score values")
    void constructor_boundaryScore() {
        SentimentScore score1 = new SentimentScore("negative", 0.8, -1.0);
        SentimentScore score2 = new SentimentScore("positive", 0.8, 1.0);
        assertEquals(-1.0, score1.score(), 0.001);
        assertEquals(1.0, score2.score(), 0.001);
    }

    @Test
    @DisplayName("Constructor rejects null label")
    void constructor_nullLabel() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore(null, 0.85, 0.75)
        );
        assertEquals("Sentiment label cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects blank label")
    void constructor_blankLabel() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("   ", 0.85, 0.75)
        );
        assertEquals("Sentiment label cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects empty label")
    void constructor_emptyLabel() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("", 0.85, 0.75)
        );
        assertEquals("Sentiment label cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects confidence below 0")
    void constructor_confidenceBelowZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("positive", -0.1, 0.75)
        );
        assertEquals("Confidence must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects confidence above 1")
    void constructor_confidenceAboveOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("positive", 1.1, 0.75)
        );
        assertEquals("Confidence must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects score below -1")
    void constructor_scoreBelowMinusOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("negative", 0.85, -1.1)
        );
        assertEquals("Score must be between -1.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects score above 1")
    void constructor_scoreAboveOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new SentimentScore("positive", 0.85, 1.1)
        );
        assertEquals("Score must be between -1.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("getConfidenceLevel returns HIGH for high confidence")
    void getConfidenceLevel_high() {
        SentimentScore score = new SentimentScore("positive", 0.85, 0.75);
        assertEquals("HIGH", score.getConfidenceLevel());
    }

    @Test
    @DisplayName("getConfidenceLevel returns MEDIUM for medium confidence")
    void getConfidenceLevel_medium() {
        SentimentScore score = new SentimentScore("positive", 0.65, 0.75);
        assertEquals("MEDIUM", score.getConfidenceLevel());
    }

    @Test
    @DisplayName("getConfidenceLevel returns LOW for low confidence")
    void getConfidenceLevel_low() {
        SentimentScore score = new SentimentScore("neutral", 0.45, 0.0);
        assertEquals("LOW", score.getConfidenceLevel());
    }

    @Test
    @DisplayName("isPositive returns true for positive sentiment")
    void isPositive_positiveLabel() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.POSITIVE, 0.85, 0.75);
        assertTrue(score.isPositive());
        assertFalse(score.isNegative());
    }

    @Test
    @DisplayName("isPositive returns false for non-positive sentiment")
    void isPositive_nonPositiveLabel() {
        SentimentScore score1 = new SentimentScore(VerbaMetricsConstants.NEGATIVE, 0.85, -0.75);
        SentimentScore score2 = new SentimentScore(VerbaMetricsConstants.NEUTRAL, 0.85, 0.0);
        assertFalse(score1.isPositive());
        assertFalse(score2.isPositive());
    }

    @Test
    @DisplayName("isNegative returns true for negative sentiment")
    void isNegative_negativeLabel() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.NEGATIVE, 0.85, -0.75);
        assertTrue(score.isNegative());
        assertFalse(score.isPositive());
    }

    @Test
    @DisplayName("isNegative returns false for non-negative sentiment")
    void isNegative_nonNegativeLabel() {
        SentimentScore score1 = new SentimentScore(VerbaMetricsConstants.POSITIVE, 0.85, 0.75);
        SentimentScore score2 = new SentimentScore(VerbaMetricsConstants.NEUTRAL, 0.85, 0.0);
        assertFalse(score1.isNegative());
        assertFalse(score2.isNegative());
    }

    @Test
    @DisplayName("toString contains all field values")
    void testToString() {
        SentimentScore score = new SentimentScore("positive", 0.85, 0.75);
        String result = score.toString();
        assertTrue(result.contains("SentimentScore"));
        assertTrue(result.contains("label='positive'"));
        assertTrue(result.contains("confidence=0.850"));
        assertTrue(result.contains("score=0.750"));
        assertTrue(result.contains("confidenceLevel='HIGH'"));
    }

    @Test
    @DisplayName("Record equality works correctly")
    void testEquals() {
        SentimentScore score1 = new SentimentScore("positive", 0.85, 0.75);
        SentimentScore score2 = new SentimentScore("positive", 0.85, 0.75);
        SentimentScore score3 = new SentimentScore("positive", 0.86, 0.75);
        assertEquals(score1, score2);
        assertNotEquals(score1, score3);
    }

    @Test
    @DisplayName("Record hashCode works correctly")
    void testHashCode() {
        SentimentScore score1 = new SentimentScore("positive", 0.85, 0.75);
        SentimentScore score2 = new SentimentScore("positive", 0.85, 0.75);
        assertEquals(score1.hashCode(), score2.hashCode());
    }

    @Test
    @DisplayName("Edge case: neutral sentiment with zero score")
    void edgeCase_neutralSentiment() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.NEUTRAL, 1.0, 0.0);
        assertEquals(VerbaMetricsConstants.NEUTRAL, score.label());
        assertEquals(0.0, score.score(), 0.001);
        assertFalse(score.isPositive());
        assertFalse(score.isNegative());
    }

    @Test
    @DisplayName("Edge case: perfect positive sentiment")
    void edgeCase_perfectPositive() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.POSITIVE, 1.0, 1.0);
        assertTrue(score.isPositive());
        assertEquals(1.0, score.confidence(), 0.001);
        assertEquals(1.0, score.score(), 0.001);
        assertEquals("HIGH", score.getConfidenceLevel());
    }

    @Test
    @DisplayName("Edge case: perfect negative sentiment")
    void edgeCase_perfectNegative() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.NEGATIVE, 1.0, -1.0);
        assertTrue(score.isNegative());
        assertEquals(1.0, score.confidence(), 0.001);
        assertEquals(-1.0, score.score(), 0.001);
        assertEquals("HIGH", score.getConfidenceLevel());
    }

    @Test
    @DisplayName("Edge case: low confidence neutral")
    void edgeCase_lowConfidenceNeutral() {
        SentimentScore score = new SentimentScore(VerbaMetricsConstants.NEUTRAL, 0.0, 0.0);
        assertEquals(0.0, score.confidence(), 0.001);
        assertEquals("LOW", score.getConfidenceLevel());
    }

}
