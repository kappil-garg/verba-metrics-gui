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
        validateInputs(modelId, modelType, accuracy, precision, recall, f1Score);
    }

    /**
     * Validates the input parameters to ensure they are valid.
     *
     * @param modelId   the model ID
     * @param modelType the model type
     * @param accuracy  the accuracy score
     * @param precision the precision score
     * @param recall    the recall score
     * @param f1Score   the F1 score
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(String modelId, String modelType, double accuracy,
                                       double precision, double recall, double f1Score) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("Model ID cannot be null or blank");
        }
        if (modelType == null || modelType.isBlank()) {
            throw new IllegalArgumentException("Model type cannot be null or blank");
        }
        if (accuracy < 0.0 || accuracy > 1.0) {
            throw new IllegalArgumentException("Accuracy must be between 0.0 and 1.0");
        }
        if (precision < 0.0 || precision > 1.0) {
            throw new IllegalArgumentException("Precision must be between 0.0 and 1.0");
        }
        if (recall < 0.0 || recall > 1.0) {
            throw new IllegalArgumentException("Recall must be between 0.0 and 1.0");
        }
        if (f1Score < 0.0 || f1Score > 1.0) {
            throw new IllegalArgumentException("F1 score must be between 0.0 and 1.0");
        }
    }

    /**
     * Calculates the overall model quality score.
     *
     * @return the quality score between 0.0 and 1.0
     */
    public double getQualityScore() {
        return (accuracy + precision + recall + f1Score) / 4.0;
    }

    /**
     * Gets the training performance level.
     *
     * @return the performance level
     */
    public String getPerformanceLevel() {
        double qualityScore = getQualityScore();
        if (qualityScore >= 0.9) return "EXCELLENT";
        if (qualityScore >= 0.8) return "GOOD";
        if (qualityScore >= 0.7) return "FAIR";
        if (qualityScore >= 0.6) return "POOR";
        return "VERY_POOR";
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
