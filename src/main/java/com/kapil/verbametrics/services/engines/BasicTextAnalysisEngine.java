package com.kapil.verbametrics.services.engines;

import com.kapil.verbametrics.domain.BasicTextStatistics;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * BasicTextAnalysisEngine provides fundamental text analysis functionalities.
 * It computes basic statistics such as word count, sentence count, character count, and paragraph count.
 *
 * @author Kapil Garg
 */
@Component
public class BasicTextAnalysisEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTextAnalysisEngine.class);

    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile(VerbaMetricsConstants.PARAGRAPH_REGEX);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(VerbaMetricsConstants.WHITESPACE_REGEX);
    private static final Pattern SENTENCE_ENDINGS_PATTERN = Pattern.compile(VerbaMetricsConstants.SENTENCE_ENDINGS_REGEX);

    /**
     * Analyzes the provided text and computes basic statistics.
     *
     * @param request The text analysis request containing the text to analyze.
     * @return A response containing the analysis results and processing time.
     */
    public TextAnalysisResponse analyze(TextAnalysisRequest request) {
        LOGGER.debug("Starting text analysis for document");
        long startTime = System.currentTimeMillis();
        String text = request.text();
        String documentId = UUID.randomUUID().toString();
        BasicTextStatistics stats = calculateStatistics(text);
        long processingTime = System.currentTimeMillis() - startTime;
        LOGGER.debug("Text analysis completed in {}ms for document {}", processingTime, documentId);
        return new TextAnalysisResponse(documentId, stats, processingTime);
    }

    /**
     * Calculates basic text statistics including word count, sentence count, character count, and paragraph count.
     *
     * @param text The text to analyze.
     * @return An object containing the calculated statistics.
     */
    private BasicTextStatistics calculateStatistics(String text) {
        if (text == null || text.isBlank()) {
            return new BasicTextStatistics(0, 0, 0, 0, 0);
        }
        String trimmedText = text.trim();
        int wordCount = countWords(trimmedText);
        int characterCount = text.length();
        int characterCountNoSpaces = text.replaceAll("\\s", "").length();
        int sentenceCount = countSentences(trimmedText);
        int paragraphCount = countParagraphs(trimmedText);
        return new BasicTextStatistics(wordCount, sentenceCount, characterCount, characterCountNoSpaces, paragraphCount);
    }

    /**
     * Counts the number of words in the given text.
     *
     * @param text The text to analyze.
     * @return The word count.
     */
    private int countWords(String text) {
        if (text.isBlank()) {
            return 0;
        }
        String[] words = WHITESPACE_PATTERN.split(text);
        return words.length == 1 && words[0].isEmpty() ? 0 : words.length;
    }

    /**
     * Counts the number of sentences in the given text.
     *
     * @param text The text to analyze.
     * @return The sentence count.
     */
    private int countSentences(String text) {
        if (text.isBlank()) {
            return 0;
        }
        String[] sentences = SENTENCE_ENDINGS_PATTERN.split(text);
        return sentences.length;
    }

    /**
     * Counts the number of paragraphs in the given text.
     *
     * @param text The text to analyze.
     * @return The paragraph count.
     */
    private int countParagraphs(String text) {
        if (text.isBlank()) {
            return 0;
        }
        String[] paragraphs = PARAGRAPH_PATTERN.split(text);
        return paragraphs.length;
    }

}
