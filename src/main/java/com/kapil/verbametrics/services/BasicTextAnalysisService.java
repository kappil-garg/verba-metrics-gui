package com.kapil.verbametrics.services;

import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;

/**
 * Service interface for basic text analysis operations.
 *
 * @author Kapil Garg
 */
public interface BasicTextAnalysisService {

    /**
     * Performs basic text analysis including word count, sentence count, character count, and paragraph count.
     *
     * @param request The text analysis request
     * @return Text analysis response with basic statistics
     */
    TextAnalysisResponse analyzeText(TextAnalysisRequest request);

}
