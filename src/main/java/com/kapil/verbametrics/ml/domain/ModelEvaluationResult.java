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
        BaseMLResult.validateModelId(modelId);
        BaseMLResult.validateStringField(evaluationType, "Evaluation type");
        BaseMLResult.validateScore(accuracy, "Accuracy");
        BaseMLResult.validateScore(precision, "Precision");
        BaseMLResult.validateScore(recall, "Recall");
        BaseMLResult.validateScore(f1Score, "F1 score");
        BaseMLResult.validateScore(auc, "AUC");
    }

    /**
     * Calculates the overall evaluation score.
     *
     * @return the evaluation score between 0.0 and 1.0
     */
    public double getEvaluationScore() {
        return new BaseMLResult() {
        }.calculateAverageScore(accuracy, precision, recall, f1Score, auc);
    }

    /**
     * Gets the evaluation performance level.
     *
     * @return the performance level
     */
    public String getPerformanceLevel() {
        return new BaseMLResult() {
        }.getPerformanceLevel(getEvaluationScore());
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
