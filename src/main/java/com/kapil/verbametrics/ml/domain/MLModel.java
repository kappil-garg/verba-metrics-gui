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
        BaseMLResult.validateModelId(modelId);
        BaseMLResult.validateModelType(modelType);
        BaseMLResult.validateStringField(name, "Model name");
        BaseMLResult.validateStringField(version, "Model version");
        BaseMLResult.validateScore(accuracy, "Accuracy");
        BaseMLResult.validateStringField(status, "Model status");
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
        return new BaseMLResult() {
        }.getPerformanceLevel(accuracy);
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
