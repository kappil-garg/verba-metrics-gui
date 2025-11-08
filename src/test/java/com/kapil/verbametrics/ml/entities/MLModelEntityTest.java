package com.kapil.verbametrics.ml.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MLModelEntity JPA entity.
 *
 * @author Kapil Garg
 */
class MLModelEntityTest {

    /**
     * Helper method to create MLModelEntity with all fields set.
     *
     * @param now     Timestamp for createdAt and lastUsed
     * @param params  Model parameters
     * @param metrics Performance metrics
     * @return MLModelEntity instance
     */
    private static MLModelEntity getMlModelEntity(LocalDateTime now, Map<String, String> params, Map<String, String> metrics) {
        MLModelEntity entity = new MLModelEntity();
        entity.setModelId("model-123");
        entity.setModelType("SENTIMENT");
        entity.setName("Sentiment Analyzer");
        entity.setDescription("Test model");
        entity.setVersion("1.0.0");
        entity.setCreatedAt(now);
        entity.setLastUsed(now);
        entity.setModelPath("/path/to/model");
        entity.setIsActive(true);
        entity.setCreatedBy("admin");
        entity.setTrainingDataSize(1000);
        entity.setAccuracy(0.85);
        entity.setStatus("TRAINED");
        entity.setParameters(params);
        entity.setPerformanceMetrics(metrics);
        return entity;
    }

    @Test
    @DisplayName("Entity creation with all fields")
    void entityCreation_allFields() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> params = Map.of("learning_rate", "0.01");
        Map<String, String> metrics = Map.of("f1_score", "0.85");
        MLModelEntity entity = getMlModelEntity(now, params, metrics);
        assertEquals("model-123", entity.getModelId());
        assertEquals("SENTIMENT", entity.getModelType());
        assertEquals("Sentiment Analyzer", entity.getName());
        assertEquals("Test model", entity.getDescription());
        assertEquals("1.0.0", entity.getVersion());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getLastUsed());
        assertEquals("/path/to/model", entity.getModelPath());
        assertTrue(entity.getIsActive());
        assertEquals("admin", entity.getCreatedBy());
        assertEquals(1000, entity.getTrainingDataSize());
        assertEquals(0.85, entity.getAccuracy(), 0.001);
        assertEquals("TRAINED", entity.getStatus());
        assertEquals(params, entity.getParameters());
        assertEquals(metrics, entity.getPerformanceMetrics());
    }

    @Test
    @DisplayName("Builder creates entity correctly")
    void builder_createsEntity() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> params = Map.of("learning_rate", "0.01");
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-456")
                .modelType("CLASSIFICATION")
                .name("Test Model")
                .description("Description")
                .version("2.0")
                .createdAt(now)
                .lastUsed(now)
                .modelPath("/models/test")
                .isActive(true)
                .createdBy("user1")
                .trainingDataSize(500)
                .accuracy(0.92)
                .status("TRAINED")
                .parameters(params)
                .build();
        assertEquals("model-456", entity.getModelId());
        assertEquals("CLASSIFICATION", entity.getModelType());
        assertEquals("Test Model", entity.getName());
        assertEquals(0.92, entity.getAccuracy(), 0.001);
    }

    @Test
    @DisplayName("NoArgsConstructor creates empty entity")
    void noArgsConstructor_createsEmptyEntity() {
        MLModelEntity entity = new MLModelEntity();
        assertNull(entity.getModelId());
        assertNull(entity.getModelType());
        assertNull(entity.getName());
    }

    @Test
    @DisplayName("AllArgsConstructor creates entity with all fields")
    void allArgsConstructor_createsEntity() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> params = Map.of("key", "value");
        Map<String, String> metrics = Map.of("metric", "value");
        MLModelEntity entity = new MLModelEntity(
                "model-789", "SENTIMENT", "Test", "Desc", "1.0",
                now, now, "/path", true, "admin", 100, 0.75, "TRAINED",
                params, metrics
        );
        assertEquals("model-789", entity.getModelId());
        assertEquals("SENTIMENT", entity.getModelType());
        assertEquals(0.75, entity.getAccuracy(), 0.001);
    }

    @Test
    @DisplayName("Equals and hashCode work correctly for same data")
    void equalsAndHashCode_sameData() {
        LocalDateTime now = LocalDateTime.now();
        MLModelEntity entity1 = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test")
                .createdAt(now)
                .build();
        MLModelEntity entity2 = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test")
                .createdAt(now)
                .build();
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    @DisplayName("Equals returns false for different data")
    void equals_differentData() {
        LocalDateTime now = LocalDateTime.now();
        MLModelEntity entity1 = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test1")
                .createdAt(now)
                .build();
        MLModelEntity entity2 = MLModelEntity.builder()
                .modelId("model-456")
                .modelType("SENTIMENT")
                .name("Test2")
                .createdAt(now)
                .build();
        assertNotEquals(entity1, entity2);
    }

    @Test
    @DisplayName("ToString includes key fields")
    void toStringIncludesKeyFields() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test Model")
                .version("1.0")
                .build();
        String result = entity.toString();
        assertTrue(result.contains("model-123"));
        assertTrue(result.contains("SENTIMENT"));
        assertTrue(result.contains("Test Model"));
    }

    @Test
    @DisplayName("PrePersist sets default values")
    void prePersist_setsDefaults() {
        MLModelEntity entity = new MLModelEntity();
        entity.setModelId("model-test");
        entity.setModelType("TEST");
        entity.setName("Test");
        entity.onCreate(); // Simulate @PrePersist
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getLastUsed());
        assertEquals("1.0", entity.getVersion());
        assertTrue(entity.getIsActive());
    }

    @Test
    @DisplayName("PrePersist does not override existing values")
    void prePersist_doesNotOverride() {
        LocalDateTime customTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        MLModelEntity entity = new MLModelEntity();
        entity.setModelId("model-test");
        entity.setModelType("TEST");
        entity.setName("Test");
        entity.setCreatedAt(customTime);
        entity.setLastUsed(customTime);
        entity.setVersion("2.0");
        entity.setIsActive(false);
        entity.onCreate(); // Simulate @PrePersist
        assertEquals(customTime, entity.getCreatedAt());
        assertEquals(customTime, entity.getLastUsed());
        assertEquals("2.0", entity.getVersion());
        assertFalse(entity.getIsActive());
    }

    @Test
    @DisplayName("PreUpdate updates lastUsed timestamp")
    void preUpdate_updatesLastUsed() throws InterruptedException {
        LocalDateTime original = LocalDateTime.of(2023, 1, 1, 0, 0);
        MLModelEntity entity = new MLModelEntity();
        entity.setModelId("model-test");
        entity.setLastUsed(original);
        Thread.sleep(10); // Ensure time difference
        entity.onUpdate();
        assertNotNull(entity.getLastUsed());
        assertTrue(entity.getLastUsed().isAfter(original));
    }

    @Test
    @DisplayName("Entity accepts null optional fields")
    void entity_nullOptionalFields() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test")
                .createdAt(LocalDateTime.now())
                .description(null)
                .version(null)
                .lastUsed(null)
                .modelPath(null)
                .createdBy(null)
                .trainingDataSize(null)
                .accuracy(null)
                .status(null)
                .parameters(null)
                .performanceMetrics(null)
                .build();
        assertNull(entity.getDescription());
        assertNull(entity.getVersion());
        assertNull(entity.getLastUsed());
        assertNull(entity.getParameters());
    }

    @Test
    @DisplayName("Entity accepts empty maps")
    void entity_emptyMaps() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test")
                .createdAt(LocalDateTime.now())
                .parameters(Map.of())
                .performanceMetrics(Map.of())
                .build();
        assertNotNull(entity.getParameters());
        assertNotNull(entity.getPerformanceMetrics());
        assertTrue(entity.getParameters().isEmpty());
        assertTrue(entity.getPerformanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("Entity accepts mutable maps")
    void entity_mutableMaps() {
        Map<String, String> params = new HashMap<>();
        params.put("lr", "0.01");
        params.put("epochs", "10");
        Map<String, String> metrics = new HashMap<>();
        metrics.put("accuracy", "0.85");
        metrics.put("loss", "0.15");
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Test")
                .createdAt(LocalDateTime.now())
                .parameters(params)
                .performanceMetrics(metrics)
                .build();
        assertEquals(2, entity.getParameters().size());
        assertEquals(2, entity.getPerformanceMetrics().size());
        assertEquals("0.01", entity.getParameters().get("lr"));
        assertEquals("0.85", entity.getPerformanceMetrics().get("accuracy"));
    }

    @Test
    @DisplayName("Entity builder with partial fields")
    void builder_partialFields() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("SENTIMENT")
                .name("Minimal Model")
                .build();
        assertEquals("model-123", entity.getModelId());
        assertEquals("SENTIMENT", entity.getModelType());
        assertEquals("Minimal Model", entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getAccuracy());
    }

    @Test
    @DisplayName("Entity accepts boundary accuracy values")
    void entity_boundaryAccuracy() {
        MLModelEntity minEntity = MLModelEntity.builder()
                .modelId("model-min")
                .modelType("TEST")
                .name("Min")
                .accuracy(0.0)
                .build();
        MLModelEntity maxEntity = MLModelEntity.builder()
                .modelId("model-max")
                .modelType("TEST")
                .name("Max")
                .accuracy(1.0)
                .build();
        assertEquals(0.0, minEntity.getAccuracy(), 0.001);
        assertEquals(1.0, maxEntity.getAccuracy(), 0.001);
    }

    @Test
    @DisplayName("Entity accepts zero training data size")
    void entity_zeroTrainingDataSize() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("TEST")
                .name("Test")
                .trainingDataSize(0)
                .build();
        assertEquals(0, entity.getTrainingDataSize());
    }

    @Test
    @DisplayName("Entity status can be set and retrieved")
    void entity_statusField() {
        MLModelEntity entity = new MLModelEntity();
        entity.setStatus("TRAINING");
        assertEquals("TRAINING", entity.getStatus());
        entity.setStatus("TRAINED");
        assertEquals("TRAINED", entity.getStatus());
        entity.setStatus("FAILED");
        assertEquals("FAILED", entity.getStatus());
    }

}
