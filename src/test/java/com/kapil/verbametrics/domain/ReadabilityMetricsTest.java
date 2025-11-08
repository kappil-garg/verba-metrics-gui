package com.kapil.verbametrics.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ReadabilityMetrics domain record.
 *
 * @author Kapil Garg
 */
class ReadabilityMetricsTest {

    @Test
    @DisplayName("Constructor creates valid metrics")
    void constructor_validValues() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        assertEquals(8.5, metrics.fleschKincaidScore(), 0.001);
        assertEquals(65.0, metrics.fleschReadingEase(), 0.001);
        assertEquals("Grade 8", metrics.readingLevel());
        assertEquals("Moderate", metrics.complexity());
        assertEquals(15.0, metrics.averageSentenceLength(), 0.001);
        assertEquals(1.5, metrics.averageSyllablesPerWord(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts zero Flesch-Kincaid score")
    void constructor_zeroFleschKincaid() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                0.0, 100.0, "Elementary", "Very Easy", 5.0, 1.0
        );
        assertEquals(0.0, metrics.fleschKincaidScore(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts boundary Flesch Reading Ease values")
    void constructor_boundaryFleschReadingEase() {
        ReadabilityMetrics metrics1 = new ReadabilityMetrics(
                0.0, 0.0, "College Graduate", "Very Difficult", 30.0, 2.5
        );
        ReadabilityMetrics metrics2 = new ReadabilityMetrics(
                0.0, 100.0, "Elementary", "Very Easy", 5.0, 1.0
        );
        assertEquals(0.0, metrics1.fleschReadingEase(), 0.001);
        assertEquals(100.0, metrics2.fleschReadingEase(), 0.001);
    }

    @Test
    @DisplayName("Constructor rejects negative Flesch-Kincaid score")
    void constructor_negativeFleschKincaid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ReadabilityMetrics(-1.0, 65.0, "Grade 8", "Moderate", 15.0, 1.5)
        );
        assertEquals("Flesch-Kincaid score cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects Flesch Reading Ease below 0")
    void constructor_fleschReadingEaseBelowZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ReadabilityMetrics(8.5, -0.1, "Grade 8", "Moderate", 15.0, 1.5)
        );
        assertEquals("Flesch Reading Ease must be between 0.0 and 100.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects Flesch Reading Ease above 100")
    void constructor_fleschReadingEaseAbove100() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ReadabilityMetrics(8.5, 100.1, "Grade 8", "Moderate", 15.0, 1.5)
        );
        assertEquals("Flesch Reading Ease must be between 0.0 and 100.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative average sentence length")
    void constructor_negativeAverageSentenceLength() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ReadabilityMetrics(8.5, 65.0, "Grade 8", "Moderate", -1.0, 1.5)
        );
        assertEquals("Average sentence length cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects negative average syllables per word")
    void constructor_negativeAverageSyllables() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ReadabilityMetrics(8.5, 65.0, "Grade 8", "Moderate", 15.0, -0.1)
        );
        assertEquals("Average syllables per word cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("toString contains all field values")
    void testToString() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        String result = metrics.toString();
        assertTrue(result.contains("ReadabilityMetrics"));
        assertTrue(result.contains("fleschKincaidScore=8.50"));
        assertTrue(result.contains("fleschReadingEase=65.00"));
        assertTrue(result.contains("readingLevel='Grade 8'"));
        assertTrue(result.contains("complexity='Moderate'"));
        assertTrue(result.contains("avgSentenceLength=15.00"));
        assertTrue(result.contains("avgSyllablesPerWord=1.50"));
    }

    @Test
    @DisplayName("Record equality works correctly")
    void testEquals() {
        ReadabilityMetrics metrics1 = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        ReadabilityMetrics metrics2 = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        ReadabilityMetrics metrics3 = new ReadabilityMetrics(
                8.5, 66.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        assertEquals(metrics1, metrics2);
        assertNotEquals(metrics1, metrics3);
    }

    @Test
    @DisplayName("Record hashCode works correctly")
    void testHashCode() {
        ReadabilityMetrics metrics1 = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        ReadabilityMetrics metrics2 = new ReadabilityMetrics(
                8.5, 65.0, "Grade 8", "Moderate", 15.0, 1.5
        );
        assertEquals(metrics1.hashCode(), metrics2.hashCode());
    }

    @Test
    @DisplayName("Edge case: very easy text")
    void edgeCase_veryEasyText() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                0.0, 100.0, "Elementary", "Very Easy", 5.0, 1.0
        );
        assertEquals(100.0, metrics.fleschReadingEase(), 0.001);
        assertEquals("Very Easy", metrics.complexity());
    }

    @Test
    @DisplayName("Edge case: very difficult text")
    void edgeCase_veryDifficultText() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                18.0, 0.0, "College Graduate", "Very Difficult", 30.0, 2.5
        );
        assertEquals(0.0, metrics.fleschReadingEase(), 0.001);
        assertEquals("Very Difficult", metrics.complexity());
    }

    @Test
    @DisplayName("Edge case: high Flesch-Kincaid score")
    void edgeCase_highFleschKincaid() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                25.0, 10.0, "Professional", "Very Difficult", 35.0, 3.0
        );
        assertEquals(25.0, metrics.fleschKincaidScore(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts zero average values")
    void constructor_zeroAverages() {
        ReadabilityMetrics metrics = new ReadabilityMetrics(
                0.0, 100.0, "Elementary", "Very Easy", 0.0, 0.0
        );
        assertEquals(0.0, metrics.averageSentenceLength(), 0.001);
        assertEquals(0.0, metrics.averageSyllablesPerWord(), 0.001);
    }

}
