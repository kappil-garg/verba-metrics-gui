package com.kapil.verbametrics.util;

/**
 * Constants used throughout the VerbaMetrics application.
 *
 * @author Kapil Garg
 */
public final class VerbaMetricsConstants {

    private VerbaMetricsConstants() {
        // Private constructor to prevent instantiation
    }

    // Complexity Levels
    public static final String K_VERY_EASY = "Very Easy";
    public static final String K_EASY = "Easy";
    public static final String K_MODERATE = "Moderate";
    public static final String K_DIFFICULT = "Difficult";
    public static final String K_VERY_DIFFICULT = "Very Difficult";

    // Reading Levels
    public static final String K_ELEMENTARY = "Elementary";
    public static final String K_MIDDLE_SCHOOL = "Middle School";
    public static final String K_HIGH_SCHOOL = "High School";
    public static final String K_COLLEGE = "College";
    public static final String K_GRADUATE = "Graduate";

    // Sentiment Labels
    public static final String NEUTRAL = "NEUTRAL";
    public static final String POSITIVE = "POSITIVE";
    public static final String NEGATIVE = "NEGATIVE";

    // Confidence Levels
    public static final String K_HIGH = "HIGH";
    public static final String K_MEDIUM = "MEDIUM";
    public static final String K_LOW = "LOW";
    public static final double HIGH_CONFIDENCE = 0.8;
    public static final double MEDIUM_CONFIDENCE = 0.6;

    public static final String WHITESPACE_REGEX = "\\s+";
    public static final String PARAGRAPH_REGEX = "\\n\\s*\\n";
    public static final String SENTENCE_ENDINGS_REGEX = "[.!?]+";

    // ML Constants
    public static final String K_UNKNOWN = "UNKNOWN";
    public static final String K_SENTIMENT = "SENTIMENT";
    public static final String K_TOPIC_MODELING = "TOPIC_MODELING";
    public static final String K_CLASSIFICATION = "CLASSIFICATION";

    public static final String K_EXCELLENT = "EXCELLENT";
    public static final String K_GOOD = "GOOD";
    public static final String K_FAIR = "FAIR";
    public static final String K_POOR = "POOR";
    public static final String K_VERY_POOR = "VERY_POOR";
}
