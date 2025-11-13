package com.kapil.verbametrics.services.calculators;

import com.kapil.verbametrics.config.ReadabilityAnalysisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for FleschKincaidCalculator.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class FleschKincaidCalculatorTest {

    @Mock
    private ReadabilityAnalysisProperties properties;

    @Mock
    private ReadabilityAnalysisProperties.FleschKincaid formulaConfig;

    private FleschKincaidCalculator calculator;

    @BeforeEach
    void setUp() {
        when(properties.getFleschKincaid()).thenReturn(formulaConfig);
        when(formulaConfig.getSentenceLengthMultiplier()).thenReturn(0.39);
        when(formulaConfig.getSyllablesPerWordMultiplier()).thenReturn(11.8);
        when(formulaConfig.getConstant()).thenReturn(-15.59);
        calculator = new FleschKincaidCalculator(properties);
    }

    @Test
    @DisplayName("calculateScore should compute Flesch-Kincaid score with standard coefficients")
    void calculateScore_standardCoefficients_computesCorrectly() {
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 1.5;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(6.01, score, 0.01, "Should calculate correct Flesch-Kincaid score");
    }

    @Test
    @DisplayName("calculateScore should handle zero sentence length")
    void calculateScore_zeroSentenceLength_computesCorrectly() {
        double avgSentenceLength = 0.0;
        double avgSyllablesPerWord = 1.5;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(2.11, score, 0.01, "Should handle zero sentence length");
    }

    @Test
    @DisplayName("calculateScore should handle zero syllables per word")
    void calculateScore_zeroSyllablesPerWord_computesCorrectly() {
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 0.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(-11.69, score, 0.01, "Should handle zero syllables per word");
    }

    @Test
    @DisplayName("calculateScore should handle both parameters as zero")
    void calculateScore_bothZero_returnsConstant() {
        double avgSentenceLength = 0.0;
        double avgSyllablesPerWord = 0.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(-15.59, score, 0.01, "Should return just the constant");
    }

    @Test
    @DisplayName("calculateScore should use configured coefficients")
    void calculateScore_usesConfiguredCoefficients() {
        when(formulaConfig.getSentenceLengthMultiplier()).thenReturn(0.5);
        when(formulaConfig.getSyllablesPerWordMultiplier()).thenReturn(10.0);
        when(formulaConfig.getConstant()).thenReturn(-20.0);
        calculator = new FleschKincaidCalculator(properties);
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 2.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(5.0, score, 0.01, "Should use configured coefficients");
        verify(formulaConfig).getSentenceLengthMultiplier();
        verify(formulaConfig).getSyllablesPerWordMultiplier();
        verify(formulaConfig).getConstant();
    }

    @Test
    @DisplayName("calculateScore should handle high complexity text")
    void calculateScore_highComplexity_returnsHighScore() {
        double avgSentenceLength = 25.0;
        double avgSyllablesPerWord = 3.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(score > 15, "High complexity should give high grade level");
    }

    @Test
    @DisplayName("calculateScore should handle low complexity text")
    void calculateScore_lowComplexity_returnsLowScore() {
        double avgSentenceLength = 5.0;
        double avgSyllablesPerWord = 1.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(score < 5, "Low complexity should give low grade level");
    }

    @Test
    @DisplayName("calculateScore should handle decimal inputs")
    void calculateScore_decimalInputs_computesPrecisely() {
        double avgSentenceLength = 12.5;
        double avgSyllablesPerWord = 1.75;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(Double.isFinite(score), "Score should be finite");
    }

    @Test
    @DisplayName("calculateScore should be deterministic")
    void calculateScore_sameInputs_returnsSameScore() {
        double avgSentenceLength = 15.0;
        double avgSyllablesPerWord = 2.0;
        double score1 = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        double score2 = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(score1, score2, "Same inputs should produce same score");
    }

    @Test
    @DisplayName("calculateScore should handle negative coefficients")
    void calculateScore_negativeCoefficients_computesCorrectly() {
        when(formulaConfig.getSentenceLengthMultiplier()).thenReturn(-0.5);
        when(formulaConfig.getSyllablesPerWordMultiplier()).thenReturn(-10.0);
        when(formulaConfig.getConstant()).thenReturn(50.0);
        calculator = new FleschKincaidCalculator(properties);
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 2.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(25.0, score, 0.01, "Should handle negative coefficients");
    }

    @Test
    @DisplayName("calculateScore should handle large input values")
    void calculateScore_largeValues_computesWithoutOverflow() {
        double avgSentenceLength = 100.0;
        double avgSyllablesPerWord = 10.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(Double.isFinite(score), "Should handle large values without overflow");
        assertFalse(Double.isNaN(score), "Score should not be NaN");
    }

    @Test
    @DisplayName("calculateScore should handle very small input values")
    void calculateScore_verySmallValues_computesCorrectly() {
        double avgSentenceLength = 0.1;
        double avgSyllablesPerWord = 0.1;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(Double.isFinite(score), "Should handle very small values");
        assertFalse(Double.isNaN(score), "Score should not be NaN");
    }

}
