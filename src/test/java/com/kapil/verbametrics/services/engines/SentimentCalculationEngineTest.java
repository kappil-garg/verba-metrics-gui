package com.kapil.verbametrics.services.engines;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.config.SentimentRuleProperties;
import com.kapil.verbametrics.services.WordListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for SentimentCalculationEngine.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SentimentCalculationEngineTest {

    @Mock
    private WordListService wordListService;

    @Mock
    private SentimentAnalysisProperties analysisProperties;

    @Mock
    private SentimentAnalysisProperties.TextProcessing textProcessing;

    @Mock
    private SentimentRuleProperties ruleProperties;

    private SentimentCalculationEngine engine;

    @BeforeEach
    void setUp() {
        when(analysisProperties.getTextProcessing()).thenReturn(textProcessing);
        when(textProcessing.isCaseSensitive()).thenReturn(false);
        when(textProcessing.getWordSeparator()).thenReturn("\\s+");
        when(textProcessing.isNormalizeHyphens()).thenReturn(false);
        when(ruleProperties.getNormalizationAlpha()).thenReturn(1.0);
        when(wordListService.getPositiveWords()).thenReturn(Set.of("good", "great", "excellent", "happy"));
        when(wordListService.getNegativeWords()).thenReturn(Set.of("bad", "terrible", "awful", "sad"));
        engine = new SentimentCalculationEngine(wordListService, analysisProperties, ruleProperties);
    }

    @Test
    @DisplayName("calculateSentimentScore should return positive score for positive text")
    void calculateSentimentScore_positiveText_returnsPositiveScore() {
        String text = "This is good and great";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score > 0, "Positive text should have positive score");
        assertTrue(score <= 1.0, "Score should be normalized to [-1, 1]");
    }

    @Test
    @DisplayName("calculateSentimentScore should return negative score for negative text")
    void calculateSentimentScore_negativeText_returnsNegativeScore() {
        String text = "This is bad and terrible";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score < 0, "Negative text should have negative score");
        assertTrue(score >= -1.0, "Score should be normalized to [-1, 1]");
    }

    @Test
    @DisplayName("calculateSentimentScore should return neutral score for neutral text")
    void calculateSentimentScore_neutralText_returnsNeutralScore() {
        String text = "This is a simple statement without sentiment words";
        double score = engine.calculateSentimentScore(text);
        assertEquals(0.0, score, 0.1, "Neutral text should have score close to 0");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle null text")
    void calculateSentimentScore_nullText_returnsZero() {
        double score = engine.calculateSentimentScore(null);
        assertEquals(0.0, score, "Null text should return 0.0");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle empty text")
    void calculateSentimentScore_emptyText_returnsZero() {
        double score = engine.calculateSentimentScore("");
        assertEquals(0.0, score, "Empty text should return 0.0");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle blank text")
    void calculateSentimentScore_blankText_returnsZero() {
        double score = engine.calculateSentimentScore("   ");
        assertEquals(0.0, score, "Blank text should return 0.0");
    }

    @Test
    @DisplayName("calculateSentimentScore should respect case sensitivity setting")
    void calculateSentimentScore_caseSensitive_respectsCase() {
        when(textProcessing.isCaseSensitive()).thenReturn(true);
        when(wordListService.getPositiveWords()).thenReturn(Set.of("Good"));
        engine = new SentimentCalculationEngine(wordListService, analysisProperties, ruleProperties);
        String lowerText = "This is good";
        String upperText = "This is Good";
        double lowerScore = engine.calculateSentimentScore(lowerText);
        double upperScore = engine.calculateSentimentScore(upperText);
        assertTrue(upperScore > lowerScore, "Case sensitive mode should differentiate case");
    }

    @Test
    @DisplayName("calculateSentimentScore should normalize case when case insensitive")
    void calculateSentimentScore_caseInsensitive_normalizesCase() {
        when(textProcessing.isCaseSensitive()).thenReturn(false);
        String lowerText = "This is good";
        String upperText = "This is GOOD";
        double lowerScore = engine.calculateSentimentScore(lowerText);
        double upperScore = engine.calculateSentimentScore(upperText);
        assertEquals(lowerScore, upperScore, 0.01,
                "Case insensitive mode should produce same scores");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle mixed positive and negative words")
    void calculateSentimentScore_mixedSentiment_balancesScore() {
        String text = "This is good but also bad";
        double score = engine.calculateSentimentScore(text);
        assertTrue(Math.abs(score) < 1.0, "Mixed sentiment should have balanced score");
    }

    @Test
    @DisplayName("calculateSentimentScore should normalize hyphens when configured")
    void calculateSentimentScore_normalizeHyphens_replacesHyphens() {
        when(textProcessing.isNormalizeHyphens()).thenReturn(true);
        engine = new SentimentCalculationEngine(wordListService, analysisProperties, ruleProperties);
        String text = "state-of-the-art good";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score > 0, "Should recognize 'good' after hyphen normalization");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle text with apostrophes")
    void calculateSentimentScore_withApostrophes_handlesCorrectly() {
        String text = "It's really good isn't it?";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score > 0, "Should handle apostrophes correctly");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle text with only positive words")
    void calculateSentimentScore_onlyPositive_returnsStrongPositive() {
        String text = "excellent great good happy";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score > 0.5, "All positive words should give strong positive score");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle text with only negative words")
    void calculateSentimentScore_onlyNegative_returnsStrongNegative() {
        String text = "terrible bad awful sad";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score < -0.5, "All negative words should give strong negative score");
    }

    @Test
    @DisplayName("calculateSentimentScore should normalize score to range [-1, 1]")
    void calculateSentimentScore_normalizedRange_withinBounds() {
        String extremePositive = "excellent excellent excellent excellent excellent";
        String extremeNegative = "terrible terrible terrible terrible terrible";
        double positiveScore = engine.calculateSentimentScore(extremePositive);
        double negativeScore = engine.calculateSentimentScore(extremeNegative);
        assertTrue(positiveScore >= -1.0 && positiveScore <= 1.0,
                "Positive score should be within [-1, 1]");
        assertTrue(negativeScore >= -1.0 && negativeScore <= 1.0,
                "Negative score should be within [-1, 1]");
    }

    @Test
    @DisplayName("calculateSentimentScore should handle long text")
    void calculateSentimentScore_longText_computesCorrectly() {
        double score = engine.calculateSentimentScore("This is good ".repeat(50));
        assertTrue(score > 0, "Long positive text should have positive score");
    }

    @Test
    @DisplayName("calculateSentimentScore should use normalization alpha")
    void calculateSentimentScore_usesNormalizationAlpha() {
        when(ruleProperties.getNormalizationAlpha()).thenReturn(2.0);
        engine = new SentimentCalculationEngine(wordListService, analysisProperties, ruleProperties);
        String text = "good";
        double score = engine.calculateSentimentScore(text);
        assertTrue(Math.abs(score) <= 1.0, "Score should still be normalized");
    }

    @Test
    @DisplayName("calculateSentimentScore should use configured word separator")
    void calculateSentimentScore_usesWordSeparator() {
        when(textProcessing.getWordSeparator()).thenReturn("\\s+");
        String text = "good    great    excellent";
        double score = engine.calculateSentimentScore(text);
        assertTrue(score > 0, "Should use word separator to tokenize");
    }

}
