package com.kapil.verbametrics.services.calculators;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculator for Flesch-Kincaid Grade Level.
 * Handles the logic for calculating Flesch-Kincaid readability scores.
 *
 * @author Kapil Garg
 */
@Component
public class FleschKincaidCalculator {

    private final ReadabilityAnalysisProperties properties;

    @Autowired
    public FleschKincaidCalculator(ReadabilityAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Calculates the Flesch-Kincaid Grade Level.
     * Uses configurable coefficients from properties.
     *
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @return the Flesch-Kincaid score
     */
    public double calculateScore(double averageSentenceLength, double averageSyllablesPerWord) {
        return properties.getFleschKincaid().getSentenceLengthMultiplier() * averageSentenceLength +
                properties.getFleschKincaid().getSyllablesPerWordMultiplier() * averageSyllablesPerWord +
                properties.getFleschKincaid().getConstant();
    }

}
