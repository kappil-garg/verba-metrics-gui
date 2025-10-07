package com.kapil.verbametrics.services.engines;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.services.WordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Business logic engine for sentiment score calculation.
 * Handles the core logic for calculating sentiment scores from text.
 *
 * @author Kapil Garg
 */
@Component
public class SentimentCalculationEngine {

    private final WordListService wordListService;
    private final SentimentAnalysisProperties analysisProperties;

    @Autowired
    public SentimentCalculationEngine(WordListService wordListService, SentimentAnalysisProperties analysisProperties) {
        this.wordListService = wordListService;
        this.analysisProperties = analysisProperties;
    }

    /**
     * Calculates the sentiment score based on positive and negative word sets.
     *
     * @param text the text to analyze
     * @return the sentiment score between -1.0 and 1.0
     */
    public double calculateSentimentScore(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        var tokens = tokenize(text);
        var totalWords = (int) Arrays.stream(tokens).filter(token -> !token.isBlank()).count();
        if (totalWords == 0) {
            return 0.0;
        }
        var positiveCount = countPositiveWords(tokens, wordListService.getPositiveWords());
        var negativeCount = countNegativeWords(tokens, wordListService.getNegativeWords());
        var positiveRatio = (double) positiveCount / totalWords;
        var negativeRatio = (double) negativeCount / totalWords;
        return positiveRatio - negativeRatio;
    }

    /**
     * Tokenizes the input text based on the configured word separator and case sensitivity.
     *
     * @param text the text to tokenize
     * @return an array of tokens
     */
    private String[] tokenize(String text) {
        boolean caseSensitive = analysisProperties.getTextProcessing().isCaseSensitive();
        String normalized = caseSensitive ? text.trim() : text.trim().toLowerCase();
        return normalized.split(analysisProperties.getTextProcessing().getWordSeparator());
    }

    /**
     * Counts the number of positive words in the token array.
     *
     * @param tokens        the array of tokens
     * @param positiveWords the set of positive words
     * @return the count of positive words
     */
    private int countPositiveWords(String[] tokens, java.util.Set<String> positiveWords) {
        var count = 0;
        for (var token : tokens) {
            if (!token.isBlank() && positiveWords.contains(token)) count++;
        }
        return count;
    }

    /**
     * Counts the number of negative words in the token array.
     *
     * @param tokens        the array of tokens
     * @param negativeWords the set of negative words
     * @return the count of negative words
     */
    private int countNegativeWords(String[] tokens, java.util.Set<String> negativeWords) {
        var count = 0;
        for (var token : tokens) {
            if (!token.isBlank() && negativeWords.contains(token)) count++;
        }
        return count;
    }

}
