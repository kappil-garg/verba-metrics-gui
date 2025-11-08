package com.kapil.verbametrics.ml.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModelTrainingResult domain record.
 *
 * @author Kapil Garg
 */
class ModelTrainingResultTest {

    @Test
    @DisplayName("Constructor creates valid training result")
    void constructor_validValues() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metrics = Map.of("loss", 0.15);
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.85, 0.82, 0.88, 0.85,
                5000L, 1000, 200, metrics, null, now
        );
        assertEquals("model-123", result.modelId());
        assertEquals("SENTIMENT", result.modelType());
        assertTrue(result.success());
        assertEquals(0.85, result.accuracy(), 0.001);
        assertEquals(0.82, result.precision(), 0.001);
        assertEquals(0.88, result.recall(), 0.001);
        assertEquals(0.85, result.f1Score(), 0.001);
        assertEquals(5000L, result.trainingTimeMs());
        assertEquals(1000, result.trainingDataSize());
        assertEquals(200, result.testDataSize());
    }

    @Test
    @DisplayName("Constructor rejects null model ID")
    void constructor_nullModelId() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        null, "SENTIMENT", true, 0.85, 0.82, 0.88, 0.85,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null model type")
    void constructor_nullModelType() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        "model-123", null, true, 0.85, 0.82, 0.88, 0.85,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("Model type cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid accuracy")
    void constructor_invalidAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        "model-123", "SENTIMENT", true, 1.5, 0.82, 0.88, 0.85,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("Accuracy must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid precision")
    void constructor_invalidPrecision() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        "model-123", "SENTIMENT", true, 0.85, -0.1, 0.88, 0.85,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("Precision must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid recall")
    void constructor_invalidRecall() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        "model-123", "SENTIMENT", true, 0.85, 0.82, 1.2, 0.85,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("Recall must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid F1 score")
    void constructor_invalidF1Score() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelTrainingResult(
                        "model-123", "SENTIMENT", true, 0.85, 0.82, 0.88, -0.5,
                        5000L, 1000, 200, Map.of(), null, now
                )
        );
        assertEquals("F1 score must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("getQualityScore calculates average of metrics")
    void getQualityScore_calculatesAverage() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.8, 0.7, 0.9, 0.8,
                5000L, 1000, 200, Map.of(), null, now
        );
        // Average of 0.8, 0.7, 0.9, 0.8 = 3.2 / 4 = 0.8
        assertEquals(0.8, result.getQualityScore(), 0.001);
    }

    @Test
    @DisplayName("getQualityScore handles perfect scores")
    void getQualityScore_perfectScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 1.0, 1.0, 1.0, 1.0,
                5000L, 1000, 200, Map.of(), null, now
        );
        assertEquals(1.0, result.getQualityScore(), 0.001);
    }

    @Test
    @DisplayName("getQualityScore handles zero scores")
    void getQualityScore_zeroScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", false, 0.0, 0.0, 0.0, 0.0,
                5000L, 1000, 200, Map.of(), "Training failed", now
        );
        assertEquals(0.0, result.getQualityScore(), 0.001);
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for quality score")
    void getPerformanceLevel_basedOnQuality() {
        LocalDateTime now = LocalDateTime.now();
        // Quality score = (0.95 + 0.92 + 0.94 + 0.93) / 4 = 0.935 -> EXCELLENT
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.95, 0.92, 0.94, 0.93,
                5000L, 1000, 200, Map.of(), null, now
        );
        assertEquals("EXCELLENT", result.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns GOOD for good quality")
    void getPerformanceLevel_good() {
        LocalDateTime now = LocalDateTime.now();
        // Quality score = (0.85 + 0.82 + 0.84 + 0.83) / 4 = 0.835 -> GOOD
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.85, 0.82, 0.84, 0.83,
                5000L, 1000, 200, Map.of(), null, now
        );
        assertEquals("GOOD", result.getPerformanceLevel());
    }

    @Test
    @DisplayName("Constructor accepts failed training with error message")
    void constructor_failedTraining() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", false, 0.0, 0.0, 0.0, 0.0,
                5000L, 1000, 200, Map.of(), "Insufficient data", now
        );
        assertFalse(result.success());
        assertEquals("Insufficient data", result.errorMessage());
    }

    @Test
    @DisplayName("Constructor accepts null optional fields")
    void constructor_nullOptionalFields() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.85, 0.82, 0.88, 0.85,
                5000L, 1000, 200, null, null, now
        );
        assertNull(result.additionalMetrics());
        assertNull(result.errorMessage());
    }

    @Test
    @DisplayName("Constructor accepts zero time and data sizes")
    void constructor_zeroValues() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", false, 0.0, 0.0, 0.0, 0.0,
                0L, 0, 0, Map.of(), "No data", now
        );
        assertEquals(0L, result.trainingTimeMs());
        assertEquals(0, result.trainingDataSize());
        assertEquals(0, result.testDataSize());
    }

    @Test
    @DisplayName("toString includes key training information")
    void toStringIncludesKeyInfo() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.85, 0.82, 0.88, 0.85,
                5000L, 1000, 200, Map.of(), null, now
        );
        String output = result.toString();
        assertTrue(output.contains("model-123"));
        assertTrue(output.contains("SENTIMENT"));
        assertTrue(output.contains("true"));
        assertTrue(output.contains("0.850"));
        assertTrue(output.contains("5000"));
    }

    @Test
    @DisplayName("Constructor accepts boundary values for scores")
    void constructor_boundaryScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelTrainingResult min = new ModelTrainingResult(
                "model-123", "SENTIMENT", false, 0.0, 0.0, 0.0, 0.0,
                5000L, 1000, 200, Map.of(), null, now
        );
        ModelTrainingResult max = new ModelTrainingResult(
                "model-456", "SENTIMENT", true, 1.0, 1.0, 1.0, 1.0,
                5000L, 1000, 200, Map.of(), null, now
        );
        assertEquals(0.0, min.accuracy(), 0.001);
        assertEquals(1.0, max.accuracy(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts additional metrics map")
    void constructor_additionalMetrics() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("loss", 0.15);
        metrics.put("learning_rate", 0.01);
        metrics.put("epochs", 10);
        ModelTrainingResult result = new ModelTrainingResult(
                "model-123", "SENTIMENT", true, 0.85, 0.82, 0.88, 0.85,
                5000L, 1000, 200, metrics, null, now
        );
        assertEquals(metrics, result.additionalMetrics());
        assertEquals(0.15, result.additionalMetrics().get("loss"));
    }

}
