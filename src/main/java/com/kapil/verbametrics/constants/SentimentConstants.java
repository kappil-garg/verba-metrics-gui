package com.kapil.verbametrics.constants;

/**
 * Constants used in sentiment analysis operations.
 *
 * @author Kapil Garg
 */
public final class SentimentConstants {

    private SentimentConstants() {}

    // Sentiment Labels
    public static final String NEUTRAL = "NEUTRAL";
    public static final String POSITIVE = "POSITIVE";
    public static final String NEGATIVE = "NEGATIVE";

    // Confidence Levels
    public static final double HIGH_CONFIDENCE = 0.8;
    public static final double MEDIUM_CONFIDENCE = 0.6;

    // Sentiment Thresholds
    public static final double POSITIVE_THRESHOLD = 0.1;
    public static final double NEGATIVE_THRESHOLD = -0.1;

}
