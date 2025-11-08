package com.kapil.verbametrics.ml.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MLModel domain record.
 *
 * @author Kapil Garg
 */
class MLModelTest {

    @Test
    @DisplayName("Constructor creates valid ML model")
    void constructor_validValues() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> params = Map.of("learning_rate", 0.01);
        Map<String, Object> metrics = Map.of("f1_score", 0.85);
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Sentiment Analyzer", "Test model",
                "1.0.0", now, now, params, metrics, "/path/to/model",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertEquals("model-123", model.modelId());
        assertEquals("SENTIMENT", model.modelType());
        assertEquals("Sentiment Analyzer", model.name());
        assertEquals("1.0.0", model.version());
        assertEquals(0.85, model.accuracy(), 0.001);
        assertEquals("TRAINED", model.status());
        assertTrue(model.isActive());
    }

    @Test
    @DisplayName("Constructor rejects null model ID")
    void constructor_nullModelId() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        null, "SENTIMENT", "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, "TRAINED"
                )
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects blank model ID")
    void constructor_blankModelId() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "   ", "SENTIMENT", "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, "TRAINED"
                )
        );
        assertEquals("Model ID cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null model type")
    void constructor_nullModelType() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", null, "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, "TRAINED"
                )
        );
        assertEquals("Model type cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null name")
    void constructor_nullName() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", "SENTIMENT", null, "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, "TRAINED"
                )
        );
        assertEquals("Model name cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null version")
    void constructor_nullVersion() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", "SENTIMENT", "Test", "Description",
                        null, now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, "TRAINED"
                )
        );
        assertEquals("Model version cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid accuracy (< 0.0)")
    void constructor_negativeAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", "SENTIMENT", "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, -0.1, "TRAINED"
                )
        );
        assertEquals("Accuracy must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects invalid accuracy (> 1.0)")
    void constructor_excessiveAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", "SENTIMENT", "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 1.1, "TRAINED"
                )
        );
        assertEquals("Accuracy must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects null status")
    void constructor_nullStatus() {
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MLModel(
                        "model-123", "SENTIMENT", "Test", "Description",
                        "1.0", now, now, Map.of(), Map.of(), "/path",
                        true, "admin", 1000, 0.85, null
                )
        );
        assertEquals("Model status cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("isReadyForUse returns true for active trained model")
    void isReadyForUse_activeTrained() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertTrue(model.isReadyForUse());
    }

    @Test
    @DisplayName("isReadyForUse returns false for inactive trained model")
    void isReadyForUse_inactiveTrained() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                false, "admin", 1000, 0.85, "TRAINED"
        );
        assertFalse(model.isReadyForUse());
    }

    @Test
    @DisplayName("isReadyForUse returns false for active untrained model")
    void isReadyForUse_activeUntrained() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.85, "TRAINING"
        );
        assertFalse(model.isReadyForUse());
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for high accuracy")
    void getPerformanceLevel_highAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.95, "TRAINED"
        );
        assertEquals("EXCELLENT", model.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for good accuracy")
    void getPerformanceLevel_goodAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertEquals("GOOD", model.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for fair accuracy")
    void getPerformanceLevel_fairAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.75, "TRAINED"
        );
        assertEquals("FAIR", model.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for poor accuracy")
    void getPerformanceLevel_poorAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.65, "TRAINED"
        );
        assertEquals("POOR", model.getPerformanceLevel());
    }

    @Test
    @DisplayName("getPerformanceLevel returns correct level for very poor accuracy")
    void getPerformanceLevel_veryPoorAccuracy() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.55, "TRAINED"
        );
        assertEquals("VERY_POOR", model.getPerformanceLevel());
    }

    @Test
    @DisplayName("Constructor accepts null optional fields")
    void constructor_nullOptionalFields() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, null, null, null, "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertNull(model.lastUsed());
        assertNull(model.parameters());
        assertNull(model.performanceMetrics());
    }

    @Test
    @DisplayName("toString includes key model information")
    void toStringIncludesKeyInfo() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test Model", "Description",
                "1.0.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        String result = model.toString();
        assertTrue(result.contains("model-123"));
        assertTrue(result.contains("SENTIMENT"));
        assertTrue(result.contains("Test Model"));
        assertTrue(result.contains("1.0.0"));
        assertTrue(result.contains("0.850"));
        assertTrue(result.contains("TRAINED"));
        assertTrue(result.contains("GOOD"));
    }

    @Test
    @DisplayName("Constructor accepts empty maps for parameters and metrics")
    void constructor_emptyMaps() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, Map.of(), Map.of(), "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertNotNull(model.parameters());
        assertNotNull(model.performanceMetrics());
        assertTrue(model.parameters().isEmpty());
        assertTrue(model.performanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("Constructor accepts mutable maps")
    void constructor_mutableMaps() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> params = new HashMap<>();
        params.put("learning_rate", 0.01);
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("f1_score", 0.85);
        MLModel model = new MLModel(
                "model-123", "SENTIMENT", "Test", "Description",
                "1.0", now, now, params, metrics, "/path",
                true, "admin", 1000, 0.85, "TRAINED"
        );
        assertEquals(params, model.parameters());
        assertEquals(metrics, model.performanceMetrics());
    }

}
