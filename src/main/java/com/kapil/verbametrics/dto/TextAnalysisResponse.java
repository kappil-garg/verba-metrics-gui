package com.kapil.verbametrics.dto;

import com.kapil.verbametrics.domain.BasicTextStatistics;

/**
 * Response DTO for basic text analysis operations.
 *
 * @author Kapil Garg
 */
public record TextAnalysisResponse(
        String documentId,
        BasicTextStatistics basicStats,
        long processingTimeMs
) {
}
