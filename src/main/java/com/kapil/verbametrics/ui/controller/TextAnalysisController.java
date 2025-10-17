package com.kapil.verbametrics.ui.controller;

import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.BasicTextAnalysisService;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import com.kapil.verbametrics.ui.util.GuiConstants;

/**
 * Controller class to handle text analysis operations.
 *
 * @param basicService       the basic text analysis service
 * @param sentimentService   the sentiment analysis service
 * @param readabilityService the readability analysis service
 */
public record TextAnalysisController(BasicTextAnalysisService basicService, SentimentAnalysisService sentimentService,
                                     ReadabilityAnalysisService readabilityService) {

    /**
     * Analyze the given text and return the analysis results.
     *
     * @param text the text to analyze
     * @return the analysis results
     * @throws IllegalArgumentException if text is invalid
     */
    public AnalysisResult analyze(String text) {
        validateInput(text);
        TextAnalysisResponse basic = basicService.analyzeText(new TextAnalysisRequest(text));
        SentimentScore sentiment = sentimentService.analyzeSentiment(text);
        ReadabilityMetrics readability = readabilityService.analyzeReadability(text);
        return new AnalysisResult(basic, sentiment, readability);
    }

    /**
     * Validate the input text for analysis.
     *
     * @param text the text to validate
     * @throws IllegalArgumentException if text is invalid
     */
    private void validateInput(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }
        if (text.length() > GuiConstants.MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("Text too long (max 10,000 characters)");
        }
    }

    /**
     * Record to hold the analysis results.
     *
     * @param basic       the basic text analysis response
     * @param sentiment   the sentiment score
     * @param readability the readability metrics
     */
    public record AnalysisResult(TextAnalysisResponse basic, SentimentScore sentiment, ReadabilityMetrics readability) {

    }

}
