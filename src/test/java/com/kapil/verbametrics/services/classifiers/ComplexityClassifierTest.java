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
 * Test class for ComplexityClassifier.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ComplexityClassifierTest {

    @Mock
    private ReadabilityAnalysisProperties properties;

    @Mock
    private ReadabilityAnalysisProperties.ComplexityLevels thresholds;

    private ComplexityClassifier classifier;

    @BeforeEach
    void setUp() {
        when(properties.getComplexityLevels()).thenReturn(thresholds);
        when(thresholds.getVeryEasy()).thenReturn(90.0);
        when(thresholds.getEasy()).thenReturn(80.0);
        when(thresholds.getModerate()).thenReturn(60.0);
        when(thresholds.getDifficult()).thenReturn(30.0);
        classifier = new ComplexityClassifier(properties);
    }

    @Test
    @DisplayName("determineComplexity should return VERY_EASY at very easy threshold")
    void determineComplexity_atVeryEasyThreshold_returnsVeryEasy() {
        double score = 90.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_VERY_EASY, complexity, "Should return VERY_EASY at threshold");
    }

    @Test
    @DisplayName("determineComplexity should return EASY at easy threshold")
    void determineComplexity_atEasyThreshold_returnsEasy() {
        double score = 80.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_EASY, complexity, "Should return EASY at threshold");
    }

    @Test
    @DisplayName("determineComplexity should return MODERATE at moderate threshold")
    void determineComplexity_atModerateThreshold_returnsModerate() {
        double score = 60.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_MODERATE, complexity, "Should return MODERATE at threshold");
    }

    @Test
    @DisplayName("determineComplexity should return DIFFICULT at difficult threshold")
    void determineComplexity_atDifficultThreshold_returnsDifficult() {
        double score = 30.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_DIFFICULT, complexity, "Should return DIFFICULT at threshold");
    }

    @Test
    @DisplayName("determineComplexity should handle zero score")
    void determineComplexity_zeroScore_returnsVeryDifficult() {
        double score = 0.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_VERY_DIFFICULT, complexity, "Zero score should return VERY_DIFFICULT");
    }

    @Test
    @DisplayName("determineComplexity should use configured thresholds")
    void determineComplexity_usesConfiguredThresholds() {
        when(thresholds.getVeryEasy()).thenReturn(95.0);
        when(thresholds.getEasy()).thenReturn(85.0);
        when(thresholds.getModerate()).thenReturn(65.0);
        when(thresholds.getDifficult()).thenReturn(35.0);
        classifier = new ComplexityClassifier(properties);
        double score = 75.0;
        String complexity = classifier.determineComplexity(score);
        assertEquals(VerbaMetricsConstants.K_MODERATE, complexity, "Should use configured thresholds");
        verify(thresholds).getVeryEasy();
        verify(thresholds).getEasy();
        verify(thresholds).getModerate();
    }

}
