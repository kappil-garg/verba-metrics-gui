package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.MLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for ML model caching operations.
 * Handles caching, retrieval, and management of trained models.
 *
 * @author Kapil Garg
 */
@Component
public class ModelCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCacheManager.class);

    private final MLModelProperties properties;
    private final Map<String, MLModel> modelCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @Autowired
    public ModelCacheManager(MLModelProperties properties) {
        this.properties = properties;
    }

    /**
     * Caches a trained model.
     *
     * @param modelId the model ID
     * @param model   the model to cache
     */
    public void cacheModel(String modelId, MLModel model) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(model, "Model cannot be null");
        int maxSize = properties.getCacheSettings().getOrDefault("max-models", 100);
        if (modelCache.size() >= maxSize) {
            evictOldestModel();
        }
        modelCache.put(modelId, model);
        cacheTimestamps.put(modelId, System.currentTimeMillis());
        LOGGER.debug("Model cached: {}", modelId);
    }

    /**
     * Retrieves a cached model.
     *
     * @param modelId the model ID
     * @return the cached model if found
     */
    public Optional<MLModel> getCachedModel(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        MLModel model = modelCache.get(modelId);
        if (model != null) {
            cacheTimestamps.put(modelId, System.currentTimeMillis());
            LOGGER.debug("Model retrieved from cache: {}", modelId);
            return Optional.of(model);
        }
        LOGGER.debug("Model not found in cache: {}", modelId);
        return Optional.empty();
    }

    /**
     * Evicts a model from cache.
     *
     * @param modelId the model ID
     */
    public void evictModel(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        modelCache.remove(modelId);
        cacheTimestamps.remove(modelId);
        LOGGER.debug("Model evicted from cache: {}", modelId);
    }

    /**
     * Evicts all models from cache.
     */
    public void evictAllModels() {
        modelCache.clear();
        cacheTimestamps.clear();
        LOGGER.debug("All models evicted from cache");
    }

    /**
     * Gets all cached model IDs.
     *
     * @return list of cached model IDs
     */
    public List<String> getCachedModelIds() {
        return new ArrayList<>(modelCache.keySet());
    }

    /**
     * Checks if a model is cached.
     *
     * @param modelId the model ID
     * @return true if the model is cached
     */
    public boolean isModelCached(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        return modelCache.containsKey(modelId);
    }

    /**
     * Gets cache statistics.
     *
     * @return cache statistics map
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", modelCache.size());
        stats.put("maxSize", properties.getCacheSettings().getOrDefault("max-models", 100));
        stats.put("cachedModels", new ArrayList<>(modelCache.keySet()));
        return stats;
    }

    /**
     * Evicts the oldest model from cache.
     */
    private void evictOldestModel() {
        cacheTimestamps.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).ifPresent(this::evictModel);
    }

}
