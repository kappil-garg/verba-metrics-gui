package com.kapil.verbametrics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for sentiment analysis.
 * Contains thresholds for sentiment classification, confidence levels, text processing options, and word lists.
 *
 * @author Kapil Garg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sentiment.analysis")
public class SentimentAnalysisProperties {

    /**
     * Word list configuration
     */
    private WordLists wordLists = new WordLists();

    /**
     * Sentiment thresholds for classification
     */
    private Thresholds thresholds = new Thresholds();

    /**
     * Text processing configuration
     */
    private TextProcessing textProcessing = new TextProcessing();

    /**
     * Confidence level thresholds
     */
    private ConfidenceLevels confidenceLevels = new ConfidenceLevels();

    @Data
    public static class WordLists {
        private String positiveWordsPath;
        private String negativeWordsPath;
        private List<String> positiveWords = List.of();
        private List<String> negativeWords = List.of();
    }

    @Data
    public static class Thresholds {
        private double positive = 0.1;
        private double negative = -0.1;
    }

    @Data
    public static class TextProcessing {
        private String wordSeparator = "\\W+";
        private boolean caseSensitive = false;
        private boolean removePunctuation = true;
    }

    @Data
    public static class ConfidenceLevels {
        private double high = 0.8;
        private double medium = 0.6;
    }

}
