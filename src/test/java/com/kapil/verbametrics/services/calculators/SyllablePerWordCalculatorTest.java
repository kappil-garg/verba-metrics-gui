package com.kapil.verbametrics.services.calculators;

import com.kapil.verbametrics.services.SyllableCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for SyllablePerWordCalculator.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class SyllablePerWordCalculatorTest {

    @Mock
    private SyllableCounterService syllableCounterService;

    private SyllablePerWordCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SyllablePerWordCalculator(syllableCounterService);
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should delegate to syllable counter service")
    void calculateAverageSyllablesPerWord_delegatesToService() {
        String text = "hello world test";
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(1.5);
        double average = calculator.calculateAverageSyllablesPerWord(text);
        assertEquals(1.5, average, "Should return value from syllable counter service");
        verify(syllableCounterService).calculateAverageSyllablesPerWord(any(String[].class));
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle single word")
    void calculateAverageSyllablesPerWord_singleWord_computesAverage() {
        String text = "beautiful";
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(3.0);
        double average = calculator.calculateAverageSyllablesPerWord(text);
        assertEquals(3.0, average, "Should handle single word");
        verify(syllableCounterService).calculateAverageSyllablesPerWord(argThat(words ->
                words != null && words.length == 1
        ));
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle empty text")
    void calculateAverageSyllablesPerWord_emptyText_returnsZero() {
        String text = "";
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(0.0);
        double average = calculator.calculateAverageSyllablesPerWord(text);
        assertEquals(0.0, average, "Empty text should return 0.0");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should handle long text")
    void calculateAverageSyllablesPerWord_longText_computesAverage() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("word").append(i).append(" ");
        }
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(1.8);
        double average = calculator.calculateAverageSyllablesPerWord(longText.toString());
        assertEquals(1.8, average, "Should handle long text");
        verify(syllableCounterService).calculateAverageSyllablesPerWord(any(String[].class));
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should return zero when service returns zero")
    void calculateAverageSyllablesPerWord_serviceReturnsZero_returnsZero() {
        String text = "test";
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(0.0);
        double average = calculator.calculateAverageSyllablesPerWord(text);
        assertEquals(0.0, average, "Should return 0.0 when service returns 0.0");
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should be deterministic")
    void calculateAverageSyllablesPerWord_sameInput_returnsSameResult() {
        String text = "hello world test";
        when(syllableCounterService.calculateAverageSyllablesPerWord(any(String[].class)))
                .thenReturn(1.5);
        double average1 = calculator.calculateAverageSyllablesPerWord(text);
        double average2 = calculator.calculateAverageSyllablesPerWord(text);
        assertEquals(average1, average2, "Same input should produce same result");
    }

}
