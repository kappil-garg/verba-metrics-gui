package com.kapil.verbametrics.services.classifiers;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for ReadingLevelClassifier.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReadingLevelClassifierTest {

    @Mock
    private ReadabilityAnalysisProperties properties;

    @Mock
    private ReadabilityAnalysisProperties.ReadingLevels thresholds;

    private ReadingLevelClassifier classifier;

    @BeforeEach
    void setUp() {
        when(properties.getReadingLevels()).thenReturn(thresholds);
        when(thresholds.getElementary()).thenReturn(5.0);
        when(thresholds.getMiddleSchool()).thenReturn(8.0);
        when(thresholds.getHighSchool()).thenReturn(12.0);
        when(thresholds.getCollege()).thenReturn(16.0);
        classifier = new ReadingLevelClassifier(properties);
    }

    @Test
    @DisplayName("determineReadingLevel should return ELEMENTARY at elementary threshold")
    void determineReadingLevel_atElementaryThreshold_returnsElementary() {
        double score = 5.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_ELEMENTARY, level, "Should return ELEMENTARY at threshold");
    }

    @Test
    @DisplayName("determineReadingLevel should return MIDDLE_SCHOOL at middle school threshold")
    void determineReadingLevel_atMiddleSchoolThreshold_returnsMiddleSchool() {
        double score = 8.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_MIDDLE_SCHOOL, level, "Should return MIDDLE_SCHOOL at threshold");
    }

    @Test
    @DisplayName("determineReadingLevel should return HIGH_SCHOOL at high school threshold")
    void determineReadingLevel_atHighSchoolThreshold_returnsHighSchool() {
        double score = 12.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_HIGH_SCHOOL, level, "Should return HIGH_SCHOOL at threshold");
    }

    @Test
    @DisplayName("determineReadingLevel should return COLLEGE at college threshold")
    void determineReadingLevel_atCollegeThreshold_returnsCollege() {
        double score = 16.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_COLLEGE, level, "Should return COLLEGE at threshold");
    }

    @Test
    @DisplayName("determineReadingLevel should return GRADUATE for scores above college")
    void determineReadingLevel_aboveCollege_returnsGraduate() {
        double score = 18.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_GRADUATE, level, "Should return GRADUATE");
    }

    @Test
    @DisplayName("determineReadingLevel should handle zero score")
    void determineReadingLevel_zeroScore_returnsElementary() {
        double score = 0.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_ELEMENTARY, level, "Zero score should return ELEMENTARY");
    }

    @Test
    @DisplayName("determineReadingLevel should use configured thresholds")
    void determineReadingLevel_usesConfiguredThresholds() {
        when(thresholds.getElementary()).thenReturn(6.0);
        when(thresholds.getMiddleSchool()).thenReturn(10.0);
        when(thresholds.getHighSchool()).thenReturn(14.0);
        when(thresholds.getCollege()).thenReturn(18.0);
        classifier = new ReadingLevelClassifier(properties);
        double score = 9.0;
        String level = classifier.determineReadingLevel(score);
        assertEquals(VerbaMetricsConstants.K_MIDDLE_SCHOOL, level, "Should use configured thresholds");
        verify(thresholds).getElementary();
        verify(thresholds).getMiddleSchool();
    }

}
