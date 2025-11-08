package com.kapil.verbametrics.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ReadabilityAnalysisProperties configuration.
 *
 * @author Kapil Garg
 */
@SpringBootTest
@TestPropertySource(properties = {
        "readability.analysis.flesch-kincaid.sentence-length-multiplier=0.39",
        "readability.analysis.flesch-kincaid.syllables-per-word-multiplier=11.8",
        "readability.analysis.flesch-kincaid.constant=-15.59",
        "readability.analysis.flesch-reading-ease.constant=206.835",
        "readability.analysis.flesch-reading-ease.sentence-length-multiplier=1.015",
        "readability.analysis.flesch-reading-ease.syllables-per-word-multiplier=84.6",
        "readability.analysis.reading-levels.elementary=6.0",
        "readability.analysis.reading-levels.middle-school=9.0",
        "readability.analysis.reading-levels.high-school=12.0",
        "readability.analysis.reading-levels.college=16.0",
        "readability.analysis.complexity-levels.very-easy=80.0",
        "readability.analysis.complexity-levels.easy=60.0",
        "readability.analysis.complexity-levels.moderate=40.0",
        "readability.analysis.complexity-levels.difficult=20.0"
})
class ReadabilityAnalysisPropertiesTest {

    @Autowired
    private ReadabilityAnalysisProperties properties;

    @Test
    @DisplayName("Properties bean is loaded")
    void propertiesLoaded() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("Flesch-Kincaid properties are loaded correctly")
    void fleschKincaidProperties() {
        assertNotNull(properties.getFleschKincaid());
        assertEquals(0.39, properties.getFleschKincaid().getSentenceLengthMultiplier(), 0.001);
        assertEquals(11.8, properties.getFleschKincaid().getSyllablesPerWordMultiplier(), 0.001);
        assertEquals(-15.59, properties.getFleschKincaid().getConstant(), 0.001);
    }

    @Test
    @DisplayName("Flesch Reading Ease properties are loaded correctly")
    void fleschReadingEaseProperties() {
        assertNotNull(properties.getFleschReadingEase());
        assertEquals(206.835, properties.getFleschReadingEase().getConstant(), 0.001);
        assertEquals(1.015, properties.getFleschReadingEase().getSentenceLengthMultiplier(), 0.001);
        assertEquals(84.6, properties.getFleschReadingEase().getSyllablesPerWordMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Reading level thresholds are loaded correctly")
    void readingLevelThresholds() {
        assertNotNull(properties.getReadingLevels());
        assertEquals(6.0, properties.getReadingLevels().getElementary(), 0.001);
        assertEquals(9.0, properties.getReadingLevels().getMiddleSchool(), 0.001);
        assertEquals(12.0, properties.getReadingLevels().getHighSchool(), 0.001);
        assertEquals(16.0, properties.getReadingLevels().getCollege(), 0.001);
    }

    @Test
    @DisplayName("Complexity level thresholds are loaded correctly")
    void complexityLevelThresholds() {
        assertNotNull(properties.getComplexityLevels());
        assertEquals(80.0, properties.getComplexityLevels().getVeryEasy(), 0.001);
        assertEquals(60.0, properties.getComplexityLevels().getEasy(), 0.001);
        assertEquals(40.0, properties.getComplexityLevels().getModerate(), 0.001);
        assertEquals(20.0, properties.getComplexityLevels().getDifficult(), 0.001);
    }

    @Test
    @DisplayName("Reading levels are in ascending order")
    void readingLevelsAscending() {
        ReadabilityAnalysisProperties.ReadingLevels levels = properties.getReadingLevels();
        assertTrue(levels.getElementary() < levels.getMiddleSchool());
        assertTrue(levels.getMiddleSchool() < levels.getHighSchool());
        assertTrue(levels.getHighSchool() < levels.getCollege());
    }

    @Test
    @DisplayName("Complexity levels are in descending order")
    void complexityLevelsDescending() {
        ReadabilityAnalysisProperties.ComplexityLevels levels = properties.getComplexityLevels();
        assertTrue(levels.getVeryEasy() > levels.getEasy());
        assertTrue(levels.getEasy() > levels.getModerate());
        assertTrue(levels.getModerate() > levels.getDifficult());
    }

    @Test
    @DisplayName("Flesch-Kincaid constant is negative")
    void fleschKincaidConstantIsNegative() {
        assertTrue(properties.getFleschKincaid().getConstant() < 0);
    }

    @Test
    @DisplayName("Flesch Reading Ease constant is positive")
    void fleschReadingEaseConstantIsPositive() {
        assertTrue(properties.getFleschReadingEase().getConstant() > 0);
    }

    @Test
    @DisplayName("All multipliers are positive")
    void multipliersArePositive() {
        assertTrue(properties.getFleschKincaid().getSentenceLengthMultiplier() > 0);
        assertTrue(properties.getFleschKincaid().getSyllablesPerWordMultiplier() > 0);
        assertTrue(properties.getFleschReadingEase().getSentenceLengthMultiplier() > 0);
        assertTrue(properties.getFleschReadingEase().getSyllablesPerWordMultiplier() > 0);
    }

}
