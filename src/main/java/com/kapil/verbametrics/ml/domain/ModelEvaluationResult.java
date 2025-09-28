package com.kapil.verbametrics.ml.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain record representing the result of model evaluation operations.
 *
 * @author Kapil Garg
 */
public record ModelEvaluationResult(
        String modelId,
        String evaluationType,
        boolean success,
        double accuracy,
        double precision,
        double recall,
        double f1Score,
        double auc,
        long evaluationTimeMs,
        int testDataSize,
        Map<String, Object> confusionMatrix,
        Map<String, Object> additionalMetrics,
        String errorMessage,
        LocalDateTime evaluatedAt
) {

    public ModelEvaluationResult {
        validateInputs(modelId, evaluationType, accuracy, precision, recall, f1Score, auc);
    }

    /**
     * Validates the input parameters to ensure they are valid.
     *
     * @param modelId        the model ID
     * @param evaluationType the evaluation type
     * @param accuracy       the accuracy score
     * @param precision      the precision score
     * @param recall         the recall score
     * @param f1Score        the F1 score
     * @param auc            the AUC score
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(String modelId, String evaluationType, double accuracy,
                                       double precision, double recall, double f1Score, double auc) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("Model ID cannot be null or blank");
        }
        if (evaluationType == null || evaluationType.isBlank()) {
            throw new IllegalArgumentException("Evaluation type cannot be null or blank");
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
        if (auc < 0.0 || auc > 1.0) {
            throw new IllegalArgumentException("AUC must be between 0.0 and 1.0");
        }
    }

    /**
     * Calculates the overall evaluation score.
     *
     * @return the evaluation score between 0.0 and 1.0
     */
    public double getEvaluationScore() {
        return (accuracy + precision + recall + f1Score + auc) / 5.0;
    }

    /**
     * Gets the evaluation performance level.
     *
     * @return the performance level
     */
    public String getPerformanceLevel() {
        double evaluationScore = getEvaluationScore();
        if (evaluationScore >= 0.9) return "EXCELLENT";
        if (evaluationScore >= 0.8) return "GOOD";
        if (evaluationScore >= 0.7) return "FAIR";
        if (evaluationScore >= 0.6) return "POOR";
        return "VERY_POOR";
    }

    /**
     * Checks if the model is suitable for production use.
     *
     * @return true if the model meets production quality standards
     */
    public boolean isProductionReady() {
        return success && accuracy >= 0.8 && precision >= 0.7 && recall >= 0.7;
    }

    @Override
    public String toString() {
        return """
                ModelEvaluationResult{
                    modelId='%s', evaluationType='%s', success=%s,
                    accuracy=%.3f, precision=%.3f, recall=%.3f, f1Score=%.3f, auc=%.3f,
                    evaluationScore=%.3f, performanceLevel='%s', productionReady=%s
                }""".formatted(
                modelId, evaluationType, success, accuracy, precision, recall, f1Score, auc,
                getEvaluationScore(), getPerformanceLevel(), isProductionReady()
        );
    }

}
