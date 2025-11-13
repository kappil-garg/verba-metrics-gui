package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.services.classifiers.SentimentLabelClassifier;
import com.kapil.verbametrics.services.engines.SentimentCalculationEngine;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SentimentAnalysisServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class SentimentAnalysisServiceImplTest {

    @Mock
    private SentimentCalculationEngine calculationEngine;

    @Mock
    private SentimentLabelClassifier labelClassifier;

    private SentimentAnalysisServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SentimentAnalysisServiceImpl(calculationEngine, labelClassifier);
    }

    @Test
    @DisplayName("analyzeSentiment with positive text should return positive sentiment")
    void analyzeSentiment_positiveText_returnsPositiveSentiment() {
        String text = "This is wonderful and amazing!";
        double score = 0.75;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.POSITIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result, "Result should not be null");
        assertEquals(VerbaMetricsConstants.POSITIVE, result.label(), "Label should be POSITIVE");
        assertEquals(0.75, result.score(), 0.01, "Score should be 0.75");
        assertTrue(result.confidence() > 0.0 && result.confidence() <= 1.0,
                "Confidence should be between 0 and 1");
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(score);
    }

    @Test
    @DisplayName("analyzeSentiment with negative text should return negative sentiment")
    void analyzeSentiment_negativeText_returnsNegativeSentiment() {
        String text = "This is terrible and awful.";
        double score = -0.65;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.NEGATIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.NEGATIVE, result.label());
        assertEquals(-0.65, result.score(), 0.01);
        assertTrue(result.confidence() > 0.0);
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(score);
    }

    @Test
    @DisplayName("analyzeSentiment with neutral text should return neutral sentiment")
    void analyzeSentiment_neutralText_returnsNeutralSentiment() {
        String text = "The weather is normal today.";
        double score = 0.05;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.NEUTRAL);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.NEUTRAL, result.label());
        assertEquals(0.05, result.score(), 0.01);
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(score);
    }

    @Test
    @DisplayName("analyzeSentiment with null text should return neutral with confidence 1.0")
    void analyzeSentiment_nullText_returnsNeutral() {
        SentimentScore result = service.analyzeSentiment(null);
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.NEUTRAL, result.label());
        assertEquals(1.0, result.confidence(), 0.01);
        assertEquals(0.0, result.score(), 0.01);
        verifyNoInteractions(calculationEngine, labelClassifier);
    }

    @Test
    @DisplayName("analyzeSentiment with blank text should return neutral")
    void analyzeSentiment_blankText_returnsNeutral() {
        SentimentScore result = service.analyzeSentiment("   ");
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.NEUTRAL, result.label());
        assertEquals(1.0, result.confidence(), 0.01);
        assertEquals(0.0, result.score(), 0.01);
        verifyNoInteractions(calculationEngine, labelClassifier);
    }

    @Test
    @DisplayName("analyzeSentiment without confidence should not calculate confidence")
    void analyzeSentiment_withoutConfidence_skipsConfidenceCalculation() {
        String text = "Good product";
        double score = 0.5;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.POSITIVE);
        SentimentScore result = service.analyzeSentiment(text, false);
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.POSITIVE, result.label());
        assertEquals(1.0, result.confidence(), 0.01, "Confidence should default to 1.0");
        assertEquals(0.5, result.score(), 0.01);
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(score);
    }

    @Test
    @DisplayName("analyzeSentiment with single parameter should include confidence")
    void analyzeSentiment_singleParameter_includesConfidence() {
        String text = "Excellent service";
        double score = 0.8;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.POSITIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertTrue(result.confidence() > 0.1 && result.confidence() <= 0.95,
                "Confidence should be calculated and within range");
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(score);
    }

    @Test
    @DisplayName("analyzeSentiment should wrap and propagate engine exceptions")
    void analyzeSentiment_engineThrowsException_wrapsException() {
        String text = "Test text";
        RuntimeException originalException = new RuntimeException("Engine error");
        when(calculationEngine.calculateSentimentScore(text)).thenThrow(originalException);
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> service.analyzeSentiment(text),
                "Should wrap and throw RuntimeException");
        assertTrue(thrown.getMessage().contains("Failed to analyze sentiment"),
                "Exception message should indicate sentiment analysis failure");
        assertEquals(originalException, thrown.getCause(),
                "Should preserve original exception as cause");
        verify(calculationEngine).calculateSentimentScore(text);
    }

    @Test
    @DisplayName("analyzeSentiment with very long text should handle correctly")
    void analyzeSentiment_longText_handlesCorrectly() {
        String text = "word ".repeat(100);
        double score = 0.6;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.POSITIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.POSITIVE, result.label());
        assertTrue(result.confidence() >= 0.1 && result.confidence() <= 0.95);
        verify(calculationEngine).calculateSentimentScore(text);
    }

    @Test
    @DisplayName("analyzeSentiment with extreme positive score should handle correctly")
    void analyzeSentiment_extremePositiveScore_handlesCorrectly() {
        String text = "Absolutely perfect incredible outstanding";
        double score = 1.0;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.POSITIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertEquals(1.0, result.score(), 0.01);
        assertTrue(result.confidence() <= 0.95, "Confidence should be capped at 0.95");
        verify(labelClassifier).determineSentimentLabel(1.0);
    }

    @Test
    @DisplayName("analyzeSentiment with extreme negative score should handle correctly")
    void analyzeSentiment_extremeNegativeScore_handlesCorrectly() {
        String text = "Horrible terrible awful disaster";
        double score = -1.0;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(score);
        when(labelClassifier.determineSentimentLabel(score)).thenReturn(VerbaMetricsConstants.NEGATIVE);
        SentimentScore result = service.analyzeSentiment(text);
        assertNotNull(result);
        assertEquals(-1.0, result.score(), 0.01);
        assertTrue(result.confidence() >= 0.1, "Confidence should be at least 0.1");
        verify(labelClassifier).determineSentimentLabel(-1.0);
    }

    @Test
    @DisplayName("Constructor should accept required dependencies")
    void constructor_withValidDependencies_createsInstance() {
        assertDoesNotThrow(() -> new SentimentAnalysisServiceImpl(
                calculationEngine,
                labelClassifier
        ), "Constructor should accept valid dependencies");
    }

    @Test
    @DisplayName("analyzeSentiment should call classifier with engine score")
    void analyzeSentiment_callsClassifierWithEngineScore() {
        String text = "Test";
        double expectedScore = 0.42;
        when(calculationEngine.calculateSentimentScore(text)).thenReturn(expectedScore);
        when(labelClassifier.determineSentimentLabel(expectedScore)).thenReturn(VerbaMetricsConstants.NEUTRAL);
        service.analyzeSentiment(text);
        verify(calculationEngine).calculateSentimentScore(text);
        verify(labelClassifier).determineSentimentLabel(expectedScore);
    }

    @Test
    @DisplayName("analyzeSentiment with empty string should return neutral")
    void analyzeSentiment_emptyString_returnsNeutral() {
        SentimentScore result = service.analyzeSentiment("");
        assertNotNull(result);
        assertEquals(VerbaMetricsConstants.NEUTRAL, result.label());
        assertEquals(0.0, result.score());
        verifyNoInteractions(calculationEngine);
    }

}
