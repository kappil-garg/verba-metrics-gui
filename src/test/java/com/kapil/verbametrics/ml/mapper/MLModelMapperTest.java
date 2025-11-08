package com.kapil.verbametrics.ml.mapper;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.entities.MLModelEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MLModelMapper conversion methods.
 *
 * @author Kapil Garg
 */
@DisplayName("MLModelMapper Tests")
class MLModelMapperTest {

    private MLModelMapper mapper;

    /**
     * Creates a complete MLModel for testing.
     *
     * @param now The current timestamp to use for createdAt and lastUsed.
     * @return The MLModel instance.
     */
    private static MLModel getMlModel(LocalDateTime now) {
        Map<String, Object> parameters = Map.of(
                "learningRate", 0.01,
                "epochs", 100,
                "optimizer", "Adam"
        );
        Map<String, Object> metrics = Map.of(
                "f1Score", 0.85,
                "precision", 0.87,
                "recall", 0.83
        );
        return new MLModel(
                "model-123",
                "CLASSIFICATION",
                "Sentiment Classifier",
                "A sentiment classification model",
                "1.0",
                now,
                now.plusDays(1),
                parameters,
                metrics,
                "/models/sentiment.model",
                true,
                "admin",
                1000,
                0.92,
                "TRAINED"
        );
    }

    /**
     * Creates an original MLModel for round-trip conversion tests.
     *
     * @param now The current timestamp to use for createdAt and lastUsed.
     * @return The original MLModel instance.
     */
    private static MLModel getOriginalModel(LocalDateTime now) {
        Map<String, Object> parameters = Map.of(
                "learningRate", 0.01,
                "epochs", 100,
                "optimizer", "Adam"
        );
        Map<String, Object> metrics = Map.of(
                "f1Score", 0.85,
                "precision", 0.87
        );
        return new MLModel(
                "model-123",
                "CLASSIFICATION",
                "Test Model",
                "Description",
                "1.0",
                now,
                now.plusDays(1),
                parameters,
                metrics,
                "/models/test.model",
                true,
                "admin",
                1000,
                0.92,
                "TRAINED"
        );
    }

    @BeforeEach
    void setUp() {
        mapper = new MLModelMapper();
    }

    @Test
    @DisplayName("toEntity should return null when model is null")
    void testToEntity_Null() {
        MLModelEntity entity = mapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    @DisplayName("toEntity should convert complete MLModel to entity")
    void testToEntity_CompleteModel() {
        LocalDateTime now = LocalDateTime.now();
        MLModel model = getMlModel(now);
        MLModelEntity entity = mapper.toEntity(model);
        assertNotNull(entity);
        assertEquals("model-123", entity.getModelId());
        assertEquals("CLASSIFICATION", entity.getModelType());
        assertEquals("Sentiment Classifier", entity.getName());
        assertEquals("A sentiment classification model", entity.getDescription());
        assertEquals("1.0", entity.getVersion());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now.plusDays(1), entity.getLastUsed());
        assertEquals("/models/sentiment.model", entity.getModelPath());
        assertTrue(entity.getIsActive());
        assertEquals("admin", entity.getCreatedBy());
        assertEquals(1000, entity.getTrainingDataSize());
        assertEquals(0.92, entity.getAccuracy(), 0.0001);
        assertEquals("TRAINED", entity.getStatus());
        // Verify maps are converted to String maps
        assertNotNull(entity.getParameters());
        assertEquals("0.01", entity.getParameters().get("learningRate"));
        assertEquals("100", entity.getParameters().get("epochs"));
        assertEquals("Adam", entity.getParameters().get("optimizer"));
        assertNotNull(entity.getPerformanceMetrics());
        assertEquals("0.85", entity.getPerformanceMetrics().get("f1Score"));
    }

    @Test
    @DisplayName("toEntity should handle model with null optional fields")
    void testToEntity_NullOptionalFields() {
        MLModel model = new MLModel(
                "model-456",
                "REGRESSION",
                "Test Model",
                null,
                "1.0",
                LocalDateTime.now(),
                null,
                null,
                null,
                "/models/test.model",
                true,
                null,
                500,
                0.85,
                "TRAINED"
        );
        MLModelEntity entity = mapper.toEntity(model);
        assertNotNull(entity);
        assertNull(entity.getDescription());
        assertNull(entity.getLastUsed());
        assertNull(entity.getCreatedBy());
        assertNotNull(entity.getParameters());
        assertTrue(entity.getParameters().isEmpty());
        assertNotNull(entity.getPerformanceMetrics());
        assertTrue(entity.getPerformanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("toEntity should handle empty maps")
    void testToEntity_EmptyMaps() {
        MLModel model = new MLModel(
                "model-789",
                "CLASSIFICATION",
                "Empty Maps Model",
                "Test",
                "1.0",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Map.of(),
                Map.of(),
                "/models/empty.model",
                true,
                "user",
                100,
                0.75,
                "TRAINED"
        );
        MLModelEntity entity = mapper.toEntity(model);
        assertNotNull(entity);
        assertNotNull(entity.getParameters());
        assertTrue(entity.getParameters().isEmpty());
        assertNotNull(entity.getPerformanceMetrics());
        assertTrue(entity.getPerformanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("toEntity should handle maps with null values")
    void testToEntity_MapsWithNullValues() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", null);
        parameters.put("param3", 42);
        MLModel model = new MLModel(
                "model-999",
                "CLASSIFICATION",
                "Test",
                "Test",
                "1.0",
                LocalDateTime.now(),
                LocalDateTime.now(),
                parameters,
                Map.of(),
                "/models/test.model",
                true,
                "user",
                100,
                0.80,
                "TRAINED"
        );
        MLModelEntity entity = mapper.toEntity(model);
        assertNotNull(entity);
        assertEquals("value1", entity.getParameters().get("param1"));
        assertFalse(entity.getParameters().containsKey("param2"));
        assertEquals("42", entity.getParameters().get("param3"));
    }

    @Test
    @DisplayName("toDomain should return null when entity is null")
    void testToDomain_Null() {
        MLModel model = mapper.toDomain(null);
        assertNull(model);
    }

    @Test
    @DisplayName("toDomain should convert complete entity to MLModel")
    void testToDomain_CompleteEntity() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> parameters = Map.of(
                "learningRate", "0.01",
                "epochs", "100",
                "optimizer", "Adam"
        );
        Map<String, String> metrics = Map.of(
                "f1Score", "0.85",
                "precision", "0.87",
                "recall", "0.83"
        );
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("CLASSIFICATION")
                .name("Sentiment Classifier")
                .description("A sentiment classification model")
                .version("1.0")
                .createdAt(now)
                .lastUsed(now.plusDays(1))
                .modelPath("/models/sentiment.model")
                .isActive(true)
                .createdBy("admin")
                .trainingDataSize(1000)
                .accuracy(0.92)
                .status("TRAINED")
                .parameters(parameters)
                .performanceMetrics(metrics)
                .build();
        MLModel model = mapper.toDomain(entity);
        assertNotNull(model);
        assertEquals("model-123", model.modelId());
        assertEquals("CLASSIFICATION", model.modelType());
        assertEquals("Sentiment Classifier", model.name());
        assertEquals("A sentiment classification model", model.description());
        assertEquals("1.0", model.version());
        assertEquals(now, model.createdAt());
        assertEquals(now.plusDays(1), model.lastUsed());
        assertEquals("/models/sentiment.model", model.modelPath());
        assertTrue(model.isActive());
        assertEquals("admin", model.createdBy());
        assertEquals(1000, model.trainingDataSize());
        assertEquals(0.92, model.accuracy(), 0.0001);
        assertEquals("TRAINED", model.status());
        // Verify maps are converted to Object maps with proper type parsing
        assertNotNull(model.parameters());
        assertEquals(0.01, model.parameters().get("learningRate"));
        assertEquals(100, model.parameters().get("epochs"));
        assertEquals("Adam", model.parameters().get("optimizer"));
        assertNotNull(model.performanceMetrics());
        assertEquals(0.85, model.performanceMetrics().get("f1Score"));
    }

    @Test
    @DisplayName("toDomain should handle entity with null optional fields")
    void testToDomain_NullOptionalFields() {
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-456")
                .modelType("REGRESSION")
                .name("Test Model")
                .description(null)
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .lastUsed(null)
                .modelPath("/models/test.model")
                .isActive(true)
                .createdBy(null)
                .trainingDataSize(500)
                .accuracy(0.85)
                .status("TRAINED")
                .parameters(null)
                .performanceMetrics(null)
                .build();
        MLModel model = mapper.toDomain(entity);
        assertNotNull(model);
        assertNull(model.description());
        assertNull(model.lastUsed());
        assertNull(model.createdBy());
        assertNotNull(model.parameters());
        assertTrue(model.parameters().isEmpty());
        assertNotNull(model.performanceMetrics());
        assertTrue(model.performanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("toDomain should parse numeric strings correctly")
    void testToDomain_NumericParsing() {
        Map<String, String> parameters = Map.of(
                "doubleValue", "3.14",
                "intValue", "42",
                "stringValue", "notANumber",
                "zeroInt", "0",
                "negativeDouble", "-2.5"
        );
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-789")
                .modelType("CLASSIFICATION")
                .name("Test")
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .modelPath("/test")
                .isActive(true)
                .trainingDataSize(100)
                .accuracy(0.8)
                .status("TRAINED")
                .parameters(parameters)
                .build();
        MLModel model = mapper.toDomain(entity);
        assertNotNull(model);
        assertEquals(3.14, model.parameters().get("doubleValue"));
        assertEquals(42, model.parameters().get("intValue"));
        assertEquals("notANumber", model.parameters().get("stringValue"));
        assertEquals(0, model.parameters().get("zeroInt"));
        assertEquals(-2.5, model.parameters().get("negativeDouble"));
    }

    @Test
    @DisplayName("toDomain should handle maps with null values")
    void testToDomain_MapsWithNullValues() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", null);
        parameters.put("param3", "42");
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-999")
                .modelType("CLASSIFICATION")
                .name("Test")
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .modelPath("/test")
                .isActive(true)
                .trainingDataSize(100)
                .accuracy(0.8)
                .status("TRAINED")
                .parameters(parameters)
                .build();
        MLModel model = mapper.toDomain(entity);
        assertNotNull(model);
        assertEquals("value1", model.parameters().get("param1"));
        assertFalse(model.parameters().containsKey("param2"));
        assertEquals(42, model.parameters().get("param3"));
    }

    @Test
    @DisplayName("updateEntity should return null when entity is null")
    void testUpdateEntity_NullEntity() {
        MLModel model = createTestModel();
        MLModelEntity result = mapper.updateEntity(null, model);
        assertNull(result);
    }

    @Test
    @DisplayName("updateEntity should return entity unchanged when model is null")
    void testUpdateEntity_NullModel() {
        MLModelEntity entity = createTestEntity();
        MLModelEntity result = mapper.updateEntity(entity, null);
        assertSame(entity, result);
    }

    @Test
    @DisplayName("updateEntity should update all updatable fields")
    void testUpdateEntity_AllFields() {
        LocalDateTime originalCreated = LocalDateTime.now().minusDays(10);
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-123")
                .modelType("OLD_TYPE")
                .name("Old Name")
                .description("Old Description")
                .version("0.9")
                .createdAt(originalCreated)
                .lastUsed(originalCreated)
                .modelPath("/old/path")
                .isActive(false)
                .createdBy("oldUser")
                .trainingDataSize(500)
                .accuracy(0.7)
                .status("DRAFT")
                .parameters(Map.of("old", "param"))
                .performanceMetrics(Map.of("old", "metric"))
                .build();
        LocalDateTime newLastUsed = LocalDateTime.now();
        Map<String, Object> newParams = Map.of(
                "learningRate", 0.01,
                "optimizer", "Adam"
        );
        Map<String, Object> newMetrics = Map.of(
                "f1Score", 0.90
        );
        MLModel model = new MLModel(
                "model-123",
                "NEW_TYPE",
                "New Name",
                "New Description",
                "1.0",
                LocalDateTime.now(),
                newLastUsed,
                newParams,
                newMetrics,
                "/new/path",
                true,
                "newUser",
                1000,
                0.95,
                "TRAINED"
        );
        MLModelEntity result = mapper.updateEntity(entity, model);
        assertSame(entity, result);
        // ModelId and createdAt should NOT be updated
        assertEquals("model-123", result.getModelId());
        assertEquals(originalCreated, result.getCreatedAt());
        // All other fields should be updated
        assertEquals("NEW_TYPE", result.getModelType());
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals("1.0", result.getVersion());
        assertEquals(newLastUsed, result.getLastUsed());
        assertEquals("/new/path", result.getModelPath());
        assertTrue(result.getIsActive());
        assertEquals("newUser", result.getCreatedBy());
        assertEquals(1000, result.getTrainingDataSize());
        assertEquals(0.95, result.getAccuracy(), 0.0001);
        assertEquals("TRAINED", result.getStatus());
        // Verify maps are updated
        assertEquals("0.01", result.getParameters().get("learningRate"));
        assertEquals("Adam", result.getParameters().get("optimizer"));
        assertFalse(result.getParameters().containsKey("old"));
        assertEquals("0.9", result.getPerformanceMetrics().get("f1Score"));
        assertFalse(result.getPerformanceMetrics().containsKey("old"));
    }

    @Test
    @DisplayName("updateEntity should handle null optional fields in model")
    void testUpdateEntity_NullOptionalFields() {
        MLModelEntity entity = createTestEntity();
        MLModel model = new MLModel(
                "model-123",
                "CLASSIFICATION",
                "Test Model",
                null,
                "1.0",
                LocalDateTime.now(),
                null,
                null,
                null,
                "/models/test.model",
                true,
                null,
                500,
                0.85,
                "TRAINED"
        );
        MLModelEntity result = mapper.updateEntity(entity, model);
        assertNotNull(result);
        assertNull(result.getDescription());
        assertNull(result.getLastUsed());
        assertNull(result.getCreatedBy());
        assertNotNull(result.getParameters());
        assertTrue(result.getParameters().isEmpty());
        assertNotNull(result.getPerformanceMetrics());
        assertTrue(result.getPerformanceMetrics().isEmpty());
    }

    @Test
    @DisplayName("Round-trip conversion should preserve all data")
    void testRoundTrip_ToEntityToDomain() {
        LocalDateTime now = LocalDateTime.now();
        MLModel originalModel = getOriginalModel(now);
        MLModelEntity entity = mapper.toEntity(originalModel);
        MLModel convertedModel = mapper.toDomain(entity);
        assertNotNull(convertedModel);
        assertEquals(originalModel.modelId(), convertedModel.modelId());
        assertEquals(originalModel.modelType(), convertedModel.modelType());
        assertEquals(originalModel.name(), convertedModel.name());
        assertEquals(originalModel.description(), convertedModel.description());
        assertEquals(originalModel.version(), convertedModel.version());
        assertEquals(originalModel.createdAt(), convertedModel.createdAt());
        assertEquals(originalModel.lastUsed(), convertedModel.lastUsed());
        assertEquals(originalModel.modelPath(), convertedModel.modelPath());
        assertEquals(originalModel.isActive(), convertedModel.isActive());
        assertEquals(originalModel.createdBy(), convertedModel.createdBy());
        assertEquals(originalModel.trainingDataSize(), convertedModel.trainingDataSize());
        assertEquals(originalModel.accuracy(), convertedModel.accuracy(), 0.0001);
        assertEquals(originalModel.status(), convertedModel.status());
        // Verify parameters are preserved (type conversions are expected)
        assertEquals(0.01, convertedModel.parameters().get("learningRate"));
        assertEquals(100, convertedModel.parameters().get("epochs"));
        assertEquals("Adam", convertedModel.parameters().get("optimizer"));
    }

    /**
     * Creates a test MLModel with preset values.
     */
    private MLModel createTestModel() {
        return new MLModel(
                "model-123",
                "CLASSIFICATION",
                "Test Model",
                "Test Description",
                "1.0",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Map.of("param", "value"),
                Map.of("metric", "value"),
                "/models/test.model",
                true,
                "testUser",
                500,
                0.85,
                "TRAINED"
        );
    }

    /**
     * Creates a test MLModelEntity with preset values.
     */
    private MLModelEntity createTestEntity() {
        return MLModelEntity.builder()
                .modelId("model-123")
                .modelType("CLASSIFICATION")
                .name("Test Model")
                .description("Test Description")
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .lastUsed(LocalDateTime.now())
                .modelPath("/models/test.model")
                .isActive(true)
                .createdBy("testUser")
                .trainingDataSize(500)
                .accuracy(0.85)
                .status("TRAINED")
                .parameters(Map.of("param", "value"))
                .performanceMetrics(Map.of("metric", "value"))
                .build();
    }

}
