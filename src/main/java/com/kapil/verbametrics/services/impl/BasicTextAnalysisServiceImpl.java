package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.constants.TextAnalysisConstants;
import com.kapil.verbametrics.domain.BasicTextStatistics;
import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.BasicTextAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implementation of BasicTextAnalysisService that provides basic text analysis functionalities.
 *
 * @author Kapil Garg
 */
@Service
public class BasicTextAnalysisServiceImpl implements BasicTextAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTextAnalysisServiceImpl.class.getName());

    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile(TextAnalysisConstants.PARAGRAPH_REGEX);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(TextAnalysisConstants.WHITESPACE_REGEX);
    private static final Pattern SENTENCE_ENDINGS_PATTERN = Pattern.compile(TextAnalysisConstants.SENTENCE_ENDINGS_REGEX);

    @Override
    public TextAnalysisResponse analyzeText(TextAnalysisRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        LOGGER.debug("Starting text analysis for document");
        long startTime = System.currentTimeMillis();
        try {
            String text = request.text();
            String documentId = UUID.randomUUID().toString();
            BasicTextStatistics stats = calculateBasicStatistics(text);
            long processingTime = System.currentTimeMillis() - startTime;
            LOGGER.debug("Text analysis completed in {}ms for document {}", processingTime, documentId);
            return new TextAnalysisResponse(documentId, stats, processingTime);
        } catch (Exception e) {
            LOGGER.error("Failed to analyze text", e);
            throw new RuntimeException("Failed to analyze text: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates basic text statistics including word count, sentence count, character count, and paragraph count.
     *
     * @param text the input text
     * @return BasicTextStatistics object containing the calculated statistics
     */
    private BasicTextStatistics calculateBasicStatistics(String text) {
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
     * Words are defined as sequences of characters separated by whitespace.
     *
     * @param text the input text
     * @return the number of words
     */
    private int countWords(String text) {
        if (text.isBlank()) return 0;
        String[] words = WHITESPACE_PATTERN.split(text);
        return words.length == 1 && words[0].isEmpty() ? 0 : words.length;
    }

    /**
     * Counts the number of sentences in the given text.
     * Sentences are defined as sequences ending with '.', '!', or '?'.
     *
     * @param text the input text
     * @return the number of sentences
     */
    private int countSentences(String text) {
        if (text.isBlank()) return 0;
        String[] sentences = SENTENCE_ENDINGS_PATTERN.split(text);
        return sentences.length;
    }

    /**
     * Counts the number of paragraphs in the given text.
     * Paragraphs are defined as blocks of text separated by one or more blank lines.
     *
     * @param text the input text
     * @return the number of paragraphs
     */
    private int countParagraphs(String text) {
        if (text.isBlank()) return 0;
        String[] paragraphs = PARAGRAPH_PATTERN.split(text);
        return paragraphs.length;
    }

}
