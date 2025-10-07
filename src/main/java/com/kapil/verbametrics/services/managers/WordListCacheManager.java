package com.kapil.verbametrics.services.managers;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.services.models.ProcessedWordLists;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages caching of positive and negative word lists for sentiment analysis.
 * Provides methods to update, clear, and query the cached word lists.
 *
 * @author Kapil Garg
 */
@Component
@Getter
public class WordListCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordListCacheManager.class);

    private final SentimentAnalysisProperties properties;

    private final Set<String> positiveWords = ConcurrentHashMap.newKeySet();
    private final Set<String> negativeWords = ConcurrentHashMap.newKeySet();

    @Autowired
    public WordListCacheManager(SentimentAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Updates the cache with processed word lists.
     *
     * @param processedLists the processed word lists to cache
     */
    public void updateCache(ProcessedWordLists processedLists) {
        clearCache();
        positiveWords.addAll(processedLists.positiveWords());
        negativeWords.addAll(processedLists.negativeWords());
        LOGGER.info("Cache updated - Positive: {}, Negative: {}", positiveWords.size(), negativeWords.size());
    }

    /**
     * Clears the cache.
     */
    public void clearCache() {
        positiveWords.clear();
        negativeWords.clear();
    }

}