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
        validateInputs(wordCount, sentenceCount, characterCount, characterCountNoSpaces, paragraphCount);
    }

    /**
     * Validates the input parameters to ensure they are non-negative and logically consistent.
     *
     * @param wordCount              the number of words
     * @param sentenceCount          the number of sentences
     * @param characterCount         the total number of characters
     * @param characterCountNoSpaces the number of characters excluding spaces
     * @param paragraphCount         the number of paragraphs
     * @throws IllegalArgumentException if any parameter is negative or inconsistent
     */
    private static void validateInputs(int wordCount, int sentenceCount, int characterCount,
                                       int characterCountNoSpaces, int paragraphCount) {
        if (wordCount < 0) throw new IllegalArgumentException("Word count cannot be negative");
        if (sentenceCount < 0) throw new IllegalArgumentException("Sentence count cannot be negative");
        if (characterCount < 0) throw new IllegalArgumentException("Character count cannot be negative");
        if (characterCountNoSpaces < 0)
            throw new IllegalArgumentException("Character count without spaces cannot be negative");
        if (paragraphCount < 0) throw new IllegalArgumentException("Paragraph count cannot be negative");
        if (characterCountNoSpaces > characterCount)
            throw new IllegalArgumentException("Character count without spaces cannot exceed total character count");
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

    /**
     * Calculates the average number of words per sentence.
     *
     * @return average words per sentence, or 0.0 if no sentences
     */
    double averageWordsPerSentence() {
        return sentenceCount > 0 ? (double) wordCount / sentenceCount : 0.0;
    }

    /**
     * Calculates the average number of characters per word.
     *
     * @return average characters per word, or 0.0 if no words
     */
    double averageCharactersPerWord() {
        return wordCount > 0 ? (double) characterCountNoSpaces / wordCount : 0.0;
    }

}
