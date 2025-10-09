package com.kapil.verbametrics.ml.classifiers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Classifier for determining the type of machine learning model based on training data structure.
 * Handles model types like SENTIMENT, TOPIC_MODELING, CLASSIFICATION, etc.
 *
 * @author Kapil Garg
 */
@Component
public class ModelTypeClassifier {

    private final MLModelProperties properties;

    @Autowired
    public ModelTypeClassifier(MLModelProperties properties) {
        this.properties = properties;
    }

    /**
     * Validates if a model type is supported.
     *
     * @param modelType the model type to validate
     * @return true if the model type is supported
     */
    public boolean isValidModelType(String modelType) {
        return properties.getSupportedModelTypes().contains(modelType);
    }

    /**
     * Gets all supported model types.
     *
     * @return list of supported model types
     */
    public List<String> getSupportedModelTypes() {
        return properties.getSupportedModelTypes();
    }

    /**
     * Classifies the model type based on training data structure.
     *
     * @param trainingData the training data
     * @return the classified model type
     */
    public String classifyModelType(List<Map<String, Object>> trainingData) {
        if (trainingData == null || trainingData.isEmpty()) {
            return VerbaMetricsConstants.K_UNKNOWN;
        }
        Map<String, Object> firstData = trainingData.getFirst();
        // Classify based on data structure
        if (firstData.containsKey("text") && firstData.containsKey("sentiment")) {
            return VerbaMetricsConstants.K_SENTIMENT;
        } else if (firstData.containsKey("text") && !firstData.containsKey("sentiment")) {
            return VerbaMetricsConstants.K_TOPIC_MODELING;
        } else if (firstData.containsKey("features") && firstData.containsKey("label")) {
            return VerbaMetricsConstants.K_CLASSIFICATION;
        }
        return VerbaMetricsConstants.K_UNKNOWN;
    }

    /**
     * Gets the required fields for a model type.
     *
     * @param modelType the model type
     * @return list of required fields
     */
    public List<String> getRequiredFields(String modelType) {
        return properties.getRequiredFields().getOrDefault(modelType, List.of());
    }

}
