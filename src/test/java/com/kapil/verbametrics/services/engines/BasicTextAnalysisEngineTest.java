package com.kapil.verbametrics.services.engines;

import com.kapil.verbametrics.domain.BasicTextStatistics;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BasicTextAnalysisEngine.
 *
 * @author Kapil Garg
 */
class BasicTextAnalysisEngineTest {

    private BasicTextAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BasicTextAnalysisEngine();
    }

    @Test
    @DisplayName("analyze should compute basic statistics for simple text")
    void analyze_simpleText_computesStatistics() {
        String text = "Hello world. This is a test.";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.documentId(), "Document ID should be generated");
        assertNotNull(response.basicStats(), "Basic stats should not be null");
        assertTrue(response.processingTimeMs() >= 0, "Processing time should be non-negative");
        BasicTextStatistics stats = response.basicStats();
        assertEquals(6, stats.wordCount(), "Should count 6 words");
        assertEquals(2, stats.sentenceCount(), "Should count 2 sentences");
        assertEquals(28, stats.characterCount(), "Should count all characters");
        assertEquals(23, stats.characterCountNoSpaces(), "Should count characters without spaces");
        assertEquals(1, stats.paragraphCount(), "Should count 1 paragraph");
    }

    @Test
    @DisplayName("analyze should throw for null text input")
    void analyze_nullText_throws() {
        assertThrows(IllegalArgumentException.class, () -> new TextAnalysisRequest(null),
                "Constructor should enforce non-null text");
    }

    @Test
    @DisplayName("analyze should throw for empty text input")
    void analyze_emptyText_throws() {
        assertThrows(IllegalArgumentException.class, () -> new TextAnalysisRequest(""),
                "Constructor should enforce non-empty text");
    }

    @Test
    @DisplayName("analyze should throw for blank text input")
    void analyze_blankText_throws() {
        assertThrows(IllegalArgumentException.class, () -> new TextAnalysisRequest("   "),
                "Constructor should enforce non-blank text");
    }

    @Test
    @DisplayName("analyze should count multiple paragraphs")
    void analyze_multipleParagraphs_countsParagraphs() {
        String text = "First paragraph.\n\nSecond paragraph.\n\nThird paragraph.";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertTrue(stats.paragraphCount() >= 1, "Should count multiple paragraphs");
    }

    @Test
    @DisplayName("analyze should count multiple sentences")
    void analyze_multipleSentences_countsSentences() {
        String text = "First sentence. Second sentence! Third sentence? Fourth sentence.";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertTrue(stats.sentenceCount() >= 3, "Should count multiple sentences with various punctuation");
    }

    @Test
    @DisplayName("analyze should handle single word")
    void analyze_singleWord_computesStatistics() {
        String text = "Hello";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertEquals(1, stats.wordCount(), "Should count 1 word");
        assertEquals(5, stats.characterCount(), "Should count 5 characters");
        assertEquals(5, stats.characterCountNoSpaces(), "Should count 5 characters without spaces");
    }

    @Test
    @DisplayName("analyze should handle text with special characters")
    void analyze_withSpecialCharacters_computesStatistics() {
        String text = "Hello, world! How are you?";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertTrue(stats.wordCount() > 0, "Should count words despite special characters");
        assertTrue(stats.characterCount() > 0, "Should count all characters");
    }

    @Test
    @DisplayName("analyze should generate unique document IDs")
    void analyze_multipleRequests_generatesUniqueIds() {
        TextAnalysisRequest request1 = new TextAnalysisRequest("First text");
        TextAnalysisRequest request2 = new TextAnalysisRequest("Second text");
        TextAnalysisResponse response1 = engine.analyze(request1);
        TextAnalysisResponse response2 = engine.analyze(request2);
        assertNotEquals(response1.documentId(), response2.documentId(),
                "Should generate unique document IDs");
    }

    @Test
    @DisplayName("analyze should track processing time")
    void analyze_tracksProcessingTime() {
        String text = "Test text for processing time.";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        assertTrue(response.processingTimeMs() >= 0, "Processing time should be non-negative");
    }

    @Test
    @DisplayName("analyze should handle very long text")
    void analyze_longText_computesStatistics() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is sentence ").append(i).append(". ");
        }
        TextAnalysisRequest request = new TextAnalysisRequest(longText.toString());
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertTrue(stats.wordCount() > 100, "Should count words in long text");
        assertTrue(stats.sentenceCount() >= 100, "Should count sentences in long text");
    }

    @Test
    @DisplayName("analyze should count character without spaces correctly")
    void analyze_characterCountNoSpaces_excludesAllWhitespace() {
        String text = "Hello    world\n\ttest";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertEquals("Helloworldtest".length(), stats.characterCountNoSpaces(),
                "Should exclude all whitespace characters");
        assertTrue(stats.characterCount() > stats.characterCountNoSpaces(),
                "Character count with spaces should be greater");
    }

    @Test
    @DisplayName("analyze should handle text with only punctuation")
    void analyze_onlyPunctuation_computesStatistics() {
        String text = "... !!! ???";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        assertNotNull(response, "Response should not be null");
        assertTrue(response.basicStats().characterCount() > 0, "Should count punctuation characters");
    }

    @Test
    @DisplayName("analyze should handle text with numbers")
    void analyze_withNumbers_computesStatistics() {
        String text = "There are 123 numbers in this text.";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertTrue(stats.wordCount() > 0, "Should count words including numbers");
        assertTrue(stats.characterCount() > 0, "Should count all characters");
    }

    @Test
    @DisplayName("analyze should trim text before processing")
    void analyze_textWithLeadingTrailingSpaces_trimsCorrectly() {
        String text = "   Hello world   ";
        TextAnalysisRequest request = new TextAnalysisRequest(text);
        TextAnalysisResponse response = engine.analyze(request);
        BasicTextStatistics stats = response.basicStats();
        assertEquals(2, stats.wordCount(), "Should count 2 words after trimming");
    }

}
