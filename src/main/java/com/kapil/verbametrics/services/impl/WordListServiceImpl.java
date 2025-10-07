package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.services.WordListService;
import com.kapil.verbametrics.services.managers.WordListCacheManager;
import com.kapil.verbametrics.services.managers.WordListFileLoader;
import com.kapil.verbametrics.services.models.ProcessedWordLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Implementation of WordListService for managing word lists used in text analysis.
 * Provides functionality to load, cache, and retrieve positive and negative word lists.
 *
 * @author Kapil Garg
 */
@Service
public class WordListServiceImpl implements WordListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordListServiceImpl.class);

    private final SentimentAnalysisProperties properties;
    private final WordListFileLoader fileLoader;
    private final WordListCacheManager cacheManager;

    @Autowired
    public WordListServiceImpl(SentimentAnalysisProperties properties,
                               WordListFileLoader fileLoader,
                               WordListCacheManager cacheManager) {
        this.properties = properties;
        this.fileLoader = fileLoader;
        this.cacheManager = cacheManager;
        loadWordLists();
    }

    @Override
    public Set<String> getPositiveWords() {
        return cacheManager.getPositiveWords();
    }

    @Override
    public Set<String> getNegativeWords() {
        return cacheManager.getNegativeWords();
    }

    @Override
    public List<String> loadWordsFromFile(String filePath) {
        return fileLoader.loadWordsFromFile(filePath);
    }

    @Override
    public void refreshWordLists() {
        LOGGER.info("Refreshing word lists");
        loadWordLists();
    }

    /**
     * Loads word lists from files or configuration and updates the cache.
     * Measures and logs the time taken to load the lists.
     */
    private void loadWordLists() {
        LOGGER.debug("Loading word lists...");
        long startTime = System.currentTimeMillis();
        boolean caseInsensitive = !properties.getTextProcessing().isCaseSensitive();
        Set<String> positive = loadAndProcessWordList(
                properties.getWordLists().getPositiveWordsPath(),
                properties.getWordLists().getPositiveWords(),
                caseInsensitive);
        Set<String> negative = loadAndProcessWordList(
                properties.getWordLists().getNegativeWordsPath(),
                properties.getWordLists().getNegativeWords(),
                caseInsensitive);
        ProcessedWordLists processedLists = new ProcessedWordLists(positive, negative);
        cacheManager.updateCache(processedLists);
        long loadTime = System.currentTimeMillis() - startTime;
        LOGGER.debug("Word lists loaded in {}ms - Positive: {}, Negative: {}",
                loadTime, processedLists.positiveWords().size(), processedLists.negativeWords().size());
    }

    /**
     * Loads and processes a word list from file or configuration.
     *
     * @param filePath        the path to the word list file
     * @param configWords     the fallback list from configuration
     * @param caseInsensitive whether to treat words case-insensitively
     * @return a set of processed words
     */
    private Set<String> loadAndProcessWordList(String filePath, List<String> configWords, boolean caseInsensitive) {
        List<String> wordList = getWordList(filePath, configWords);
        return toProcessedSet(wordList, caseInsensitive);
    }

    /**
     * Retrieves word list from file or configuration.
     *
     * @param filePath    the path to the word list file
     * @param configWords the fallback list from configuration
     * @return the list of words
     */
    private List<String> getWordList(String filePath, List<String> configWords) {
        List<String> fileWords = loadWordsFromFile(filePath);
        return fileWords.isEmpty() ? configWords : fileWords;
    }

    /**
     * Normalizes and collects input words into a set based on case sensitivity.
     *
     * @param words           the list of words to process
     * @param caseInsensitive whether to treat words case-insensitively
     * @return a set of processed words
     */
    private java.util.Set<String> toProcessedSet(java.util.List<String> words, boolean caseInsensitive) {
        java.util.function.Function<String, String> normalizer = caseInsensitive
                ? s -> s.toLowerCase(java.util.Locale.ROOT)
                : s -> s;
        java.util.Set<String> result = java.util.concurrent.ConcurrentHashMap.newKeySet();
        words.stream()
                .filter(w -> w != null && !w.isBlank())
                .map(String::trim)
                .map(normalizer)
                .forEach(result::add);
        return result;
    }

}
