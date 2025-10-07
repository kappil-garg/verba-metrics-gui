package com.kapil.verbametrics.services.classifiers;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Classifier for reading level based on Flesch-Kincaid score.
 * Handles the logic for determining reading levels from scores.
 *
 * @author Kapil Garg
 */
@Component
public class ReadingLevelClassifier {

    private final ReadabilityAnalysisProperties properties;

    @Autowired
    public ReadingLevelClassifier(ReadabilityAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Determines the reading level based on Flesch-Kincaid score.
     * Uses configurable thresholds from properties.
     *
     * @param fleschKincaidScore the Flesch-Kincaid score
     * @return the reading level
     */
    public String determineReadingLevel(double fleschKincaidScore) {
        if (fleschKincaidScore <= properties.getReadingLevels().getElementary()) {
            return VerbaMetricsConstants.K_ELEMENTARY;
        } else if (fleschKincaidScore <= properties.getReadingLevels().getMiddleSchool()) {
            return VerbaMetricsConstants.K_MIDDLE_SCHOOL;
        } else if (fleschKincaidScore <= properties.getReadingLevels().getHighSchool()) {
            return VerbaMetricsConstants.K_HIGH_SCHOOL;
        } else if (fleschKincaidScore <= properties.getReadingLevels().getCollege()) {
            return VerbaMetricsConstants.K_COLLEGE;
        } else {
            return VerbaMetricsConstants.K_GRADUATE;
        }
    }

}
