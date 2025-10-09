package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.classifiers.ModelTypeClassifier;
import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ModelTrainingEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTrainingEngine.class);

    private final MLModelProperties properties;
    private final ModelTypeClassifier modelTypeClassifier;

    @Autowired
    public ModelTrainingEngine(MLModelProperties properties, ModelTypeClassifier modelTypeClassifier) {
        this.properties = properties;
        this.modelTypeClassifier = modelTypeClassifier;
    }

    /*
     * Trains a model.
     *
     * @param modelId The model ID
     * @param modelType The model type
     * @param trainingData The training data
     * @param parameters The parameters
     * @return The training result
     */
    public ModelTrainingResult trainModel(String modelId, String modelType,
                                          List<Map<String, Object>> trainingData,
                                          Map<String, Object> parameters) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(modelType, "Model type cannot be null");
        Objects.requireNonNull(trainingData, "Training data cannot be null");
        Objects.requireNonNull(parameters, "Parameters cannot be null");
        LOGGER.debug("Starting model training for type: {} with {} data points", modelType, trainingData.size());
        // TODO: Implement ML training logic with real ML libraries (DL4J, Smile, Weka, etc.)
        throw new UnsupportedOperationException("Model training not implemented yet");
    }

    /*
     * Validates the training data before training.
     *
     * @param trainingData The training data
     * @param modelType The model type
     * @return true if the training data is valid, false otherwise
     */
    public boolean validateTrainingData(List<Map<String, Object>> trainingData, String modelType) {
        if (trainingData == null || trainingData.isEmpty()) {
            LOGGER.warn("Training data is null or empty");
            return false;
        }
        int minDataSize = properties.getTrainingLimits().getOrDefault("min-data-size", 10);
        if (trainingData.size() < minDataSize) {
            LOGGER.warn("Training data size too small: {}", trainingData.size());
            return false;
        }
        return modelTypeClassifier.isValidModelType(modelType);
    }

    /*
     * Gets the supported model types.
     *
     * @return The supported model types
     */
    public List<String> getSupportedModelTypes() {
        return properties.getSupportedModelTypes();
    }

    /*
     * Gets the default parameters for a model type.
     *
     * @param modelType The model type
     * @return The default parameters for the model type
     */
    public Map<String, Object> getDefaultParameters(String modelType) {
        Object defaultParams = properties.getDefaultParameters().get(modelType);
        if (defaultParams instanceof Map<?, ?> map) {
            Map<String, Object> params = new java.util.HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    params.put((String) entry.getKey(), entry.getValue());
                } else {
                    return Map.of();
                }
            }
            return params;
        }
        return Map.of();
    }

}
