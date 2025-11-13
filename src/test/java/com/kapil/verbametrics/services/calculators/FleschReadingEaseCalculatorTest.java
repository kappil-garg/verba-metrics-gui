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
 * Test class for FleschReadingEaseCalculator.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class FleschReadingEaseCalculatorTest {

    @Mock
    private ReadabilityAnalysisProperties properties;

    @Mock
    private ReadabilityAnalysisProperties.FleschReadingEase formulaConfig;

    private FleschReadingEaseCalculator calculator;

    @BeforeEach
    void setUp() {
        when(properties.getFleschReadingEase()).thenReturn(formulaConfig);
        when(formulaConfig.getConstant()).thenReturn(206.835);
        when(formulaConfig.getSentenceLengthMultiplier()).thenReturn(1.015);
        when(formulaConfig.getSyllablesPerWordMultiplier()).thenReturn(84.6);
        calculator = new FleschReadingEaseCalculator(properties);
    }

    @Test
    @DisplayName("calculateScore should compute Flesch Reading Ease with standard coefficients")
    void calculateScore_standardCoefficients_computesCorrectly() {
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 1.5;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(69.785, score, 0.01, "Should calculate correct Flesch Reading Ease score");
    }

    @Test
    @DisplayName("calculateScore should handle zero sentence length")
    void calculateScore_zeroSentenceLength_computesCorrectly() {
        double avgSentenceLength = 0.0;
        double avgSyllablesPerWord = 1.5;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(79.935, score, 0.01, "Should handle zero sentence length");
    }

    @Test
    @DisplayName("calculateScore should handle zero syllables per word")
    void calculateScore_zeroSyllablesPerWord_computesCorrectly() {
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 0.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(196.685, score, 0.01, "Should handle zero syllables per word");
    }

    @Test
    @DisplayName("calculateScore should handle both parameters as zero")
    void calculateScore_bothZero_returnsConstant() {
        double avgSentenceLength = 0.0;
        double avgSyllablesPerWord = 0.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(206.835, score, 0.01, "Should return just the constant");
    }

    @Test
    @DisplayName("calculateScore should use configured coefficients")
    void calculateScore_usesConfiguredCoefficients() {
        when(formulaConfig.getConstant()).thenReturn(200.0);
        when(formulaConfig.getSentenceLengthMultiplier()).thenReturn(1.0);
        when(formulaConfig.getSyllablesPerWordMultiplier()).thenReturn(80.0);
        calculator = new FleschReadingEaseCalculator(properties);
        double avgSentenceLength = 10.0;
        double avgSyllablesPerWord = 2.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertEquals(30.0, score, 0.01, "Should use configured coefficients");
        verify(formulaConfig).getConstant();
        verify(formulaConfig).getSentenceLengthMultiplier();
        verify(formulaConfig).getSyllablesPerWordMultiplier();
    }

    @Test
    @DisplayName("calculateScore should handle easy text (high score)")
    void calculateScore_easyText_returnsHighScore() {
        double avgSentenceLength = 5.0;
        double avgSyllablesPerWord = 1.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(score > 90, "Easy text should have high Flesch Reading Ease score");
    }

    @Test
    @DisplayName("calculateScore should handle difficult text (low score)")
    void calculateScore_difficultText_returnsLowScore() {
        double avgSentenceLength = 25.0;
        double avgSyllablesPerWord = 3.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(score < 30, "Difficult text should have low Flesch Reading Ease score");
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
    @DisplayName("calculateScore should allow negative scores for very difficult text")
    void calculateScore_veryDifficult_allowsNegativeScore() {
        double avgSentenceLength = 30.0;
        double avgSyllablesPerWord = 4.0;
        double score = calculator.calculateScore(avgSentenceLength, avgSyllablesPerWord);
        assertTrue(Double.isFinite(score), "Should allow negative scores");
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

    @Test
    @DisplayName("calculateScore inversely correlates with text difficulty")
    void calculateScore_inverselyCorrelatesWithDifficulty() {
        double simpleTextAvgSentLen = 8.0;
        double complexTextAvgSentLen = 20.0;
        double avgSyllablesPerWord = 1.5;
        double simpleScore = calculator.calculateScore(simpleTextAvgSentLen, avgSyllablesPerWord);
        double complexScore = calculator.calculateScore(complexTextAvgSentLen, avgSyllablesPerWord);
        assertTrue(simpleScore > complexScore,
                "Simpler text (shorter sentences) should have higher Flesch Reading Ease score");
    }

}
