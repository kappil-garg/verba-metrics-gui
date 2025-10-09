package com.kapil.verbametrics.ml.domain;

import com.kapil.verbametrics.util.VerbaMetricsConstants;

/**
 * Base abstract class for ML domain records providing common validation and utility methods.
 *
 * @author Kapil Garg
 */
public abstract class BaseMLResult {

    /**
     * Validates that a model ID is not null or blank.
     *
     * @param modelId the model ID to validate
     * @throws IllegalArgumentException if the model ID is invalid
     */
    protected static void validateModelId(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("Model ID cannot be null or blank");
        }
    }

    /**
     * Validates that a model type is not null or blank.
     *
     * @param modelType the model type to validate
     * @throws IllegalArgumentException if the model type is invalid
     */
    protected static void validateModelType(String modelType) {
        if (modelType == null || modelType.isBlank()) {
            throw new IllegalArgumentException("Model type cannot be null or blank");
        }
    }

    /**
     * Validates that a score is within the valid range [0.0, 1.0].
     *
     * @param score     the score to validate
     * @param scoreName the name of the score for error messages
     * @throws IllegalArgumentException if the score is invalid
     */
    protected static void validateScore(double score, String scoreName) {
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException(scoreName + " must be between 0.0 and 1.0");
        }
    }

    /**
     * Validates that a string field is not null or blank.
     *
     * @param value     the value to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the value is invalid
     */
    protected static void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
    }

    /**
     * Gets the performance level based on a score.
     *
     * @param score the score to evaluate
     * @return the performance level string
     */
    protected String getPerformanceLevel(double score) {
        if (score >= 0.9) {
            return VerbaMetricsConstants.K_EXCELLENT;
        }
        if (score >= 0.8) {
            return VerbaMetricsConstants.K_GOOD;
        }
        if (score >= 0.7) {
            return VerbaMetricsConstants.K_FAIR;
        }
        if (score >= 0.6) {
            return VerbaMetricsConstants.K_POOR;
        }
        return VerbaMetricsConstants.K_VERY_POOR;
    }

    /**
     * Calculates the average of multiple scores.
     *
     * @param scores the scores to average
     * @return the average score
     */
    protected double calculateAverageScore(double... scores) {
        if (scores.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.length;
    }

}
