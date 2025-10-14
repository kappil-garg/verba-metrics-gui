package com.kapil.verbametrics.services;

import com.kapil.verbametrics.domain.SentimentScore;

/**
 * Service interface for sentiment analysis operations.
 *
 * @author Kapil Garg
 */
public interface SentimentAnalysisService {

    /**
     * Analyzes the sentiment of the given text.
     *
     * @param text the text to analyze
     * @return SentimentScore containing sentiment analysis results
     */
    SentimentScore analyzeSentiment(String text);

    /**
     * Analyzes the sentiment of the given text with custom parameters.
     *
     * @param text              the text to analyze
     * @param includeConfidence whether to include confidence analysis
     * @return SentimentScore containing sentiment analysis results
     */
    SentimentScore analyzeSentiment(String text, boolean includeConfidence);

}
