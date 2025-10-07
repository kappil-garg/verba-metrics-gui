package com.kapil.verbametrics.services.calculators;

import com.kapil.verbametrics.services.SyllableCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculator for syllables per word analysis.
 * Handles the logic for calculating average syllables per word.
 *
 * @author Kapil Garg
 */
@Component
public class SyllablePerWordCalculator {

    private final SyllableCounterService syllableCounterService;

    @Autowired
    public SyllablePerWordCalculator(SyllableCounterService syllableCounterService) {
        this.syllableCounterService = syllableCounterService;
    }

    /**
     * Calculates the average number of syllables per word.
     *
     * @param text the text to analyze
     * @return the average syllables per word
     */
    public double calculateAverageSyllablesPerWord(String text) {
        String[] words = text.split("\\s+");
        if (words.length == 0) return 0.0;
        return syllableCounterService.calculateAverageSyllablesPerWord(words);
    }

}
