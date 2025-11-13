package com.kapil.verbametrics.services.classifiers;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
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
 * Test class for SentimentLabelClassifier.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SentimentLabelClassifierTest {

    @Mock
    private SentimentAnalysisProperties properties;

    @Mock
    private SentimentAnalysisProperties.Thresholds thresholds;

    private SentimentLabelClassifier classifier;

    @BeforeEach
    void setUp() {
        when(properties.getThresholds()).thenReturn(thresholds);
        when(thresholds.getPositive()).thenReturn(0.05);
        when(thresholds.getNegative()).thenReturn(-0.05);

        classifier = new SentimentLabelClassifier(properties);
    }

    @Test
    @DisplayName("determineSentimentLabel should return POSITIVE for score equal to positive threshold")
    void determineSentimentLabel_equalToPositiveThreshold_returnsPositive() {
        double score = 0.05;
        String label = classifier.determineSentimentLabel(score);
        assertEquals(VerbaMetricsConstants.POSITIVE, label, "Should return POSITIVE at threshold");
    }

    @Test
    @DisplayName("determineSentimentLabel should return NEGATIVE for score below negative threshold")
    void determineSentimentLabel_belowNegativeThreshold_returnsNegative() {
        double score = -0.1;
        String label = classifier.determineSentimentLabel(score);
        assertEquals(VerbaMetricsConstants.NEGATIVE, label, "Should return NEGATIVE");
        verify(thresholds).getNegative();
    }

    @Test
    @DisplayName("determineSentimentLabel should return NEGATIVE for score equal to negative threshold")
    void determineSentimentLabel_equalToNegativeThreshold_returnsNegative() {
        double score = -0.05;
        String label = classifier.determineSentimentLabel(score);
        assertEquals(VerbaMetricsConstants.NEGATIVE, label, "Should return NEGATIVE at threshold");
    }

    @Test
    @DisplayName("determineSentimentLabel should return NEUTRAL for score between thresholds")
    void determineSentimentLabel_betweenThresholds_returnsNeutral() {
        double score = 0.03;
        String label = classifier.determineSentimentLabel(score);
        assertEquals(VerbaMetricsConstants.NEUTRAL, label, "Should return NEUTRAL");
    }

    @Test
    @DisplayName("determineSentimentLabel should return NEUTRAL for score of zero")
    void determineSentimentLabel_zeroScore_returnsNeutral() {
        double score = 0.0;
        String label = classifier.determineSentimentLabel(score);
        assertEquals(VerbaMetricsConstants.NEUTRAL, label, "Score of 0 should return NEUTRAL");
    }

    @Test
    @DisplayName("determineSentimentLabel should use configured thresholds")
    void determineSentimentLabel_usesConfiguredThresholds() {
        when(thresholds.getPositive()).thenReturn(0.2);
        when(thresholds.getNegative()).thenReturn(-0.2);
        classifier = new SentimentLabelClassifier(properties);
        double neutralScore = 0.1;
        String label = classifier.determineSentimentLabel(neutralScore);
        assertEquals(VerbaMetricsConstants.NEUTRAL, label, "Should use configured thresholds");
    }

}
