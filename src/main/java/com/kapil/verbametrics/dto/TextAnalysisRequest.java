package com.kapil.verbametrics.dto;

/**
 * Request DTO for text analysis operations.
 *
 * @author Kapil Garg
 */
public record TextAnalysisRequest(
        String text
) {

    public TextAnalysisRequest {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text content cannot be null or empty");
        }
    }

}
