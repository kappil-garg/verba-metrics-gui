package com.kapil.verbametrics.ml.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModelEvaluationResult domain record.
 *
 * @author Kapil Garg
 */
class ModelEvaluationResultTest {

    @Test
    @DisplayName("Constructor creates valid evaluation result")
    void constructor_validValues() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> confusion = Map.of("tp", 85, "fp", 10, "tn", 90, "fn", 15);
        Map<String, Object> metrics = Map.of("mcc", 0.75);
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "CROSS_VALIDATION", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, confusion, metrics, null, now
        );
        assertEquals("model-123", result.modelId());
        assertEquals("CROSS_VALIDATION", result.evaluationType());
        assertTrue(result.success());
        assertEquals(0.85, result.accuracy(), 0.001);
        assertEquals(0.82, result.precision(), 0.001);
        assertEquals(0.88, result.recall(), 0.001);
        assertEquals(0.85, result.f1Score(), 0.001);
        assertEquals(0.90, result.auc(), 0.001);
        assertEquals(3000L, result.evaluationTimeMs());
        assertEquals(500, result.testDataSize());
    }

    @Test
    @DisplayName("Constructor rejects null model ID")
    void constructor_nullModelId() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        null, "TEST", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null evaluation type")
    void constructor_nullEvaluationType() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", null, true, 0.85, 0.82, 0.88, 0.85, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("Evaluation type cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid accuracy")
    void constructor_invalidAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", "TEST", true, -0.1, 0.82, 0.88, 0.85, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("Accuracy must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid precision")
    void constructor_invalidPrecision() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", "TEST", true, 0.85, 1.5, 0.88, 0.85, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("Precision must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid recall")
    void constructor_invalidRecall() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", "TEST", true, 0.85, 0.82, -0.2, 0.85, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("Recall must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid F1 score")
    void constructor_invalidF1Score() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", "TEST", true, 0.85, 0.82, 0.88, 1.1, 0.90,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("F1 score must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid AUC")
    void constructor_invalidAUC() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new ModelEvaluationResult(
                        "model-123", "TEST", true, 0.85, 0.82, 0.88, 0.85, 1.5,
                        3000L, 500, Map.of(), Map.of(), null, now
                )
        );
        assertEquals("AUC must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("getEvaluationScore calculates average of all metrics")
    void getEvaluationScore_calculatesAverage() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.8, 0.7, 0.9, 0.8, 0.85,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        // Average of 0.8, 0.7, 0.9, 0.8, 0.85 = 4.05 / 5 = 0.81
        assertEquals(0.81, result.getEvaluationScore(), 0.001);
    }

    @Test
    @DisplayName("getEvaluationScore handles perfect scores")
    void getEvaluationScore_perfectScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 1.0, 1.0, 1.0, 1.0, 1.0,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertEquals(1.0, result.getEvaluationScore(), 0.001);
    }

    @Test
    @DisplayName("getEvaluationScore handles zero scores")
    void getEvaluationScore_zeroScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", false, 0.0, 0.0, 0.0, 0.0, 0.0,
                3000L, 500, Map.of(), Map.of(), "Evaluation failed", now
        );
        assertEquals(0.0, result.getEvaluationScore(), 0.001);
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for evaluation score")
    void getPerformanceLevel_basedOnScore() {
        LocalDateTime now = LocalDateTime.now();
        // Evaluation score = (0.95 + 0.92 + 0.94 + 0.93 + 0.96) / 5 = 0.94 -> EXCELLENT
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.95, 0.92, 0.94, 0.93, 0.96,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertEquals("EXCELLENT", result.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns GOOD for good evaluation")
    void getPerformanceLevel_good() {
        LocalDateTime now = LocalDateTime.now();
        // Evaluation score = (0.85 + 0.82 + 0.84 + 0.83 + 0.86) / 5 = 0.84 -> GOOD
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.84, 0.83, 0.86,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertEquals("GOOD", result.getPerformanceLevel());
    }

    @Test
    @DisplayName("Constructor accepts failed evaluation with error message")
    void constructor_failedEvaluation() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", false, 0.0, 0.0, 0.0, 0.0, 0.0,
                3000L, 500, Map.of(), Map.of(), "Model not found", now
        );
        assertFalse(result.success());
        assertEquals("Model not found", result.errorMessage());
    }

    @Test
    @DisplayName("Constructor accepts null optional fields")
    void constructor_nullOptionalFields() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, null, null, null, now
        );
        assertNull(result.confusionMatrix());
        assertNull(result.additionalMetrics());
        assertNull(result.errorMessage());
    }

    @Test
    @DisplayName("Constructor accepts confusion matrix")
    void constructor_confusionMatrix() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> confusion = new HashMap<>();
        confusion.put("true_positive", 85);
        confusion.put("false_positive", 10);
        confusion.put("true_negative", 90);
        confusion.put("false_negative", 15);
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, confusion, Map.of(), null, now
        );
        assertEquals(confusion, result.confusionMatrix());
        assertEquals(85, result.confusionMatrix().get("true_positive"));
    }

    @Test
    @DisplayName("Constructor accepts additional metrics")
    void constructor_additionalMetrics() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("mcc", 0.75);
        metrics.put("specificity", 0.88);
        metrics.put("sensitivity", 0.85);
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), metrics, null, now
        );
        assertEquals(metrics, result.additionalMetrics());
        assertEquals(0.75, result.additionalMetrics().get("mcc"));
    }

    @Test
    @DisplayName("toString includes key evaluation information")
    void toStringIncludesKeyInfo() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "CROSS_VALIDATION", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        String output = result.toString();
        assertTrue(output.contains("model-123"));
        assertTrue(output.contains("CROSS_VALIDATION"));
        assertTrue(output.contains("true"));
        assertTrue(output.contains("0.850"));
    }

    @Test
    @DisplayName("Constructor accepts boundary values for all scores")
    void constructor_boundaryScores() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult min = new ModelEvaluationResult(
                "model-123", "TEST", false, 0.0, 0.0, 0.0, 0.0, 0.0,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        ModelEvaluationResult max = new ModelEvaluationResult(
                "model-456", "TEST", true, 1.0, 1.0, 1.0, 1.0, 1.0,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertEquals(0.0, min.accuracy(), 0.001);
        assertEquals(1.0, max.auc(), 0.001);
    }

    @Test
    @DisplayName("Constructor accepts different evaluation types")
    void constructor_differentEvaluationTypes() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult test = new ModelEvaluationResult(
                "model-123", "TEST_SET", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        ModelEvaluationResult cv = new ModelEvaluationResult(
                "model-456", "CROSS_VALIDATION", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertEquals("TEST_SET", test.evaluationType());
        assertEquals("CROSS_VALIDATION", cv.evaluationType());
    }

    @Test
    @DisplayName("isProductionReady returns true for high quality model")
    void isProductionReady_highQuality() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertTrue(result.isProductionReady());
    }

    @Test
    @DisplayName("isProductionReady returns false for failed evaluation")
    void isProductionReady_failed() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", false, 0.85, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), "Error", now
        );
        assertFalse(result.isProductionReady());
    }

    @Test
    @DisplayName("isProductionReady returns false for low accuracy")
    void isProductionReady_lowAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.75, 0.82, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertFalse(result.isProductionReady());
    }

    @Test
    @DisplayName("isProductionReady returns false for low precision")
    void isProductionReady_lowPrecision() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.65, 0.88, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertFalse(result.isProductionReady());
    }

    @Test
    @DisplayName("isProductionReady returns false for low recall")
    void isProductionReady_lowRecall() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.85, 0.82, 0.65, 0.85, 0.90,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertFalse(result.isProductionReady());
    }

    @Test
    @DisplayName("isProductionReady returns true at boundary thresholds")
    void isProductionReady_boundaryThresholds() {
        LocalDateTime now = LocalDateTime.now();
        ModelEvaluationResult result = new ModelEvaluationResult(
                "model-123", "TEST", true, 0.8, 0.7, 0.7, 0.74, 0.80,
                3000L, 500, Map.of(), Map.of(), null, now
        );
        assertTrue(result.isProductionReady());
    }

}
