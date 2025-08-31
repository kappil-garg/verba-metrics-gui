package com.kapil.verbametrics.constants;

/**
 * Constants used in text analysis and processing.
 *
 * @author Kapil Garg
 */
public final class TextAnalysisConstants {
    
    private TextAnalysisConstants() {}

    public static final String WHITESPACE_REGEX = "\\s+";
    public static final String PARAGRAPH_REGEX = "\\n\\s*\\n";
    public static final String SENTENCE_ENDINGS_REGEX = "[.!?]+";

}
