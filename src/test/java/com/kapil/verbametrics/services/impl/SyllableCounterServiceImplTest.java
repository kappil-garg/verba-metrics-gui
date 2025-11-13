package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.services.engines.SyllableCountingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SyllableCounterServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class SyllableCounterServiceImplTest {

    @Mock
    private SyllableCountingEngine countingEngine;

    private SyllableCounterServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SyllableCounterServiceImpl(countingEngine);
    }

    @Test
    @DisplayName("countSyllables for single word should delegate to engine")
    void countSyllables_singleWord_delegatesToEngine() {
        String word = "hello";
        when(countingEngine.countSyllables(word)).thenReturn(2);
        int count = service.countSyllables(word);
        assertEquals(2, count, "Should return syllable count from engine");
        verify(countingEngine).countSyllables(word);
        verifyNoMoreInteractions(countingEngine);
    }

    @Test
    @DisplayName("countSyllables for word array should delegate to engine")
    void countSyllables_wordArray_delegatesToEngine() {
        String[] words = {"hello", "world", "testing"};
        when(countingEngine.countSyllables(words)).thenReturn(6);
        int count = service.countSyllables(words);
        assertEquals(6, count, "Should return total syllable count from engine");
        verify(countingEngine).countSyllables(words);
        verifyNoMoreInteractions(countingEngine);
    }

    @Test
    @DisplayName("calculateAverageSyllablesPerWord should delegate to engine")
    void calculateAverageSyllablesPerWord_delegatesToEngine() {
        String[] words = {"hello", "world"};
        when(countingEngine.calculateAverageSyllablesPerWord(words)).thenReturn(2.0);
        double average = service.calculateAverageSyllablesPerWord(words);
        assertEquals(2.0, average, 0.001, "Should return average from engine");
        verify(countingEngine).calculateAverageSyllablesPerWord(words);
        verifyNoMoreInteractions(countingEngine);
    }

    @Test
    @DisplayName("countSyllables for empty word should return zero")
    void countSyllables_emptyWord_returnsZero() {
        String word = "";
        when(countingEngine.countSyllables(word)).thenReturn(0);
        int count = service.countSyllables(word);
        assertEquals(0, count);
        verify(countingEngine).countSyllables(word);
    }

    @Test
    @DisplayName("countSyllables for empty array should return zero")
    void countSyllables_emptyArray_returnsZero() {
        String[] words = {};
        when(countingEngine.countSyllables(words)).thenReturn(0);
        int count = service.countSyllables(words);
        assertEquals(0, count);
        verify(countingEngine).countSyllables(words);
    }

    @Test
    @DisplayName("countSyllables should handle multi-syllable words")
    void countSyllables_multiSyllableWord_returnsCorrectCount() {
        String word = "extraordinary";
        when(countingEngine.countSyllables(word)).thenReturn(5);
        int count = service.countSyllables(word);
        assertEquals(5, count);
        verify(countingEngine).countSyllables(word);
    }

    @Test
    @DisplayName("Service should propagate engine exceptions")
    void countSyllables_engineThrowsException_propagatesException() {
        String word = "test";
        RuntimeException expectedException = new RuntimeException("Engine error");
        when(countingEngine.countSyllables(word)).thenThrow(expectedException);
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> service.countSyllables(word),
                "Should propagate engine exception");
        assertEquals("Engine error", thrown.getMessage());
        verify(countingEngine).countSyllables(word);
    }

    @Test
    @DisplayName("Constructor should create instance with valid engine")
    void constructor_withValidEngine_createsInstance() {
        assertDoesNotThrow(() -> new SyllableCounterServiceImpl(countingEngine),
                "Constructor should accept valid engine");
    }

    @Test
    @DisplayName("Service should handle multiple consecutive calls")
    void countSyllables_multipleCalls_handlesAll() {
        when(countingEngine.countSyllables("first")).thenReturn(1);
        when(countingEngine.countSyllables("second")).thenReturn(2);
        when(countingEngine.countSyllables("third")).thenReturn(1);
        int count1 = service.countSyllables("first");
        int count2 = service.countSyllables("second");
        int count3 = service.countSyllables("third");
        assertEquals(1, count1);
        assertEquals(2, count2);
        assertEquals(1, count3);
        verify(countingEngine).countSyllables("first");
        verify(countingEngine).countSyllables("second");
        verify(countingEngine).countSyllables("third");
    }

}
