package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.config.SentimentWordProperties;
import com.kapil.verbametrics.constants.SentimentConstants;
import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of SentimentAnalysisService that provides sentiment analysis functionalities.
 *
 * @author Kapil Garg
 */
@Service
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalysisServiceImpl.class.getName());

    private final SentimentWordProperties wordProperties;

    @Autowired
    public SentimentAnalysisServiceImpl(SentimentWordProperties wordProperties) {
        this.wordProperties = wordProperties;
    }

    @Override
    public SentimentScore analyzeSentiment(String text) {
        return analyzeSentiment(text, true);
    }

    @Override
    public SentimentScore analyzeSentiment(String text, boolean includeConfidence) {

        Objects.requireNonNull(text, "Text cannot be null");
        LOGGER.debug("Starting sentiment analysis for text of length: {}", text.length());

        try {
            if (text.isBlank()) {
                return new SentimentScore(SentimentConstants.NEUTRAL, 1.0, 0.0);
            }
            double score = calculateSentimentScore(text);
            String label = determineSentimentLabel(score);
            double confidence = includeConfidence ? calculateConfidence(text, score) : 1.0;
            SentimentScore result = new SentimentScore(label, confidence, score);
            LOGGER.debug("Sentiment analysis completed: {}", result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to analyze sentiment", e);
            throw new RuntimeException("Failed to analyze sentiment: " + e.getMessage(), e);
        }

    }

    /**
     * Calculates the sentiment score based on positive and negative word sets.
     *
     * @param text the text to analyze
     * @return the sentiment score between -1.0 and 1.0
     */
    private double calculateSentimentScore(String text) {

        if (text == null || text.isBlank()) return 0.0;

        String normalized = text.trim().toLowerCase();
        String[] tokens = normalized.split("\\W+");

        Set<String> positiveWords = getPositiveWordSet();
        Set<String> negativeWords = getNegativeWordSet();

        int totalWords = 0;
        int positiveCount = 0;
        int negativeCount = 0;

        for (String token : tokens) {
            if (token.isBlank()) continue;
            totalWords++;
            if (positiveWords.contains(token)) positiveCount++;
            if (negativeWords.contains(token)) negativeCount++;
        }

        if (totalWords == 0) return 0.0;

        double positiveRatio = (double) positiveCount / totalWords;
        double negativeRatio = (double) negativeCount / totalWords;

        return positiveRatio - negativeRatio;

    }

    /**
     * Retrieves the set of positive words from configuration.
     *
     * @return a set of positive words
     */
    private Set<String> getPositiveWordSet() {
        return new HashSet<>(wordProperties.getPositiveWords().stream().map(String::toLowerCase).toList());
    }

    /**
     * Retrieves the set of negative words from configuration.
     *
     * @return a set of negative words
     */
    private Set<String> getNegativeWordSet() {
        return new HashSet<>(wordProperties.getNegativeWords().stream().map(String::toLowerCase).toList());
    }

    /**
     * Determines the sentiment label based on the score.
     *
     * @param score the sentiment score
     * @return the sentiment label
     */
    private String determineSentimentLabel(double score) {
        if (score > SentimentConstants.POSITIVE_THRESHOLD) {
            return SentimentConstants.POSITIVE;
        } else if (score < SentimentConstants.NEGATIVE_THRESHOLD) {
            return SentimentConstants.NEGATIVE;
        } else {
            return SentimentConstants.NEUTRAL;
        }
    }

    /**
     * Calculates the confidence level based on the text characteristics.
     *
     * @param text  the text analyzed
     * @param score the sentiment score
     * @return the confidence level between 0.0 and 1.0
     */
    private double calculateConfidence(String text, double score) {
        int totalWords = text.split("\\s+").length;
        double scoreMagnitude = Math.abs(score);
        // Base confidence on score magnitude and text length
        double baseConfidence = Math.min(scoreMagnitude * 2, 1.0);
        double lengthFactor = Math.min(totalWords / 10.0, 1.0);
        return Math.max(0.1, baseConfidence * lengthFactor);
    }

}
