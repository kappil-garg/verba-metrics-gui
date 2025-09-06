package com.kapil.verbametrics.services;

import com.kapil.verbametrics.domain.ReadabilityMetrics;

/**
 * Service interface for readability analysis operations.
 *
 * @author Kapil Garg
 */
public interface ReadabilityAnalysisService {
    
    /**
     * Analyzes the readability of the given text.
     *
     * @param text the text to analyze
     * @return ReadabilityMetrics containing readability analysis results
     */
    ReadabilityMetrics analyzeReadability(String text);
    
    /**
     * Analyzes the readability of the given text with custom parameters.
     *
     * @param text the text to analyze
     * @param includeComplexity whether to include complexity analysis
     * @return ReadabilityMetrics containing readability analysis results
     */
    ReadabilityMetrics analyzeReadability(String text, boolean includeComplexity);
    
}
