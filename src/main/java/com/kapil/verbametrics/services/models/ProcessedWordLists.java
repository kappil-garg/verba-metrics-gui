package com.kapil.verbametrics.services.models;

import java.util.Set;

/**
 * Holds processed positive and negative word lists.
 *
 * @author Kapil Garg
 */
public record ProcessedWordLists(
        Set<String> positiveWords,
        Set<String> negativeWords
) {

}
