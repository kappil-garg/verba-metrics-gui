package com.kapil.verbametrics.domain;

import com.kapil.verbametrics.util.VerbaMetricsConstants;

/**
 * Domain record representing sentiment analysis results.
 *
 * @author Kapil Garg
 */
public record SentimentScore(
        String label,
        double confidence,
        double score
) {

    public SentimentScore {
        validateInputs(label, confidence, score);
    }

    /**
     * Validates the input parameters to ensure they are valid.
     *
     * @param label      the sentiment label
     * @param confidence the confidence level
     * @param score      the sentiment score
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(String label, double confidence, double score) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Sentiment label cannot be null or blank");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
        if (score < -1.0 || score > 1.0) {
            throw new IllegalArgumentException("Score must be between -1.0 and 1.0");
        }
    }

    /**
     * Determines the confidence level based on the confidence value.
     *
     * @return the confidence level as a string
     */
    public String getConfidenceLevel() {
        if (confidence >= VerbaMetricsConstants.HIGH_CONFIDENCE) {
            return VerbaMetricsConstants.K_HIGH;
        } else if (confidence >= VerbaMetricsConstants.MEDIUM_CONFIDENCE) {
            return VerbaMetricsConstants.K_MEDIUM;
        } else {
            return VerbaMetricsConstants.K_LOW;
        }
    }

    /**
     * Checks if the sentiment is positive.
     *
     * @return true if the sentiment is positive
     */
    public boolean isPositive() {
        return VerbaMetricsConstants.POSITIVE.equals(label);
    }

    /**
     * Checks if the sentiment is negative.
     *
     * @return true if the sentiment is negative
     */
    public boolean isNegative() {
        return VerbaMetricsConstants.NEGATIVE.equals(label);
    }

    @Override
    public String toString() {
        return """
                SentimentScore{
                    label='%s', confidence=%.3f, score=%.3f,
                    confidenceLevel='%s'
                }""".formatted(
                label, confidence, score, getConfidenceLevel()
        );
    }

}
