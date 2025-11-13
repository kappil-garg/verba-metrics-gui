package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.MLModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelCacheManager.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModelCacheManagerTest {

    @Mock
    private MLModelProperties properties;

    private ModelCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        when(properties.getCacheSettings()).thenReturn(Map.of("max-models", 100));
        cacheManager = new ModelCacheManager(properties);
    }

    private MLModel createTestModel(String modelId, String modelType) {
        return new MLModel(
                modelId, modelType, "Test Model", "Test Description", "1.0",
                LocalDateTime.now(), LocalDateTime.now(),
                Map.of(), Map.of(), "/models/test.model",
                true, "test-user", 1000, 0.85, "TRAINED"
        );
    }

    @Test
    @DisplayName("cacheModel should cache a model successfully")
    void cacheModel_validModel_cachesSuccessfully() {
        MLModel model = createTestModel("model-1", "CLASSIFICATION");
        cacheManager.cacheModel("model-1", model);
        assertTrue(cacheManager.isModelCached("model-1"), "Model should be cached");
    }

    @Test
    @DisplayName("getCachedModel should retrieve cached model")
    void getCachedModel_cachedModel_retrievesSuccessfully() {
        MLModel model = createTestModel("model-1", "CLASSIFICATION");
        cacheManager.cacheModel("model-1", model);
        Optional<MLModel> retrieved = cacheManager.getCachedModel("model-1");
        assertTrue(retrieved.isPresent(), "Should retrieve cached model");
        assertEquals(model, retrieved.get(), "Should retrieve correct model");
    }

    @Test
    @DisplayName("getCachedModel should return empty for non-existent model")
    void getCachedModel_nonExistentModel_returnsEmpty() {
        Optional<MLModel> retrieved = cacheManager.getCachedModel("non-existent");
        assertFalse(retrieved.isPresent(), "Should return empty for non-existent model");
    }

    @Test
    @DisplayName("cacheModel should throw exception for null model ID")
    void cacheModel_nullModelId_throwsException() {
        MLModel model = createTestModel("model-1", "CLASSIFICATION");
        assertThrows(NullPointerException.class,
                () -> cacheManager.cacheModel(null, model),
                "Should throw exception for null model ID");
    }

    @Test
    @DisplayName("cacheModel should throw exception for null model")
    void cacheModel_nullModel_throwsException() {
        assertThrows(NullPointerException.class,
                () -> cacheManager.cacheModel("model-1", null),
                "Should throw exception for null model");
    }

    @Test
    @DisplayName("getCachedModel should throw exception for null model ID")
    void getCachedModel_nullModelId_throwsException() {
        assertThrows(NullPointerException.class,
                () -> cacheManager.getCachedModel(null),
                "Should throw exception for null model ID");
    }

    @Test
    @DisplayName("evictModel should remove model from cache")
    void evictModel_cachedModel_removesFromCache() {
        MLModel model = createTestModel("model-1", "CLASSIFICATION");
        cacheManager.cacheModel("model-1", model);
        cacheManager.evictModel("model-1");
        assertFalse(cacheManager.isModelCached("model-1"), "Model should be evicted");
    }

    @Test
    @DisplayName("evictModel should handle non-existent model gracefully")
    void evictModel_nonExistentModel_handlesGracefully() {
        assertDoesNotThrow(() -> cacheManager.evictModel("non-existent"),
                "Should handle non-existent model gracefully");
    }

    @Test
    @DisplayName("evictAllModels should clear all cached models")
    void evictAllModels_multipleCachedModels_clearsAll() {
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        cacheManager.cacheModel("model-2", createTestModel("model-2", "SENTIMENT"));
        cacheManager.evictAllModels();
        assertFalse(cacheManager.isModelCached("model-1"), "Model 1 should be evicted");
        assertFalse(cacheManager.isModelCached("model-2"), "Model 2 should be evicted");
        assertEquals(0, cacheManager.getCachedModelIds().size(), "Cache should be empty");
    }

    @Test
    @DisplayName("isModelCached should return true for cached model")
    void isModelCached_cachedModel_returnsTrue() {
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        boolean isCached = cacheManager.isModelCached("model-1");
        assertTrue(isCached, "Should return true for cached model");
    }

    @Test
    @DisplayName("isModelCached should return false for non-cached model")
    void isModelCached_nonCachedModel_returnsFalse() {
        boolean isCached = cacheManager.isModelCached("non-existent");
        assertFalse(isCached, "Should return false for non-cached model");
    }

    @Test
    @DisplayName("getCachedModelIds should return all cached model IDs")
    void getCachedModelIds_multipleCachedModels_returnsAllIds() {
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        cacheManager.cacheModel("model-2", createTestModel("model-2", "SENTIMENT"));
        cacheManager.cacheModel("model-3", createTestModel("model-3", "REGRESSION"));
        List<String> ids = cacheManager.getCachedModelIds();
        assertEquals(3, ids.size(), "Should return 3 model IDs");
        assertTrue(ids.contains("model-1"), "Should contain model-1");
        assertTrue(ids.contains("model-2"), "Should contain model-2");
        assertTrue(ids.contains("model-3"), "Should contain model-3");
    }

    @Test
    @DisplayName("getCachedModelIds should return empty list when cache is empty")
    void getCachedModelIds_emptyCache_returnsEmptyList() {
        List<String> ids = cacheManager.getCachedModelIds();
        assertNotNull(ids, "Should return non-null list");
        assertTrue(ids.isEmpty(), "Should return empty list");
    }

    @Test
    @DisplayName("getCacheStatistics should return cache information")
    void getCacheStatistics_returnsStatistics() {
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        cacheManager.cacheModel("model-2", createTestModel("model-2", "SENTIMENT"));
        Map<String, Object> stats = cacheManager.getCacheStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertEquals(2, stats.get("cacheSize"), "Should have correct cache size");
        assertEquals(100, stats.get("maxSize"), "Should have correct max size");
        assertNotNull(stats.get("cachedModels"), "Should have cached models list");
    }

    @Test
    @DisplayName("cacheModel should evict oldest model when cache is full")
    void cacheModel_cacheIsFull_evictsOldestModel() throws InterruptedException {
        when(properties.getCacheSettings()).thenReturn(Map.of("max-models", 2));
        cacheManager = new ModelCacheManager(properties);
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        Thread.sleep(10);
        cacheManager.cacheModel("model-2", createTestModel("model-2", "SENTIMENT"));
        Thread.sleep(10);
        cacheManager.cacheModel("model-3", createTestModel("model-3", "REGRESSION"));
        assertFalse(cacheManager.isModelCached("model-1"), "Oldest model should be evicted");
        assertTrue(cacheManager.isModelCached("model-2"), "Model 2 should still be cached");
        assertTrue(cacheManager.isModelCached("model-3"), "Model 3 should be cached");
    }

    @Test
    @DisplayName("cacheModel should overwrite existing model")
    void cacheModel_existingModelId_overwritesModel() {
        MLModel model1 = createTestModel("model-1", "CLASSIFICATION");
        MLModel model2 = new MLModel(
                "model-1", "SENTIMENT", "Updated Model", "Updated Description", "2.0",
                LocalDateTime.now(), LocalDateTime.now(),
                Map.of(), Map.of(), "/models/updated.model",
                true, "test-user", 2000, 0.90, "TRAINED"
        );
        cacheManager.cacheModel("model-1", model1);
        cacheManager.cacheModel("model-1", model2);
        Optional<MLModel> retrieved = cacheManager.getCachedModel("model-1");
        assertTrue(retrieved.isPresent(), "Model should be cached");
        assertEquals("SENTIMENT", retrieved.get().modelType(), "Should have updated model type");
        assertEquals(2000, retrieved.get().trainingDataSize(), "Should have updated training data size");
    }

    @Test
    @DisplayName("getCachedModel should update access timestamp")
    void getCachedModel_updateAccessTimestamp() throws InterruptedException {
        when(properties.getCacheSettings()).thenReturn(Map.of("max-models", 2));
        cacheManager = new ModelCacheManager(properties);
        cacheManager.cacheModel("model-1", createTestModel("model-1", "CLASSIFICATION"));
        Thread.sleep(10);
        cacheManager.cacheModel("model-2", createTestModel("model-2", "SENTIMENT"));
        Thread.sleep(10);
        cacheManager.getCachedModel("model-1");
        Thread.sleep(10);
        cacheManager.cacheModel("model-3", createTestModel("model-3", "REGRESSION"));
        assertTrue(cacheManager.isModelCached("model-1"), "Model 1 should still be cached (recently accessed)");
        assertFalse(cacheManager.isModelCached("model-2"), "Model 2 should be evicted (oldest)");
        assertTrue(cacheManager.isModelCached("model-3"), "Model 3 should be cached");
    }

}
