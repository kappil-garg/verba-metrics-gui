package com.kapil.verbametrics.ml.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BaseMLResult validation and utility methods.
 *
 * @author Kapil Garg
 */
class BaseMLResultTest {

    @Test
    @DisplayName("validateModelId accepts valid non-blank model ID")
    void validateModelId_validId() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateModelId("model-123")
        );
    }

    @Test
    @DisplayName("validateModelId rejects null")
    void validateModelId_nullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateModelId(null)
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateModelId rejects blank string")
    void validateModelId_blankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateModelId("   ")
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateModelId rejects empty string")
    void validateModelId_emptyValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateModelId("")
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateModelType accepts valid non-blank model type")
    void validateModelType_validType() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateModelType("SENTIMENT")
        );
    }

    @Test
    @DisplayName("validateModelType rejects null")
    void validateModelType_nullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateModelType(null)
        );
        assertEquals("Model type cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateModelType rejects blank string")
    void validateModelType_blankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateModelType("   ")
        );
        assertEquals("Model type cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateScore accepts valid score within range")
    void validateScore_validScore() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateScore(0.85, "Test score")
        );
    }

    @Test
    @DisplayName("validateScore accepts minimum boundary (0.0)")
    void validateScore_minBoundary() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateScore(0.0, "Test score")
        );
    }

    @Test
    @DisplayName("validateScore accepts maximum boundary (1.0)")
    void validateScore_maxBoundary() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateScore(1.0, "Test score")
        );
    }

    @Test
    @DisplayName("validateScore rejects score below minimum")
    void validateScore_belowMin() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateScore(-0.1, "Test score")
        );
        assertEquals("Test score must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("validateScore rejects score above maximum")
    void validateScore_aboveMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateScore(1.1, "Test score")
        );
        assertEquals("Test score must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("validateStringField accepts valid non-blank string")
    void validateStringField_validString() {
        assertDoesNotThrow(() ->
                BaseMLResult.validateStringField("valid", "Test field")
        );
    }

    @Test
    @DisplayName("validateStringField rejects null")
    void validateStringField_nullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateStringField(null, "Test field")
        );
        assertEquals("Test field cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateStringField rejects blank string")
    void validateStringField_blankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseMLResult.validateStringField("   ", "Test field")
        );
        assertEquals("Test field cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("getPerformanceLevel returns EXCELLENT for score >= 0.9")
    void getPerformanceLevel_excellent() {
        TestMLResult result = new TestMLResult();
        assertEquals("EXCELLENT", result.getPerformanceLevel(0.95));
        assertEquals("EXCELLENT", result.getPerformanceLevel(0.9));
    }

    @Test
    @DisplayName("getPerformanceLevel returns GOOD for score >= 0.8")
    void getPerformanceLevel_good() {
        TestMLResult result = new TestMLResult();
        assertEquals("GOOD", result.getPerformanceLevel(0.85));
        assertEquals("GOOD", result.getPerformanceLevel(0.8));
    }

    @Test
    @DisplayName("getPerformanceLevel returns FAIR for score >= 0.7")
    void getPerformanceLevel_fair() {
        TestMLResult result = new TestMLResult();
        assertEquals("FAIR", result.getPerformanceLevel(0.75));
        assertEquals("FAIR", result.getPerformanceLevel(0.7));
    }

    @Test
    @DisplayName("getPerformanceLevel returns POOR for score >= 0.6")
    void getPerformanceLevel_poor() {
        TestMLResult result = new TestMLResult();
        assertEquals("POOR", result.getPerformanceLevel(0.65));
        assertEquals("POOR", result.getPerformanceLevel(0.6));
    }

    @Test
    @DisplayName("getPerformanceLevel returns VERY_POOR for score < 0.6")
    void getPerformanceLevel_veryPoor() {
        TestMLResult result = new TestMLResult();
        assertEquals("VERY_POOR", result.getPerformanceLevel(0.55));
        assertEquals("VERY_POOR", result.getPerformanceLevel(0.0));
    }

    @Test
    @DisplayName("calculateAverageScore returns correct average")
    void calculateAverageScore_validScores() {
        TestMLResult result = new TestMLResult();
        assertEquals(0.8, result.calculateAverageScore(0.7, 0.8, 0.9), 0.001);
    }

    @Test
    @DisplayName("calculateAverageScore handles single score")
    void calculateAverageScore_singleScore() {
        TestMLResult result = new TestMLResult();
        assertEquals(0.85, result.calculateAverageScore(0.85), 0.001);
    }

    @Test
    @DisplayName("calculateAverageScore returns zero for empty array")
    void calculateAverageScore_emptyArray() {
        TestMLResult result = new TestMLResult();
        assertEquals(0.0, result.calculateAverageScore(), 0.001);
    }

    @Test
    @DisplayName("calculateAverageScore handles perfect scores")
    void calculateAverageScore_perfectScores() {
        TestMLResult result = new TestMLResult();
        assertEquals(1.0, result.calculateAverageScore(1.0, 1.0, 1.0), 0.001);
    }

    @Test
    @DisplayName("calculateAverageScore handles zero scores")
    void calculateAverageScore_zeroScores() {
        TestMLResult result = new TestMLResult();
        assertEquals(0.0, result.calculateAverageScore(0.0, 0.0, 0.0), 0.001);
    }

    @Test
    @DisplayName("calculateAverageScore handles mixed scores")
    void calculateAverageScore_mixedScores() {
        TestMLResult result = new TestMLResult();
        assertEquals(0.5, result.calculateAverageScore(0.0, 0.5, 1.0), 0.001);
    }

    /**
     * Concrete implementation of BaseMLResult for testing protected instance methods.
     */
    private static class TestMLResult extends BaseMLResult {
        // Empty implementation to test non-static protected methods
    }

}
