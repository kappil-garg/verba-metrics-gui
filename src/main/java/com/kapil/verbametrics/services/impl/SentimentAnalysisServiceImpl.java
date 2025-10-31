package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import com.kapil.verbametrics.services.classifiers.SentimentLabelClassifier;
import com.kapil.verbametrics.services.engines.SentimentCalculationEngine;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public SentimentAnalysisServiceImpl(SentimentCalculationEngine calculationEngine,
                                        SentimentLabelClassifier labelClassifier) {
        this.calculationEngine = calculationEngine;
        this.labelClassifier = labelClassifier;
    }

    /**
     * Analyzes the sentiment of the given text and returns a SentimentScore with confidence.
     *
     * @param text the text to analyze
     * @return the sentiment score with label and confidence
     */
    @Override
    public SentimentScore analyzeSentiment(String text) {
        return analyzeSentiment(text, true);
    }

    /**
     * Analyzes the sentiment of the given text and returns a SentimentScore.
     *
     * @param text              the text to analyze
     * @param includeConfidence whether to include confidence score
     * @return the sentiment score with label and confidence
     */
    @Override
    public SentimentScore analyzeSentiment(String text, boolean includeConfidence) {
        if (text == null || text.isBlank()) {
            return new SentimentScore(VerbaMetricsConstants.NEUTRAL, includeConfidence ? 1.0 : 0.0, 0.0);
        }
        LOGGER.debug("Starting sentiment analysis for text of length: {}", text.length());
        try {
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
     * Calculates confidence score based on sentiment score magnitude and text length.
     *
     * @param text  the input text
     * @param score the calculated sentiment score
     * @return confidence score between 0.1 and 1.0
     */
    private double calculateConfidence(String text, double score) {
        int totalWords = Math.max(1, text.split("\\s+").length);
        double scoreMagnitude = Math.abs(score);
        double sentimentConfidence = Math.min(0.9, 0.1 + (scoreMagnitude * 0.8));
        double lengthFactor = Math.min(0.8, 0.3 + (totalWords / 15.0));
        double finalConfidence = sentimentConfidence * lengthFactor;
        return Math.max(0.1, Math.min(0.95, finalConfidence));
    }

}
