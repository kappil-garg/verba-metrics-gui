package com.kapil.verbametrics.ml.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain record representing the result of model training operations.
 *
 * @author Kapil Garg
 */
public record ModelTrainingResult(
        String modelId,
        String modelType,
        boolean success,
        double accuracy,
        double precision,
        double recall,
        double f1Score,
        long trainingTimeMs,
        int trainingDataSize,
        int testDataSize,
        Map<String, Object> additionalMetrics,
        String errorMessage,
        LocalDateTime completedAt
) {

    public ModelTrainingResult {
        BaseMLResult.validateModelId(modelId);
        BaseMLResult.validateModelType(modelType);
        BaseMLResult.validateScore(accuracy, "Accuracy");
        BaseMLResult.validateScore(precision, "Precision");
        BaseMLResult.validateScore(recall, "Recall");
        BaseMLResult.validateScore(f1Score, "F1 score");
    }

    /**
     * Calculates the overall model quality score.
     *
     * @return the quality score between 0.0 and 1.0
     */
    public double getQualityScore() {
        return new BaseMLResult() {}.calculateAverageScore(accuracy, precision, recall, f1Score);
    }

    /**
     * Gets the training performance level.
     *
     * @return the performance level
     */
    public String getPerformanceLevel() {
        return new BaseMLResult() {}.getPerformanceLevel(getQualityScore());
    }

    @Override
    public String toString() {
        return """
                ModelTrainingResult{
                    modelId='%s', modelType='%s', success=%s,
                    accuracy=%.3f, precision=%.3f, recall=%.3f, f1Score=%.3f,
                    qualityScore=%.3f, performanceLevel='%s', trainingTimeMs=%d
                }""".formatted(
                modelId, modelType, success, accuracy, precision, recall, f1Score,
                getQualityScore(), getPerformanceLevel(), trainingTimeMs
        );
    }

}
