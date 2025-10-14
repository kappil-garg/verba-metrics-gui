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
        BaseDomainResult.validateStringField(label, "Sentiment label");
        BaseDomainResult.validateRange(confidence, 0.0, 1.0, "Confidence");
        BaseDomainResult.validateRange(score, -1.0, 1.0, "Score");
    }

    /**
     * Determines the confidence level based on the confidence value.
     *
     * @return the confidence level as a string
     */
    public String getConfidenceLevel() {
        return new BaseDomainResult() {
        }.getConfidenceLevel(confidence);
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
