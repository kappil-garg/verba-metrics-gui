package com.kapil.verbametrics.services.classifiers;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Classifier for complexity level based on Flesch Reading Ease score.
 * Handles the logic for determining complexity levels from scores.
 *
 * @author Kapil Garg
 */
@Component
public class ComplexityClassifier {

    private final ReadabilityAnalysisProperties properties;

    @Autowired
    public ComplexityClassifier(ReadabilityAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Determines the complexity level based on Flesch Reading Ease score.
     * Uses configurable thresholds from properties.
     *
     * @param fleschReadingEase the Flesch Reading Ease score
     * @return the complexity level
     */
    public String determineComplexity(double fleschReadingEase) {
        if (fleschReadingEase >= properties.getComplexityLevels().getVeryEasy()) {
            return VerbaMetricsConstants.K_VERY_EASY;
        } else if (fleschReadingEase >= properties.getComplexityLevels().getEasy()) {
            return VerbaMetricsConstants.K_EASY;
        } else if (fleschReadingEase >= properties.getComplexityLevels().getModerate()) {
            return VerbaMetricsConstants.K_MODERATE;
        } else if (fleschReadingEase >= properties.getComplexityLevels().getDifficult()) {
            return VerbaMetricsConstants.K_DIFFICULT;
        } else {
            return VerbaMetricsConstants.K_VERY_DIFFICULT;
        }
    }

}
