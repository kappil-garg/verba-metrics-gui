package com.kapil.verbametrics.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SentimentRuleProperties configuration.
 *
 * @author Kapil Garg
 */
@SpringBootTest
@TestPropertySource(properties = {
        "sentiment.analysis.rules.negation-window=3",
        "sentiment.analysis.rules.contrastive-window=10",
        "sentiment.analysis.rules.normalization-alpha=15.0"
})
class SentimentRulePropertiesTest {

    @Autowired
    private SentimentRuleProperties properties;

    @Test
    @DisplayName("Properties bean is loaded")
    void propertiesLoaded() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("Boosters map is loaded with default values")
    void boostersMapLoaded() {
        assertNotNull(properties.getBoosters());
        assertFalse(properties.getBoosters().isEmpty());
        assertTrue(properties.getBoosters().containsKey("extremely"));
        assertTrue(properties.getBoosters().containsKey("very"));
        assertTrue(properties.getBoosters().containsKey("really"));
    }

    @Test
    @DisplayName("Dampeners map is loaded with default values")
    void dampenersMapLoaded() {
        assertNotNull(properties.getDampeners());
        assertFalse(properties.getDampeners().isEmpty());
        assertTrue(properties.getDampeners().containsKey("slightly"));
        assertTrue(properties.getDampeners().containsKey("somewhat"));
        assertTrue(properties.getDampeners().containsKey("bit"));
    }

    @Test
    @DisplayName("Negations list can be configured")
    void negationsListLoaded() {
        // Negations are initialized as empty List.of() by default
        // Can be configured via application properties
        assertNotNull(properties.getNegations());
    }

    @Test
    @DisplayName("Contrastives list is loaded with default values")
    void contractivesListLoaded() {
        assertNotNull(properties.getContrastives());
        assertFalse(properties.getContrastives().isEmpty());
        assertTrue(properties.getContrastives().contains("but"));
        assertTrue(properties.getContrastives().contains("however"));
        assertTrue(properties.getContrastives().contains("though"));
    }

    @Test
    @DisplayName("Punctuation breaks list is loaded with default values")
    void punctuationBreaksLoaded() {
        assertNotNull(properties.getPunctuationBreaks());
        assertFalse(properties.getPunctuationBreaks().isEmpty());
        assertTrue(properties.getPunctuationBreaks().contains("."));
        assertTrue(properties.getPunctuationBreaks().contains("!"));
        assertTrue(properties.getPunctuationBreaks().contains("?"));
    }

    @Test
    @DisplayName("Phrases map is loaded with default values")
    void phrasesMapLoaded() {
        assertNotNull(properties.getPhrases());
        assertFalse(properties.getPhrases().isEmpty());
        assertTrue(properties.getPhrases().containsKey("waste of time"));
        assertTrue(properties.getPhrases().containsKey("not good"));
    }

    @Test
    @DisplayName("Negation window is loaded correctly")
    void negationWindowLoaded() {
        assertEquals(3, properties.getNegationWindow());
    }

    @Test
    @DisplayName("Contrastive window is loaded correctly")
    void contrastiveWindowLoaded() {
        assertEquals(10, properties.getContrastiveWindow());
    }

    @Test
    @DisplayName("Normalization alpha is loaded correctly")
    void normalizationAlphaLoaded() {
        assertEquals(15.0, properties.getNormalizationAlpha(), 0.001);
    }

    @Test
    @DisplayName("All booster values are positive")
    void boosterValuesArePositive() {
        properties.getBoosters().values().forEach(value ->
                assertTrue(value > 0, "Booster value should be positive: " + value)
        );
    }

    @Test
    @DisplayName("All dampener values are negative")
    void dampenerValuesAreNegative() {
        properties.getDampeners().values().forEach(value ->
                assertTrue(value < 0, "Dampener value should be negative: " + value)
        );
    }

    @Test
    @DisplayName("Phrase weights include both positive and negative values")
    void phraseWeightsIncludeBothSigns() {
        boolean hasPositive = properties.getPhrases().values().stream().anyMatch(v -> v > 0);
        boolean hasNegative = properties.getPhrases().values().stream().anyMatch(v -> v < 0);
        assertTrue(hasPositive, "Should have at least one positive phrase weight");
        assertTrue(hasNegative, "Should have at least one negative phrase weight");
    }

    @Test
    @DisplayName("Negation window is positive")
    void negationWindowIsPositive() {
        assertTrue(properties.getNegationWindow() > 0);
    }

    @Test
    @DisplayName("Contrastive window is positive")
    void contrastiveWindowIsPositive() {
        assertTrue(properties.getContrastiveWindow() > 0);
    }

    @Test
    @DisplayName("Normalization alpha is positive")
    void normalizationAlphaIsPositive() {
        assertTrue(properties.getNormalizationAlpha() > 0);
    }

    @Test
    @DisplayName("Contrastive window is greater than negation window")
    void contrastiveWindowIsGreater() {
        assertTrue(properties.getContrastiveWindow() >= properties.getNegationWindow(),
                "Contrastive window should be >= negation window");
    }

    @Test
    @DisplayName("Boosters can be modified")
    void boostersCanBeModified() {
        Map<String, Double> customBoosters = Map.of("amazing", 0.5, "fantastic", 0.6);
        properties.setBoosters(customBoosters);
        assertEquals(customBoosters, properties.getBoosters());
    }

    @Test
    @DisplayName("Dampeners can be modified")
    void dampenersCanBeModified() {
        Map<String, Double> customDampeners = Map.of("barely", -0.3, "slightly", -0.2);
        properties.setDampeners(customDampeners);
        assertEquals(customDampeners, properties.getDampeners());
    }

    @Test
    @DisplayName("Negations list can be modified")
    void negationsCanBeModified() {
        List<String> customNegations = List.of("not", "never", "none", "no", "nothing");
        properties.setNegations(customNegations);
        assertEquals(customNegations, properties.getNegations());
        assertEquals(5, properties.getNegations().size());
    }

}
