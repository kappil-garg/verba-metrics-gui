package com.kapil.verbametrics.services.calculators;

import org.springframework.stereotype.Component;

/**
 * Calculator for sentence length analysis.
 * Handles the logic for calculating average sentence length.
 *
 * @author Kapil Garg
 */
@Component
public class SentenceLengthCalculator {

    /**
     * Calculates the average sentence length in words.
     *
     * @param text the text to analyze
     * @return the average sentence length
     */
    public double calculateAverageSentenceLength(String text) {
        String[] sentences = text.split("[.!?]+");
        if (sentences.length == 0) return 0.0;
        int totalWords = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                totalWords += sentence.trim().split("\\s+").length;
            }
        }
        return (double) totalWords / sentences.length;
    }

}
