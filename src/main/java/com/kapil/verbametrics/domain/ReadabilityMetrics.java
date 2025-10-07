package com.kapil.verbametrics.domain;

/**
 * Domain record representing readability analysis results.
 *
 * @author Kapil Garg
 */
public record ReadabilityMetrics(
        double fleschKincaidScore,
        double fleschReadingEase,
        String readingLevel,
        String complexity,
        double averageSentenceLength,
        double averageSyllablesPerWord
) {

    public ReadabilityMetrics {
        validateInputs(fleschKincaidScore, fleschReadingEase, averageSentenceLength, averageSyllablesPerWord);
    }

    /**
     * Validates the input parameters to ensure they are valid.
     *
     * @param fleschKincaidScore      the Flesch-Kincaid score
     * @param fleschReadingEase       the Flesch Reading Ease score
     * @param averageSentenceLength   the average sentence length
     * @param averageSyllablesPerWord the average syllables per word
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(double fleschKincaidScore, double fleschReadingEase,
                                       double averageSentenceLength, double averageSyllablesPerWord) {
        if (fleschKincaidScore < 0) {
            throw new IllegalArgumentException("Flesch-Kincaid score cannot be negative");
        }
        if (fleschReadingEase < 0 || fleschReadingEase > 100) {
            throw new IllegalArgumentException("Flesch Reading Ease must be between 0 and 100");
        }
        if (averageSentenceLength < 0) {
            throw new IllegalArgumentException("Average sentence length cannot be negative");
        }
        if (averageSyllablesPerWord < 0) {
            throw new IllegalArgumentException("Average syllables per word cannot be negative");
        }
    }

    @Override
    public String toString() {
        return """
                ReadabilityMetrics{
                    fleschKincaidScore=%.2f, fleschReadingEase=%.2f,
                    readingLevel='%s', complexity='%s',
                    avgSentenceLength=%.2f, avgSyllablesPerWord=%.2f
                }""".formatted(
                fleschKincaidScore, fleschReadingEase, readingLevel, complexity,
                averageSentenceLength, averageSyllablesPerWord
        );
    }

}
