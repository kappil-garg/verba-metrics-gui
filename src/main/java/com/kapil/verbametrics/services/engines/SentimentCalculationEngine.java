package com.kapil.verbametrics.services.engines;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.config.SentimentRuleProperties;
import com.kapil.verbametrics.services.WordListService;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * Business logic engine for sentiment score calculation.
 * Handles the core logic for calculating sentiment scores from text.
 *
 * @author Kapil Garg
 */
@Component
public class SentimentCalculationEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentCalculationEngine.class);

    private final WordListService wordListService;
    private final SentimentRuleProperties ruleProperties;
    private final SentimentAnalysisProperties analysisProperties;

    @Autowired
    public SentimentCalculationEngine(WordListService wordListService, SentimentAnalysisProperties analysisProperties, SentimentRuleProperties ruleProperties) {
        this.wordListService = wordListService;
        this.analysisProperties = analysisProperties;
        this.ruleProperties = ruleProperties;
    }

    /**
     * Calculates the sentiment score based on positive and negative word sets.
     *
     * @param text the text to analyze
     * @return the sentiment score between -1.0 and 1.0
     */
    public double calculateSentimentScore(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        var tokens = tokenize(text);
        var totalWords = (int) Arrays.stream(tokens).filter(token -> !token.isBlank()).count();
        if (totalWords == 0) {
            return 0.0;
        }
        double phraseAdjustment = calculatePhraseAdjustments(text);
        double weightedSum = calculateWeightedSentiment(text, tokens) + phraseAdjustment;
        double denominator = Math.sqrt(weightedSum * weightedSum + ruleProperties.getNormalizationAlpha());
        return denominator > 0 ? weightedSum / denominator : 0.0;
    }

    /**
     * Tokenizes the input text based on the configured word separator and case sensitivity.
     * Handles contractions properly by preserving them as single tokens.
     *
     * @param text the text to tokenize
     * @return an array of tokens
     */
    private String[] tokenize(String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }
        String normalized = getNormalizedString(text);
        String[] tokens = normalized.split(analysisProperties.getTextProcessing().getWordSeparator());
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replace(VerbaMetricsConstants.APOSTROPHE_PLACEHOLDER, "'");
        }
        return tokens;
    }

    /**
     * Normalizes the input text based on case sensitivity and preprocessing rules.
     * <p>
     * Note: Hyphen normalization replaces all hyphens with spaces, which may break
     * hyphenated compound words (e.g., 'state-of-the-art' becomes 'state of the art').
     * This behavior can be disabled via the normalizeHyphens configuration property.
     *
     * @param text the text to normalize
     * @return the normalized string
     */
    private String getNormalizedString(String text) {
        boolean caseSensitive = analysisProperties.getTextProcessing().isCaseSensitive();
        boolean normalizeHyphens = analysisProperties.getTextProcessing().isNormalizeHyphens();
        String preprocessed = text;
        if (normalizeHyphens) {
            preprocessed = preprocessed.replace('-', ' ');
        }
        preprocessed = preprocessed.replaceAll("'", VerbaMetricsConstants.APOSTROPHE_PLACEHOLDER);
        return caseSensitive ? preprocessed.trim() : preprocessed.trim().toLowerCase();
    }

    /**
     * Calculates a weighted sentiment sum using negation and intensity heuristics.
     * Positive words contribute +1, negative words -1, modified by nearby intensifiers/diminishers.
     * Processes text sentence by sentence to properly reset context at sentence boundaries.
     *
     * @param rawText the original input text (for sentence boundary detection)
     * @param tokens  the tokenized input text
     * @return the weighted sentiment sum
     */
    private double calculateWeightedSentiment(String rawText, String[] tokens) {
        var positiveWords = wordListService.getPositiveWords();
        var negativeWords = wordListService.getNegativeWords();
        // Split into sentences and process each separately to reset context properly
        String[] sentences = splitIntoSentences(rawText);
        double sum = 0.0;
        for (String sentence : sentences) {
            if (sentence == null || sentence.isBlank()) continue;
            // Tokenize each sentence separately
            String[] sentenceTokens = tokenize(sentence);
            SentimentContext context = new SentimentContext();
            for (int i = 0; i < sentenceTokens.length; i++) {
                String token = sentenceTokens[i];
                if (token.isBlank()) {
                    continue;
                }
                if (handleSpecialTokens(token, i, sentenceTokens, context)) {
                    continue;
                }
                double contribution = processSentimentToken(token, i, sentenceTokens, positiveWords, negativeWords, context);
                sum += contribution;
            }
        }
        return sum;
    }

    /**
     * Splits text into sentences based on sentence-ending punctuation.
     * Sentence boundaries are periods, exclamation marks, or question marks followed by whitespace or end of text.
     *
     * @param text the text to split
     * @return array of sentences
     */
    private String[] splitIntoSentences(String text) {
        if (text == null || text.isBlank()) {
            return new String[]{text};
        }
        // Split on sentence-ending punctuation followed by whitespace or end of string
        // This regex matches: sentence-ending punctuation (. ! ?) followed by optional whitespace
        String[] sentences = text.split("(?<=[.!?])\\s+");
        return Arrays.stream(sentences)
                .filter(s -> s != null && !s.trim().isBlank())
                .toArray(String[]::new);
    }

    /**
     * Handles special tokens like punctuation, contrastives, and negations.
     *
     * @param token   current token
     * @param index   current index
     * @param tokens  all tokens
     * @param context sentiment context
     * @return true if token was handled and should be skipped
     */
    private boolean handleSpecialTokens(String token, int index, String[] tokens, SentimentContext context) {
        if (ruleProperties.getPunctuationBreaks().contains(token)) {
            resetContext(context);
            return true;
        }
        if (ruleProperties.getContrastives().contains(token)) {
            context.afterContrastive = true;
            context.contrastiveCountdown = ruleProperties.getContrastiveWindow();
            return true;
        }
        updateNegationState(token, index, tokens, context);
        return false;
    }

    /**
     * Processes a sentiment-bearing token and returns its contribution to the total score.
     *
     * @param token         current token
     * @param index         current index
     * @param tokens        all tokens
     * @param positiveWords set of positive words
     * @param negativeWords set of negative words
     * @param context       sentiment context
     * @return contribution to sentiment score
     */
    private double processSentimentToken(String token, int index, String[] tokens, Set<String> positiveWords, Set<String> negativeWords,
                                         SentimentContext context) {
        boolean isPositive = positiveWords.contains(token);
        boolean isNegative = negativeWords.contains(token);
        if (!isPositive && !isNegative) {
            return 0.0;
        }
        double valence = calculateValence(token, isPositive, isNegative, context);
        double modifier = calculateModifier(tokens, index, context);
        double contribution = valence * modifier;
        updateNegationWindow(context);
        return contribution;
    }

    /**
     * Resets the sentiment context.
     *
     * @param context the sentiment context
     */
    private void resetContext(SentimentContext context) {
        context.negationActive = false;
        context.negationWindow = 0;
        context.afterContrastive = false;
        context.contrastiveCountdown = 0;
    }

    /**
     * Updates negation state based on current token.
     *
     * @param token   the current token
     * @param index   the current index
     * @param tokens  the tokenized input text
     * @param context the sentiment context
     */
    private void updateNegationState(String token, int index, String[] tokens, SentimentContext context) {
        // Handle "not only" - this doesn't create negation
        if ("not".equals(token) && index + 1 < tokens.length && "only".equals(tokens[index + 1])) {
            context.negationActive = false;
            context.negationWindow = 0;
            return;
        }
        // Handle "not without" - this is a litotes (double negative = positive), so cancel negation
        if ("not".equals(token) && index + 1 < tokens.length && "without".equals(tokens[index + 1])) {
            context.negationActive = false;
            context.negationWindow = 0;
            return;
        }
        if (ruleProperties.getNegations().contains(token)) {
            // Handle double negations properly - if already negated, another negation cancels it
            if (context.negationActive) {
                context.negationActive = false;
                context.negationWindow = 0;
            } else {
                context.negationActive = true;
                context.negationWindow = ruleProperties.getNegationWindow();
            }
        }
    }

    /**
     * Calculates valence (positive/negative) with negation handling.
     *
     * @param token      the current token being processed
     * @param isPositive true if the token is positive
     * @param isNegative true if the token is negative
     * @param context    the sentiment context
     * @return the valence
     */
    private double calculateValence(String token, boolean isPositive, boolean isNegative, SentimentContext context) {
        if (isPositive && isNegative) {
            // A word can't be both positive and negative - this indicates data quality issues
            LOGGER.warn("Word '{}' found in both positive and negative word lists - this may indicate data quality issues in the sentiment lexicons", token);
            return 0.0;
        }
        double valence = isPositive ? 1.0 : -1.0;
        if (context.negationActive) {
            valence = -valence;
        }
        return valence;
    }

    /**
     * Calculates modifier based on boosters, dampeners, and contrastive weighting.
     *
     * @param tokens  the tokenized input text
     * @param index   the current index
     * @param context the sentiment context
     * @return the modifier
     */
    private double calculateModifier(String[] tokens, int index, SentimentContext context) {
        double modifier = 1.0;
        for (int j = Math.max(0, index - 2); j < index; j++) {
            String prev = tokens[j];
            if (prev.isBlank()) continue;

            Double boost = ruleProperties.getBoosters().get(prev);
            if (boost != null) {
                modifier += Math.abs(boost);
            }
            Double damp = ruleProperties.getDampeners().get(prev);
            if (damp != null) {
                modifier -= Math.abs(damp);
            }
        }
        if (modifier < 0.0) modifier = 0.0;
        if (context.afterContrastive) {
            modifier *= 1.2;  // Reduced from 1.5 to avoid overconfidence in mixed sentiment texts
            if (context.contrastiveCountdown > 0) {
                context.contrastiveCountdown--;
                if (context.contrastiveCountdown == 0) {
                    context.afterContrastive = false;
                }
            }
        }
        return modifier;
    }

    /**
     * Updates negation window after processing a sentiment-bearing token.
     *
     * @param context sentiment context
     */
    private void updateNegationWindow(SentimentContext context) {
        if (context.negationWindow > 0) {
            context.negationWindow--;
            if (context.negationWindow == 0) {
                context.negationActive = false;
            }
        }
    }

    /**
     * Apply fixed adjustments for multi-word expressions found in raw text.
     *
     * @param rawText the original input text
     * @return the cumulative adjustment from recognized phrases
     */
    private double calculatePhraseAdjustments(String rawText) {
        String text = rawText == null ? "" : rawText.toLowerCase();
        double adj = 0.0;
        for (var e : ruleProperties.getPhrases().entrySet()) {
            if (text.contains(e.getKey())) {
                adj += e.getValue();
            }
        }
        return adj;
    }

    /**
     * Context object to track sentiment processing state.
     */
    private static class SentimentContext {
        boolean negationActive = false;
        int negationWindow = 0;
        boolean afterContrastive = false;
        int contrastiveCountdown = 0;
    }

}
