package com.kapil.verbametrics.ml.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages class values for ML models to ensure consistent mapping between training and prediction.
 *
 * @author Kapil Garg
 */
@Component
public class ClassValueManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassValueManager.class);

    private final Map<String, List<String>> modelClassValues = new ConcurrentHashMap<>();

    /**
     * Stores class values for a specific model.
     *
     * @param modelId     the model ID
     * @param classValues the class values in the order they were used during training
     */
    public void storeClassValues(String modelId, List<String> classValues) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(classValues, "Class values cannot be null");
        List<String> storedValues = new ArrayList<>(classValues);
        modelClassValues.put(modelId, storedValues);
        LOGGER.debug("Stored class values for model {} ({} classes)", modelId, storedValues.size());
    }

    /**
     * Retrieves class values for a specific model.
     *
     * @param modelId the model ID
     * @return the class values for the model, or empty list if not found
     */
    public List<String> getClassValues(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        List<String> classValues = modelClassValues.get(modelId);
        if (classValues == null) {
            LOGGER.warn("No class values found for model: {}", modelId);
            return new ArrayList<>();
        }
        return new ArrayList<>(classValues);
    }

}
