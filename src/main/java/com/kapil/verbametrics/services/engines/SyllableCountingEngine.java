package com.kapil.verbametrics.services.engines;

import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Business logic engine for syllable counting operations.
 * Orchestrates syllable counting using different strategies.
 *
 * @author Kapil Garg
 */
@Component
public class SyllableCountingEngine {

    public SyllableCountingEngine() {
    }

    /**
     * Calculates average syllables per word.
     *
     * @param words array of words
     * @return average syllables per word
     */
    public double calculateAverageSyllablesPerWord(String[] words) {
        if (words == null || words.length == 0) {
            return 0.0;
        }
        long validWords = Arrays.stream(words)
                .filter(word -> word != null && !word.trim().isEmpty())
                .count();
        if (validWords == 0) {
            return 0.0;
        }
        int totalSyllables = countSyllables(words);
        return (double) totalSyllables / validWords;
    }

    /**
     * Counts syllables in multiple words.
     *
     * @param words array of words to count syllables for
     * @return total number of syllables
     */
    public int countSyllables(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }
        return Arrays.stream(words)
                .filter(word -> word != null && !word.trim().isEmpty())
                .mapToInt(this::countSyllables)
                .sum();
    }

    /**
     * Counts syllables in a single word.
     *
     * @param word the word to count syllables for
     * @return number of syllables
     */
    public int countSyllables(String word) {
        if (word == null) {
            return 0;
        }
        String w = normalizeWord(word);
        if (w.isEmpty()) {
            return 0;
        }
        String normalized = removeTrailingSilentE(w);
        int count = countVowelGroups(normalized);
        return Math.max(1, count);
    }

    /**
     * Normalizes a word by trimming, lowercasing, and removing non-letters.
     *
     * @param word the word to normalize
     * @return normalized word
     */
    private String normalizeWord(String word) {
        if (word == null) {
            return "";
        }
        String w = word.trim().toLowerCase();
        return w.replaceAll("[^a-z]", "");
    }

    /**
     * Removes trailing silent 'e' from a word if present.
     *
     * @param word the word to process
     * @return word without trailing silent 'e'
     */
    private String removeTrailingSilentE(String word) {
        if (word.endsWith("e") && word.length() > 1) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    /**
     * Counts vowel groups in a word.
     *
     * @param word the word to analyze
     * @return number of vowel groups
     */
    private int countVowelGroups(String word) {
        String[] groups = word.split("[^aeiouy]+");
        int count = 0;
        for (String g : groups) {
            if (!g.isEmpty()) count++;
        }
        return count;
    }

}
