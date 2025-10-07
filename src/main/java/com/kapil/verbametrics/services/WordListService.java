package com.kapil.verbametrics.services;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing word lists used in text analysis.
 *
 * @author Kapil Garg
 */
public interface WordListService {

    /**
     * Gets the set of positive words for sentiment analysis.
     *
     * @return set of positive words
     */
    Set<String> getPositiveWords();

    /**
     * Gets the set of negative words for sentiment analysis.
     *
     * @return set of negative words
     */
    Set<String> getNegativeWords();

    /**
     * Loads words from a file path.
     *
     * @param filePath the path to the word list file
     * @return list of words from the file
     */
    List<String> loadWordsFromFile(String filePath);

    /**
     * Refreshes the word lists from configuration.
     */
    void refreshWordLists();

}
