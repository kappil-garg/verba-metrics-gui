package com.kapil.verbametrics.domain;

import com.kapil.verbametrics.util.VerbaMetricsConstants;

/**
 * Base abstract class for main domain records providing common validation and utility methods.
 *
 * @author Kapil Garg
 */
public abstract class BaseDomainResult {

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
     * Validates that a numeric value is non-negative.
     *
     * @param value     the value to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the value is negative
     */
    protected static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that a numeric value is non-negative.
     *
     * @param value     the value to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the value is negative
     */
    protected static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that a numeric value is within a specified range.
     *
     * @param value     the value to validate
     * @param min       the minimum allowed value
     * @param max       the maximum allowed value
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the value is outside the range
     */
    protected static void validateRange(double value, double min, double max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }

    /**
     * Validates that a numeric value is within a specified range.
     *
     * @param value     the value to validate
     * @param min       the minimum allowed value
     * @param max       the maximum allowed value
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the value is outside the range
     */
    protected static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }

    /**
     * Performs safe division to avoid division by zero.
     *
     * @param numerator   the numerator
     * @param denominator the denominator
     * @return the result of division, or 0.0 if denominator is zero
     */
    protected static double safeDivide(double numerator, double denominator) {
        return denominator > 0 ? numerator / denominator : 0.0;
    }

    /**
     * Performs safe division to avoid division by zero.
     *
     * @param numerator   the numerator
     * @param denominator the denominator
     * @return the result of division, or 0.0 if denominator is zero
     */
    protected static double safeDivide(int numerator, int denominator) {
        return denominator > 0 ? (double) numerator / denominator : 0.0;
    }

    /**
     * Gets the confidence level based on confidence value and thresholds.
     *
     * @param confidence      the confidence value
     * @param highThreshold   the high confidence threshold
     * @param mediumThreshold the medium confidence threshold
     * @return the confidence level string
     */
    protected String getConfidenceLevel(double confidence, double highThreshold, double mediumThreshold) {
        if (confidence >= highThreshold) {
            return VerbaMetricsConstants.K_HIGH;
        } else if (confidence >= mediumThreshold) {
            return VerbaMetricsConstants.K_MEDIUM;
        } else {
            return VerbaMetricsConstants.K_LOW;
        }
    }

    /**
     * Gets the confidence level using default thresholds.
     *
     * @param confidence the confidence value
     * @return the confidence level string
     */
    protected String getConfidenceLevel(double confidence) {
        return getConfidenceLevel(confidence, VerbaMetricsConstants.HIGH_CONFIDENCE, VerbaMetricsConstants.MEDIUM_CONFIDENCE);
    }

}
