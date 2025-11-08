package com.kapil.verbametrics.dto;

import com.kapil.verbametrics.domain.BasicTextStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextAnalysisResponse DTO.
 *
 * @author Kapil Garg
 */
class TextAnalysisResponseTest {

    @Test
    @DisplayName("Constructor creates valid response")
    void constructor_validValues() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, 150L);
        assertEquals("doc-123", response.documentId());
        assertEquals(stats, response.basicStats());
        assertEquals(150L, response.processingTimeMs());
    }

    @Test
    @DisplayName("Constructor accepts null documentId")
    void constructor_nullDocumentId() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse(null, stats, 150L);
        assertNull(response.documentId());
        assertEquals(stats, response.basicStats());
    }

    @Test
    @DisplayName("Constructor accepts zero processing time")
    void constructor_zeroProcessingTime() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, 0L);
        assertEquals(0L, response.processingTimeMs());
    }

    @Test
    @DisplayName("Constructor accepts negative processing time")
    void constructor_negativeProcessingTime() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        // Note: No validation on processingTimeMs, so negative values are allowed
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, -1L);
        assertEquals(-1L, response.processingTimeMs());
    }

    @Test
    @DisplayName("Constructor accepts large processing time")
    void constructor_largeProcessingTime() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.processingTimeMs());
    }

    @Test
    @DisplayName("Record equality works correctly")
    void testEquals() {
        BasicTextStatistics stats1 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats2 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats3 = new BasicTextStatistics(101, 5, 500, 400, 3);
        TextAnalysisResponse response1 = new TextAnalysisResponse("doc-123", stats1, 150L);
        TextAnalysisResponse response2 = new TextAnalysisResponse("doc-123", stats2, 150L);
        TextAnalysisResponse response3 = new TextAnalysisResponse("doc-123", stats3, 150L);
        TextAnalysisResponse response4 = new TextAnalysisResponse("doc-124", stats1, 150L);
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, response4);
    }

    @Test
    @DisplayName("Record hashCode works correctly")
    void testHashCode() {
        BasicTextStatistics stats1 = new BasicTextStatistics(100, 5, 500, 400, 3);
        BasicTextStatistics stats2 = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response1 = new TextAnalysisResponse("doc-123", stats1, 150L);
        TextAnalysisResponse response2 = new TextAnalysisResponse("doc-123", stats2, 150L);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("toString returns useful representation")
    void testToString() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, 150L);
        String result = response.toString();
        assertTrue(result.contains("TextAnalysisResponse"));
        assertTrue(result.contains("doc-123"));
        assertTrue(result.contains("150"));
    }

    @Test
    @DisplayName("Edge case: empty stats")
    void edgeCase_emptyStats() {
        BasicTextStatistics stats = new BasicTextStatistics(0, 0, 0, 0, 0);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-empty", stats, 5L);
        assertEquals("doc-empty", response.documentId());
        assertEquals(0, response.basicStats().wordCount());
        assertEquals(5L, response.processingTimeMs());
    }

    @Test
    @DisplayName("Edge case: UUID-style documentId")
    void edgeCase_uuidDocumentId() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        TextAnalysisResponse response = new TextAnalysisResponse(uuid, stats, 150L);
        assertEquals(uuid, response.documentId());
    }

    @Test
    @DisplayName("Edge case: very fast processing")
    void edgeCase_veryFastProcessing() {
        BasicTextStatistics stats = new BasicTextStatistics(5, 1, 20, 15, 1);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-fast", stats, 1L);
        assertEquals(1L, response.processingTimeMs());
    }

    @Test
    @DisplayName("Edge case: very slow processing")
    void edgeCase_verySlowProcessing() {
        BasicTextStatistics stats = new BasicTextStatistics(10000, 500, 50000, 40000, 100);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-slow", stats, 60000L);
        assertEquals(60000L, response.processingTimeMs());
    }

    @Test
    @DisplayName("basicStats is accessible")
    void basicStats_accessible() {
        BasicTextStatistics stats = new BasicTextStatistics(100, 5, 500, 400, 3);
        TextAnalysisResponse response = new TextAnalysisResponse("doc-123", stats, 150L);
        assertNotNull(response.basicStats());
        assertEquals(100, response.basicStats().wordCount());
        assertEquals(5, response.basicStats().sentenceCount());
    }

}
