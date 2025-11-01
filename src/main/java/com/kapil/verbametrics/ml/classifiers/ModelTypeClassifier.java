package com.kapil.verbametrics.ml.classifiers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Classifier for validating and managing machine learning model types.
 * Handles validation of supported model types and required fields for each type.
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
        if (modelType == null) {
            return false;
        }
        String normalized = modelType.toUpperCase();
        return properties.getSupportedModelTypes().stream()
                .map(String::toUpperCase)
                .anyMatch(s -> s.equals(normalized));
    }

    /**
     * Gets the required fields for a model type.
     *
     * @param modelType the model type
     * @return list of required fields
     */
    public List<String> getRequiredFields(String modelType) {
        String key = modelType == null ? "" : modelType.toUpperCase();
        return properties.getRequiredFields().getOrDefault(key, List.of());
    }

}
