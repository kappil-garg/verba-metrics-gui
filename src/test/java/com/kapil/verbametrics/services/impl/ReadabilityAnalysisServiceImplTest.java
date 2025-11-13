package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.services.calculators.FleschKincaidCalculator;
import com.kapil.verbametrics.services.calculators.FleschReadingEaseCalculator;
import com.kapil.verbametrics.services.calculators.SentenceLengthCalculator;
import com.kapil.verbametrics.services.calculators.SyllablePerWordCalculator;
import com.kapil.verbametrics.services.classifiers.ComplexityClassifier;
import com.kapil.verbametrics.services.classifiers.ReadingLevelClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for ReadabilityAnalysisServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class ReadabilityAnalysisServiceImplTest {

    @Mock
    private SentenceLengthCalculator sentenceLengthCalculator;

    @Mock
    private SyllablePerWordCalculator syllablePerWordCalculator;

    @Mock
    private FleschKincaidCalculator fleschKincaidCalculator;

    @Mock
    private FleschReadingEaseCalculator fleschReadingEaseCalculator;

    @Mock
    private ReadingLevelClassifier readingLevelClassifier;

    @Mock
    private ComplexityClassifier complexityClassifier;

    private ReadabilityAnalysisServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReadabilityAnalysisServiceImpl(
                sentenceLengthCalculator,
                syllablePerWordCalculator,
                fleschKincaidCalculator,
                fleschReadingEaseCalculator,
                readingLevelClassifier,
                complexityClassifier
        );
    }

    @Test
    @DisplayName("analyzeReadability with valid text should compute all metrics")
    void analyzeReadability_validText_computesMetrics() {
        String text = "This is a test sentence. It has multiple words.";
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenReturn(6.5);
        when(syllablePerWordCalculator.calculateAverageSyllablesPerWord(anyString())).thenReturn(1.5);
        when(fleschKincaidCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(8.5);
        when(fleschReadingEaseCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(75.0);
        when(readingLevelClassifier.determineReadingLevel(anyDouble())).thenReturn("Middle School");
        when(complexityClassifier.determineComplexity(anyDouble())).thenReturn("Easy");
        ReadabilityMetrics result = service.analyzeReadability(text);
        assertNotNull(result, "Result should not be null");
        assertEquals(8.5, result.fleschKincaidScore(), 0.01);
        assertEquals(75.0, result.fleschReadingEase(), 0.01);
        assertEquals("Middle School", result.readingLevel());
        assertEquals("Easy", result.complexity());
        assertEquals(6.5, result.averageSentenceLength(), 0.01);
        assertEquals(1.5, result.averageSyllablesPerWord(), 0.01);
        verify(sentenceLengthCalculator).calculateAverageSentenceLength(text);
        verify(syllablePerWordCalculator).calculateAverageSyllablesPerWord(text);
        verify(fleschKincaidCalculator).calculateScore(anyDouble(), anyDouble());
        verify(fleschReadingEaseCalculator).calculateScore(anyDouble(), anyDouble());
        verify(readingLevelClassifier).determineReadingLevel(8.5);
        verify(complexityClassifier).determineComplexity(75.0);
    }

    @Test
    @DisplayName("analyzeReadability without complexity should skip complexity classifier")
    void analyzeReadability_withoutComplexity_skipsComplexityClassifier() {
        String text = "Simple text";
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenReturn(2.0);
        when(syllablePerWordCalculator.calculateAverageSyllablesPerWord(anyString())).thenReturn(1.0);
        when(fleschKincaidCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(5.0);
        when(fleschReadingEaseCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(90.0);
        when(readingLevelClassifier.determineReadingLevel(anyDouble())).thenReturn("Elementary");
        ReadabilityMetrics result = service.analyzeReadability(text, false);
        assertNotNull(result);
        assertEquals(5.0, result.fleschKincaidScore(), 0.01);
        assertEquals(90.0, result.fleschReadingEase(), 0.01);
        verify(sentenceLengthCalculator).calculateAverageSentenceLength(text);
        verify(syllablePerWordCalculator).calculateAverageSyllablesPerWord(text);
        verify(fleschKincaidCalculator).calculateScore(anyDouble(), anyDouble());
        verify(fleschReadingEaseCalculator).calculateScore(anyDouble(), anyDouble());
        verify(readingLevelClassifier).determineReadingLevel(5.0);
    }

    @Test
    @DisplayName("analyzeReadability with blank text should return default metrics")
    void analyzeReadability_blankText_returnsDefaultMetrics() {
        String text = "   ";
        ReadabilityMetrics result = service.analyzeReadability(text);
        assertNotNull(result);
        assertEquals(0.0, result.fleschKincaidScore(), 0.01);
        assertEquals(100.0, result.fleschReadingEase(), 0.01);
        assertEquals("Elementary", result.readingLevel());
        assertEquals("Very Easy", result.complexity());
        assertEquals(0.0, result.averageSentenceLength(), 0.01);
        assertEquals(0.0, result.averageSyllablesPerWord(), 0.01);
        verifyNoInteractions(sentenceLengthCalculator, syllablePerWordCalculator,
                fleschKincaidCalculator, fleschReadingEaseCalculator,
                readingLevelClassifier, complexityClassifier);
    }

    @Test
    @DisplayName("analyzeReadability with null text should throw NullPointerException")
    void analyzeReadability_nullText_throwsException() {
        assertThrows(NullPointerException.class,
                () -> service.analyzeReadability(null),
                "Should throw NullPointerException for null text");
        verifyNoInteractions(sentenceLengthCalculator, syllablePerWordCalculator,
                fleschKincaidCalculator, fleschReadingEaseCalculator,
                readingLevelClassifier, complexityClassifier);
    }

    @Test
    @DisplayName("analyzeReadability should wrap and propagate calculator exceptions")
    void analyzeReadability_calculatorThrowsException_wrapsException() {
        String text = "Test text";
        RuntimeException originalException = new RuntimeException("Calculator error");
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenThrow(originalException);
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> service.analyzeReadability(text),
                "Should wrap and throw RuntimeException");
        assertTrue(thrown.getMessage().contains("Failed to analyze readability"),
                "Exception message should indicate readability analysis failure");
        assertEquals(originalException, thrown.getCause(),
                "Should preserve original exception as cause");
        verify(sentenceLengthCalculator).calculateAverageSentenceLength(text);
    }

    @Test
    @DisplayName("analyzeReadability with single parameter should default to include complexity")
    void analyzeReadability_singleParameter_includesComplexity() {
        String text = "Test sentence";
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenReturn(2.0);
        when(syllablePerWordCalculator.calculateAverageSyllablesPerWord(anyString())).thenReturn(1.5);
        when(fleschKincaidCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(6.0);
        when(fleschReadingEaseCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(80.0);
        when(readingLevelClassifier.determineReadingLevel(anyDouble())).thenReturn("Elementary");
        when(complexityClassifier.determineComplexity(anyDouble())).thenReturn("Very Easy");
        ReadabilityMetrics result = service.analyzeReadability(text);
        assertNotNull(result);
        assertEquals("Very Easy", result.complexity());
        verify(complexityClassifier).determineComplexity(80.0);
    }

    @Test
    @DisplayName("analyzeReadability should handle high complexity text")
    void analyzeReadability_complexText_computesCorrectly() {
        String text = "The implementation demonstrates sophisticated algorithmic complexity.";
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenReturn(7.0);
        when(syllablePerWordCalculator.calculateAverageSyllablesPerWord(anyString())).thenReturn(3.5);
        when(fleschKincaidCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(15.5);
        when(fleschReadingEaseCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(35.0);
        when(readingLevelClassifier.determineReadingLevel(anyDouble())).thenReturn("College");
        when(complexityClassifier.determineComplexity(anyDouble())).thenReturn("Difficult");
        ReadabilityMetrics result = service.analyzeReadability(text);
        assertNotNull(result);
        assertEquals(15.5, result.fleschKincaidScore(), 0.01);
        assertEquals(35.0, result.fleschReadingEase(), 0.01);
        assertEquals("College", result.readingLevel());
        assertEquals("Difficult", result.complexity());
        verify(readingLevelClassifier).determineReadingLevel(15.5);
        verify(complexityClassifier).determineComplexity(35.0);
    }

    @Test
    @DisplayName("Constructor should accept all required dependencies")
    void constructor_withValidDependencies_createsInstance() {
        assertDoesNotThrow(() -> new ReadabilityAnalysisServiceImpl(
                sentenceLengthCalculator,
                syllablePerWordCalculator,
                fleschKincaidCalculator,
                fleschReadingEaseCalculator,
                readingLevelClassifier,
                complexityClassifier
        ), "Constructor should accept valid dependencies");
    }

    @Test
    @DisplayName("analyzeReadability should handle empty string")
    void analyzeReadability_emptyString_returnsDefaultMetrics() {
        String text = "";
        ReadabilityMetrics result = service.analyzeReadability(text);
        assertNotNull(result);
        assertEquals(0.0, result.fleschKincaidScore());
        assertEquals(100.0, result.fleschReadingEase());
        verifyNoInteractions(sentenceLengthCalculator);
    }

    @Test
    @DisplayName("analyzeReadability should call classifiers with correct scores")
    void analyzeReadability_callsClassifiersWithCorrectScores() {
        String text = "Test";
        double expectedFK = 10.5;
        double expectedFRE = 65.0;
        when(sentenceLengthCalculator.calculateAverageSentenceLength(anyString())).thenReturn(5.0);
        when(syllablePerWordCalculator.calculateAverageSyllablesPerWord(anyString())).thenReturn(2.0);
        when(fleschKincaidCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(expectedFK);
        when(fleschReadingEaseCalculator.calculateScore(anyDouble(), anyDouble())).thenReturn(expectedFRE);
        when(readingLevelClassifier.determineReadingLevel(expectedFK)).thenReturn("High School");
        when(complexityClassifier.determineComplexity(expectedFRE)).thenReturn("Moderate");
        service.analyzeReadability(text, true);
        verify(readingLevelClassifier).determineReadingLevel(expectedFK);
        verify(complexityClassifier).determineComplexity(expectedFRE);
    }

}
