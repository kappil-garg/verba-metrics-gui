package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of ReadabilityAnalysisService that provides readability analysis functionalities.
 *
 * @author Kapil Garg
 */
@Service
public class ReadabilityAnalysisServiceImpl implements ReadabilityAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadabilityAnalysisServiceImpl.class.getName());

    @Override
    public ReadabilityMetrics analyzeReadability(String text) {
        return analyzeReadability(text, true);
    }

    @Override
    public ReadabilityMetrics analyzeReadability(String text, boolean includeComplexity) {

        Objects.requireNonNull(text, "Text cannot be null");
        LOGGER.debug("Starting readability analysis for text of length: {}", text.length());

        try {
            if (text.isBlank()) {
                return new ReadabilityMetrics(0.0, 100.0, "Elementary", "Very Easy", 0.0, 0.0);
            }
            double averageSentenceLength = calculateAverageSentenceLength(text);
            double averageSyllablesPerWord = calculateAverageSyllablesPerWord(text);
            ReadabilityMetrics result = getReadabilityMetrics(includeComplexity, averageSentenceLength, averageSyllablesPerWord);
            LOGGER.debug("Readability analysis completed: {}", result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to analyze readability", e);
            throw new RuntimeException("Failed to analyze readability: " + e.getMessage(), e);
        }

    }

    /**
     * Calculates the average sentence length in words.
     *
     * @param text the text to analyze
     * @return the average sentence length
     */
    private double calculateAverageSentenceLength(String text) {
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

    /**
     * Calculates the average number of syllables per word.
     *
     * @param text the text to analyze
     * @return the average syllables per word
     */
    private double calculateAverageSyllablesPerWord(String text) {
        String[] words = text.split("\\s+");
        if (words.length == 0) return 0.0;
        int totalSyllables = 0;
        for (String word : words) {
            if (!word.trim().isEmpty()) {
                totalSyllables += countSyllables(word);
            }
        }
        return (double) totalSyllables / words.length;
    }

    /**
     * Counts the number of syllables in a word.
     *
     * @param word the word to analyze
     * @return the number of syllables
     */
    private int countSyllables(String word) {

        if (word == null || word.isEmpty()) return 0;

        String cleanWord = word.toLowerCase().replaceAll("[^a-z]", "");
        if (cleanWord.isEmpty()) return 0;

        int syllables = 0;
        boolean previousWasVowel = false;

        for (int i = 0; i < cleanWord.length(); i++) {
            boolean isVowel = isVowel(cleanWord.charAt(i));
            if (isVowel && !previousWasVowel) {
                syllables++;
            }
            previousWasVowel = isVowel;
        }

        // Handle silent 'e' at the end
        if (cleanWord.endsWith("e") && syllables > 1) {
            syllables--;
        }

        return Math.max(1, syllables);

    }

    /**
     * Checks if a character is a vowel.
     *
     * @param c the character to check
     * @return true if the character is a vowel
     */
    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    /**
     * Computes the readability metrics based on average sentence length and syllables per word.
     *
     * @param includeComplexity       whether to include complexity analysis
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @return the computed ReadabilityMetrics
     */
    private ReadabilityMetrics getReadabilityMetrics(boolean includeComplexity, double averageSentenceLength, double averageSyllablesPerWord) {
        double fleschKincaidScore = calculateFleschKincaidScore(averageSentenceLength, averageSyllablesPerWord);
        double fleschReadingEase = calculateFleschReadingEase(averageSentenceLength, averageSyllablesPerWord);
        String readingLevel = determineReadingLevel(fleschKincaidScore);
        String complexity = includeComplexity ? determineComplexity(fleschReadingEase) : "Unknown";
        return new ReadabilityMetrics(
                fleschKincaidScore, fleschReadingEase, readingLevel, complexity,
                averageSentenceLength, averageSyllablesPerWord
        );
    }

    /**
     * Calculates the Flesch-Kincaid Grade Level.
     *
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @return the Flesch-Kincaid score
     */
    private double calculateFleschKincaidScore(double averageSentenceLength, double averageSyllablesPerWord) {
        return 0.39 * averageSentenceLength + 11.8 * averageSyllablesPerWord - 15.59;
    }

    /**
     * Calculates the Flesch Reading Ease score.
     *
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @return the Flesch Reading Ease score
     */
    private double calculateFleschReadingEase(double averageSentenceLength, double averageSyllablesPerWord) {
        return 206.835 - (1.015 * averageSentenceLength) - (84.6 * averageSyllablesPerWord);
    }

    /**
     * Determines the reading level based on Flesch-Kincaid score.
     *
     * @param fleschKincaidScore the Flesch-Kincaid score
     * @return the reading level
     */
    private String determineReadingLevel(double fleschKincaidScore) {
        if (fleschKincaidScore <= 6) {
            return "Elementary";
        } else if (fleschKincaidScore <= 9) {
            return "Middle School";
        } else if (fleschKincaidScore <= 12) {
            return "High School";
        } else if (fleschKincaidScore <= 16) {
            return "College";
        } else {
            return "Graduate";
        }
    }

    /**
     * Determines the complexity level based on Flesch Reading Ease score.
     *
     * @param fleschReadingEase the Flesch Reading Ease score
     * @return the complexity level
     */
    private String determineComplexity(double fleschReadingEase) {
        if (fleschReadingEase >= 80) {
            return "Very Easy";
        } else if (fleschReadingEase >= 60) {
            return "Easy";
        } else if (fleschReadingEase >= 40) {
            return "Moderate";
        } else if (fleschReadingEase >= 20) {
            return "Difficult";
        } else {
            return "Very Difficult";
        }
    }

}
