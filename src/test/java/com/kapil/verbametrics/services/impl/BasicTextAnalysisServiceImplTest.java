package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.domain.BasicTextStatistics;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.engines.BasicTextAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for BasicTextAnalysisServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class BasicTextAnalysisServiceImplTest {

    @Mock
    private BasicTextAnalysisEngine analysisEngine;

    private BasicTextAnalysisServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BasicTextAnalysisServiceImpl(analysisEngine);
    }

    @Test
    @DisplayName("analyzeText should delegate to engine and return response")
    void analyzeText_validRequest_returnsResponse() {
        TextAnalysisRequest request = new TextAnalysisRequest("Hello world");
        BasicTextStatistics basicStats = new BasicTextStatistics(2, 1, 11, 0, 1);
        TextAnalysisResponse expectedResponse = new TextAnalysisResponse(
                "doc-1", basicStats, 100L
        );
        when(analysisEngine.analyze(request)).thenReturn(expectedResponse);
        TextAnalysisResponse actualResponse = service.analyzeText(request);
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(expectedResponse, actualResponse, "Response should match expected");
        verify(analysisEngine).analyze(request);
        verifyNoMoreInteractions(analysisEngine);
    }

    @Test
    @DisplayName("analyzeText should throw NullPointerException for null request")
    void analyzeText_nullRequest_throwsException() {
        assertThrows(NullPointerException.class, () -> service.analyzeText(null),
                "Should throw NullPointerException for null request");
        verifyNoInteractions(analysisEngine);
    }

    @Test
    @DisplayName("analyzeText should pass through all request parameters")
    void analyzeText_withVariousParameters_passesThrough() {
        TextAnalysisRequest request = new TextAnalysisRequest("Test text content");
        BasicTextStatistics basicStats = new BasicTextStatistics(3, 1, 17, 0, 1);
        TextAnalysisResponse expectedResponse = new TextAnalysisResponse(
                "doc-2", basicStats, 50L
        );
        when(analysisEngine.analyze(any(TextAnalysisRequest.class))).thenReturn(expectedResponse);
        TextAnalysisResponse actualResponse = service.analyzeText(request);
        assertNotNull(actualResponse);
        verify(analysisEngine).analyze(request);
    }

    @Test
    @DisplayName("analyzeText should handle empty text")
    void analyzeText_emptyText_handledByEngine() {
        TextAnalysisRequest request = new TextAnalysisRequest("a");
        BasicTextStatistics basicStats = new BasicTextStatistics(1, 1, 1, 1, 1);
        TextAnalysisResponse expectedResponse = new TextAnalysisResponse(
                "doc-3", basicStats, 10L
        );
        when(analysisEngine.analyze(request)).thenReturn(expectedResponse);
        TextAnalysisResponse actualResponse = service.analyzeText(request);
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.basicStats().wordCount());
        verify(analysisEngine).analyze(request);
    }

    @Test
    @DisplayName("analyzeText should propagate engine exceptions")
    void analyzeText_engineThrowsException_propagatesException() {
        TextAnalysisRequest request = new TextAnalysisRequest("Test");
        RuntimeException expectedException = new RuntimeException("Engine error");
        when(analysisEngine.analyze(request)).thenThrow(expectedException);
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> service.analyzeText(request),
                "Should propagate engine exception");
        assertEquals("Engine error", thrown.getMessage());
        verify(analysisEngine).analyze(request);
    }

    @Test
    @DisplayName("Constructor should accept non-null engine")
    void constructor_withValidEngine_createsInstance() {
        assertDoesNotThrow(() -> new BasicTextAnalysisServiceImpl(analysisEngine),
                "Constructor should accept valid engine");
    }

    @Test
    @DisplayName("Service should handle multiple consecutive requests")
    void analyzeText_multipleRequests_handlesAll() {
        TextAnalysisRequest request1 = new TextAnalysisRequest("First");
        TextAnalysisRequest request2 = new TextAnalysisRequest("Second");
        BasicTextStatistics stats1 = new BasicTextStatistics(1, 1, 5, 5, 1);
        BasicTextStatistics stats2 = new BasicTextStatistics(1, 1, 6, 6, 1);
        TextAnalysisResponse response1 = new TextAnalysisResponse("doc-1", stats1, 10L);
        TextAnalysisResponse response2 = new TextAnalysisResponse("doc-2", stats2, 20L);
        when(analysisEngine.analyze(request1)).thenReturn(response1);
        when(analysisEngine.analyze(request2)).thenReturn(response2);
        TextAnalysisResponse actual1 = service.analyzeText(request1);
        TextAnalysisResponse actual2 = service.analyzeText(request2);
        assertEquals(response1, actual1);
        assertEquals(response2, actual2);
        verify(analysisEngine).analyze(request1);
        verify(analysisEngine).analyze(request2);
    }

}
