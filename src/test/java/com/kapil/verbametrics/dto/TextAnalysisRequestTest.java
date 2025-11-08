package com.kapil.verbametrics.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextAnalysisRequest DTO.
 *
 * @author Kapil Garg
 */
class TextAnalysisRequestTest {

    @Test
    @DisplayName("Constructor creates valid request with text")
    void constructor_validText() {
        TextAnalysisRequest request = new TextAnalysisRequest("Hello, world!");
        assertEquals("Hello, world!", request.text());
    }

    @Test
    @DisplayName("Constructor accepts text with whitespace")
    void constructor_textWithWhitespace() {
        TextAnalysisRequest request = new TextAnalysisRequest("  Hello, world!  ");
        assertEquals("  Hello, world!  ", request.text());
    }

    @Test
    @DisplayName("Constructor accepts multiline text")
    void constructor_multilineText() {
        String multilineText = "Line 1\nLine 2\nLine 3";
        TextAnalysisRequest request = new TextAnalysisRequest(multilineText);
        assertEquals(multilineText, request.text());
    }

    @Test
    @DisplayName("Constructor rejects null text")
    void constructor_nullText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TextAnalysisRequest(null)
        );
        assertEquals("Text content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects empty text")
    void constructor_emptyText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TextAnalysisRequest("")
        );
        assertEquals("Text content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects whitespace-only text")
    void constructor_whitespaceOnlyText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TextAnalysisRequest("   ")
        );
        assertEquals("Text content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects tab-only text")
    void constructor_tabOnlyText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TextAnalysisRequest("\t\t")
        );
        assertEquals("Text content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor rejects newline-only text")
    void constructor_newlineOnlyText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TextAnalysisRequest("\n\n")
        );
        assertEquals("Text content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Record equality works correctly")
    void testEquals() {
        TextAnalysisRequest request1 = new TextAnalysisRequest("Hello");
        TextAnalysisRequest request2 = new TextAnalysisRequest("Hello");
        TextAnalysisRequest request3 = new TextAnalysisRequest("World");
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    @DisplayName("Record hashCode works correctly")
    void testHashCode() {
        TextAnalysisRequest request1 = new TextAnalysisRequest("Hello");
        TextAnalysisRequest request2 = new TextAnalysisRequest("Hello");
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("toString returns useful representation")
    void testToString() {
        TextAnalysisRequest request = new TextAnalysisRequest("Sample text");
        String result = request.toString();
        assertTrue(result.contains("TextAnalysisRequest"));
        assertTrue(result.contains("Sample text"));
    }

    @Test
    @DisplayName("Edge case: very long text")
    void edgeCase_veryLongText() {
        String longText = "word ".repeat(10000);
        TextAnalysisRequest request = new TextAnalysisRequest(longText);
        assertEquals(longText, request.text());
    }

    @Test
    @DisplayName("Edge case: single character")
    void edgeCase_singleCharacter() {
        TextAnalysisRequest request = new TextAnalysisRequest("a");
        assertEquals("a", request.text());
    }

    @Test
    @DisplayName("Edge case: special characters")
    void edgeCase_specialCharacters() {
        String specialText = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        TextAnalysisRequest request = new TextAnalysisRequest(specialText);
        assertEquals(specialText, request.text());
    }

    @Test
    @DisplayName("Edge case: Unicode characters")
    void edgeCase_unicodeCharacters() {
        String unicodeText = "Hello 世界 🌍";
        TextAnalysisRequest request = new TextAnalysisRequest(unicodeText);
        assertEquals(unicodeText, request.text());
    }

}
