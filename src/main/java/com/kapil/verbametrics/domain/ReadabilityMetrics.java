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
        BaseDomainResult.validateNonNegative(fleschKincaidScore, "Flesch-Kincaid score");
        BaseDomainResult.validateRange(fleschReadingEase, 0, 100, "Flesch Reading Ease");
        BaseDomainResult.validateNonNegative(averageSentenceLength, "Average sentence length");
        BaseDomainResult.validateNonNegative(averageSyllablesPerWord, "Average syllables per word");
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
