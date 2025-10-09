package com.kapil.verbametrics.domain;

/**
 * Domain record representing basic text statistics with validation.
 *
 * @author Kapil Garg
 */
public record BasicTextStatistics(
        int wordCount,
        int sentenceCount,
        int characterCount,
        int characterCountNoSpaces,
        int paragraphCount
) {

    public BasicTextStatistics {
        BaseDomainResult.validateNonNegative(wordCount, "Word count");
        BaseDomainResult.validateNonNegative(sentenceCount, "Sentence count");
        BaseDomainResult.validateNonNegative(characterCount, "Character count");
        BaseDomainResult.validateNonNegative(characterCountNoSpaces, "Character count without spaces");
        BaseDomainResult.validateNonNegative(paragraphCount, "Paragraph count");
        if (characterCountNoSpaces > characterCount) {
            throw new IllegalArgumentException("Character count without spaces cannot exceed total character count");
        }
    }

    /**
     * Calculates the average number of words per sentence.
     *
     * @return average words per sentence, or 0.0 if no sentences
     */
    double averageWordsPerSentence() {
        return BaseDomainResult.safeDivide(wordCount, sentenceCount);
    }

    /**
     * Calculates the average number of characters per word.
     *
     * @return average characters per word, or 0.0 if no words
     */
    double averageCharactersPerWord() {
        return BaseDomainResult.safeDivide(characterCountNoSpaces, wordCount);
    }

    @Override
    public String toString() {
        return """
                BasicTextStatistics{
                    wordCount=%d, sentenceCount=%d, characterCount=%d,
                    characterCountNoSpaces=%d, paragraphCount=%d,
                    avgWordsPerSentence=%.2f, avgCharactersPerWord=%.2f
                }""".formatted(
                wordCount, sentenceCount, characterCount, characterCountNoSpaces,
                paragraphCount, averageWordsPerSentence(), averageCharactersPerWord()
        );
    }

}
