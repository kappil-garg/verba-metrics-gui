package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import com.kapil.verbametrics.services.classifiers.SentimentLabelClassifier;
import com.kapil.verbametrics.services.engines.SentimentCalculationEngine;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of SentimentAnalysisService using calculation engine and label classifier.
 * Provides functionality to analyze text and return sentiment scores.
 *
 * @author Kapil Garg
 */
@Service
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalysisServiceImpl.class);

    private final SentimentCalculationEngine calculationEngine;
    private final SentimentLabelClassifier labelClassifier;
    private final SentimentAnalysisProperties sentimentProperties;

    @Autowired
    public SentimentAnalysisServiceImpl(SentimentCalculationEngine calculationEngine,
                                        SentimentLabelClassifier labelClassifier,
                                        SentimentAnalysisProperties sentimentProperties) {
        this.calculationEngine = calculationEngine;
        this.labelClassifier = labelClassifier;
        this.sentimentProperties = sentimentProperties;
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
                return new SentimentScore(VerbaMetricsConstants.NEUTRAL, 1.0, 0.0);
            }
            // Delegate to specialized engines
            double score = calculationEngine.calculateSentimentScore(text);
            String label = labelClassifier.determineSentimentLabel(score);
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
     * Calculates confidence score based on text length and sentiment score magnitude.
     *
     * @param text  the input text
     * @param score the calculated sentiment score
     * @return confidence score between 0.1 and the configured maximum confidence level
     */
    private double calculateConfidence(String text, double score) {
        int totalWords = text.split("\\s+").length;
        double scoreMagnitude = Math.abs(score);
        double baseConfidence = Math.min(scoreMagnitude * 2, 1.0);
        double lengthFactor = Math.min(totalWords / 10.0, 1.0);
        double confidence = Math.max(0.1, baseConfidence * lengthFactor);
        if (confidence >= sentimentProperties.getConfidenceLevels().getHigh()) {
            return sentimentProperties.getConfidenceLevels().getHigh();
        } else {
            return Math.min(confidence, sentimentProperties.getConfidenceLevels().getMedium());
        }
    }

}
