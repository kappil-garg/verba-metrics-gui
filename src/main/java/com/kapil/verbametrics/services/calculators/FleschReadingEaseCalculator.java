package com.kapil.verbametrics.services.calculators;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculator for Flesch Reading Ease score.
 * Handles the logic for calculating Flesch Reading Ease scores.
 *
 * @author Kapil Garg
 */
@Component
public class FleschReadingEaseCalculator {

    private final ReadabilityAnalysisProperties properties;

    @Autowired
    public FleschReadingEaseCalculator(ReadabilityAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Calculates the Flesch Reading Ease score.
     * Uses configurable coefficients from properties.
     *
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @return the Flesch Reading Ease score
     */
    public double calculateScore(double averageSentenceLength, double averageSyllablesPerWord) {
        return properties.getFleschReadingEase().getConstant() -
                (properties.getFleschReadingEase().getSentenceLengthMultiplier() * averageSentenceLength) -
                (properties.getFleschReadingEase().getSyllablesPerWordMultiplier() * averageSyllablesPerWord);
    }

}
