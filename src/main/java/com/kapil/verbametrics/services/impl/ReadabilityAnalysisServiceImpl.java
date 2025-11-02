package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import com.kapil.verbametrics.services.calculators.FleschKincaidCalculator;
import com.kapil.verbametrics.services.calculators.FleschReadingEaseCalculator;
import com.kapil.verbametrics.services.calculators.SentenceLengthCalculator;
import com.kapil.verbametrics.services.calculators.SyllablePerWordCalculator;
import com.kapil.verbametrics.services.classifiers.ComplexityClassifier;
import com.kapil.verbametrics.services.classifiers.ReadingLevelClassifier;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of ReadabilityAnalysisService using various calculators and classifiers.
 * Provides functionality to analyze text and return readability metrics.
 *
 * @author Kapil Garg
 */
@Service
public class ReadabilityAnalysisServiceImpl implements ReadabilityAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadabilityAnalysisServiceImpl.class);

    private final SentenceLengthCalculator sentenceLengthCalculator;
    private final SyllablePerWordCalculator syllablePerWordCalculator;
    private final FleschKincaidCalculator fleschKincaidCalculator;
    private final FleschReadingEaseCalculator fleschReadingEaseCalculator;
    private final ReadingLevelClassifier readingLevelClassifier;
    private final ComplexityClassifier complexityClassifier;

    @Autowired
    public ReadabilityAnalysisServiceImpl(SentenceLengthCalculator sentenceLengthCalculator,
                                          SyllablePerWordCalculator syllablePerWordCalculator,
                                          FleschKincaidCalculator fleschKincaidCalculator,
                                          FleschReadingEaseCalculator fleschReadingEaseCalculator,
                                          ReadingLevelClassifier readingLevelClassifier,
                                          ComplexityClassifier complexityClassifier) {
        this.sentenceLengthCalculator = sentenceLengthCalculator;
        this.syllablePerWordCalculator = syllablePerWordCalculator;
        this.fleschKincaidCalculator = fleschKincaidCalculator;
        this.fleschReadingEaseCalculator = fleschReadingEaseCalculator;
        this.readingLevelClassifier = readingLevelClassifier;
        this.complexityClassifier = complexityClassifier;
    }

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
            ReadabilityMetrics result = computeMetrics(text, includeComplexity);
            LOGGER.debug("Readability analysis completed: {}", result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to analyze readability", e);
            throw new RuntimeException("Failed to analyze readability: " + e.getMessage(), e);
        }
    }

    /**
     * Computes the readability metrics based on the provided text.
     *
     * @param text              the text to analyze
     * @param includeComplexity whether to include complexity classification
     * @return the computed readability metrics
     */
    private ReadabilityMetrics computeMetrics(String text, boolean includeComplexity) {
        double averageSentenceLength = sentenceLengthCalculator.calculateAverageSentenceLength(text);
        double averageSyllablesPerWord = syllablePerWordCalculator.calculateAverageSyllablesPerWord(text);
        double fleschKincaidScore = fleschKincaidCalculator.calculateScore(averageSentenceLength, averageSyllablesPerWord);
        double fleschReadingEaseRaw = fleschReadingEaseCalculator.calculateScore(averageSentenceLength, averageSyllablesPerWord);
        // Clamp FRE to [0, 100] for reporting/validation consistency
        double fleschReadingEase = Math.max(VerbaMetricsConstants.FLESCH_READING_EASE_MIN, Math.min(VerbaMetricsConstants.FLESCH_READING_EASE_MAX, fleschReadingEaseRaw));
        String readingLevel = readingLevelClassifier.determineReadingLevel(fleschKincaidScore);
        String complexity = includeComplexity ? complexityClassifier.determineComplexity(fleschReadingEase) : "Unknown";
        return new ReadabilityMetrics(
                fleschKincaidScore, fleschReadingEase, readingLevel, complexity,
                averageSentenceLength, averageSyllablesPerWord
        );
    }

}
