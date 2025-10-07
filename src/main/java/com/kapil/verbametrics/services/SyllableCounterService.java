package com.kapil.verbametrics.services;

/**
 * Service for counting syllables in words and calculating average syllables per word.
 *
 * @author Kapil Garg
 */
public interface SyllableCounterService {

    /**
     * Counts the number of syllables in a word.
     *
     * @param word the word to analyze
     * @return the number of syllables
     */
    int countSyllables(String word);

    /**
     * Counts syllables for multiple words.
     *
     * @param words array of words to analyze
     * @return total syllable count
     */
    int countSyllables(String[] words);

    /**
     * Calculates average syllables per word.
     *
     * @param words array of words to analyze
     * @return average syllables per word
     */
    double calculateAverageSyllablesPerWord(String[] words);

}
