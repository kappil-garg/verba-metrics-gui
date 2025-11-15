package com.kapil.verbametrics.ui.controller;

import com.kapil.verbametrics.domain.BasicTextStatistics;
import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.BasicTextAnalysisService;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import com.kapil.verbametrics.ui.util.GuiConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for TextAnalysisController.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class TextAnalysisControllerTest {

    @Mock
    private BasicTextAnalysisService basicService;

    @Mock
    private SentimentAnalysisService sentimentService;

    @Mock
    private ReadabilityAnalysisService readabilityService;

    private TextAnalysisController controller;

    @BeforeEach
    void setUp() {
        controller = new TextAnalysisController(basicService, sentimentService, readabilityService);
    }

    @Test
    @DisplayName("analyze returns combined analysis result")
    void analyze_returnsCombinedResult() {
        String text = "This is a test text.";
        BasicTextStatistics stats = new BasicTextStatistics(4, 1, 19, 0, 1);
        TextAnalysisResponse basicResponse = new TextAnalysisResponse("doc-1", stats, 10L);
        SentimentScore sentiment = new SentimentScore("NEUTRAL", 0.5, 0.0);
        ReadabilityMetrics readability = new ReadabilityMetrics(8.0, 60.0, "Grade 8", "Moderate", 15.0, 1.5);
        when(basicService.analyzeText(any(TextAnalysisRequest.class))).thenReturn(basicResponse);
        when(sentimentService.analyzeSentiment(text)).thenReturn(sentiment);
        when(readabilityService.analyzeReadability(text)).thenReturn(readability);
        TextAnalysisController.AnalysisResult result = controller.analyze(text);
        assertNotNull(result);
        assertEquals(basicResponse, result.basic());
        assertEquals(sentiment, result.sentiment());
        assertEquals(readability, result.readability());
    }

    @Test
    @DisplayName("analyze throws exception when text is null")
    void analyze_throwsExceptionWhenTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> controller.analyze(null));
    }

    @Test
    @DisplayName("analyze throws exception when text is blank")
    void analyze_throwsExceptionWhenTextIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> controller.analyze("   "));
        assertThrows(IllegalArgumentException.class, () -> controller.analyze(""));
    }

    @Test
    @DisplayName("analyze throws exception when text exceeds max length")
    void analyze_throwsExceptionWhenTextExceedsMaxLength() {
        String longText = "a".repeat(GuiConstants.MAX_TEXT_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> controller.analyze(longText));
    }

    @Test
    @DisplayName("analyze accepts text at max length")
    void analyze_acceptsTextAtMaxLength() {
        String maxLengthText = "a".repeat(GuiConstants.MAX_TEXT_LENGTH);
        BasicTextStatistics stats = new BasicTextStatistics(1, 1, GuiConstants.MAX_TEXT_LENGTH, 0, 1);
        TextAnalysisResponse basicResponse = new TextAnalysisResponse("doc-1", stats, 10L);
        SentimentScore sentiment = new SentimentScore("NEUTRAL", 0.5, 0.0);
        ReadabilityMetrics readability = new ReadabilityMetrics(8.0, 60.0, "Grade 8", "Moderate", 15.0, 1.5);
        when(basicService.analyzeText(any(TextAnalysisRequest.class))).thenReturn(basicResponse);
        when(sentimentService.analyzeSentiment(maxLengthText)).thenReturn(sentiment);
        when(readabilityService.analyzeReadability(maxLengthText)).thenReturn(readability);
        assertDoesNotThrow(() -> controller.analyze(maxLengthText));
    }

    @Test
    @DisplayName("analyze processes normal text successfully")
    void analyze_processesNormalText() {
        String text = "Hello world! This is a sample text for testing.";
        BasicTextStatistics stats = new BasicTextStatistics(8, 1, 47, 0, 1);
        TextAnalysisResponse basicResponse = new TextAnalysisResponse("doc-1", stats, 10L);
        SentimentScore sentiment = new SentimentScore("POSITIVE", 0.3, 0.5);
        ReadabilityMetrics readability = new ReadabilityMetrics(6.0, 70.0, "Grade 6", "Easy", 12.0, 1.3);
        when(basicService.analyzeText(any(TextAnalysisRequest.class))).thenReturn(basicResponse);
        when(sentimentService.analyzeSentiment(text)).thenReturn(sentiment);
        when(readabilityService.analyzeReadability(text)).thenReturn(readability);
        TextAnalysisController.AnalysisResult result = controller.analyze(text);
        assertNotNull(result);
        assertEquals(basicResponse, result.basic());
        assertEquals(sentiment, result.sentiment());
        assertEquals(readability, result.readability());
    }

}
