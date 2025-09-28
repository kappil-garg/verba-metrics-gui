package com.kapil.verbametrics.ml.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain record representing a machine learning model with metadata and configuration.
 *
 * @author Kapil Garg
 */
public record MLModel(
        String modelId,
        String modelType,
        String name,
        String description,
        String version,
        LocalDateTime createdAt,
        LocalDateTime lastUsed,
        Map<String, Object> parameters,
        Map<String, Object> performanceMetrics,
        String modelPath,
        boolean isActive,
        String createdBy,
        int trainingDataSize,
        double accuracy,
        String status
) {

    public MLModel {
        validateInputs(modelId, modelType, name, version, accuracy, status);
    }

    /**
     * Validates the input parameters to ensure they are valid.
     *
     * @param modelId   the model ID
     * @param modelType the model type
     * @param name      the model name
     * @param version   the model version
     * @param accuracy  the model accuracy
     * @param status    the model status
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(String modelId, String modelType, String name,
                                       String version, double accuracy, String status) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("Model ID cannot be null or blank");
        }
        if (modelType == null || modelType.isBlank()) {
            throw new IllegalArgumentException("Model type cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Model name cannot be null or blank");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Model version cannot be null or blank");
        }
        if (accuracy < 0.0 || accuracy > 1.0) {
            throw new IllegalArgumentException("Accuracy must be between 0.0 and 1.0");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Model status cannot be null or blank");
        }
    }

    /**
     * Checks if the model is trained and ready for use.
     *
     * @return true if the model is trained and active
     */
    public boolean isReadyForUse() {
        return isActive && "TRAINED".equals(status);
    }

    /**
     * Gets the model performance level based on accuracy.
     *
     * @return the performance level
     */
    public String getPerformanceLevel() {
        if (accuracy >= 0.9) return "EXCELLENT";
        if (accuracy >= 0.8) return "GOOD";
        if (accuracy >= 0.7) return "FAIR";
        if (accuracy >= 0.6) return "POOR";
        return "VERY_POOR";
    }

    @Override
    public String toString() {
        return """
                MLModel{
                    modelId='%s', modelType='%s', name='%s',
                    version='%s', accuracy=%.3f, status='%s',
                    isActive=%s, performanceLevel='%s'
                }""".formatted(
                modelId, modelType, name, version, accuracy, status,
                isActive, getPerformanceLevel()
        );
    }

}
