package com.kapil.verbametrics.services.classifiers;

import com.kapil.verbametrics.config.SentimentAnalysisProperties;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Classifier for determining sentiment labels.
 * Handles the logic for converting sentiment scores to labels.
 *
 * @author Kapil Garg
 */
@Component
public class SentimentLabelClassifier {

    private final SentimentAnalysisProperties properties;

    @Autowired
    public SentimentLabelClassifier(SentimentAnalysisProperties properties) {
        this.properties = properties;
    }

    /**
     * Determines the sentiment label based on the score.
     *
     * @param score the sentiment score
     * @return the sentiment label
     */
    public String determineSentimentLabel(double score) {
        if (score > properties.getThresholds().getPositive()) {
            return VerbaMetricsConstants.POSITIVE;
        } else if (score < properties.getThresholds().getNegative()) {
            return VerbaMetricsConstants.NEGATIVE;
        } else {
            return VerbaMetricsConstants.NEUTRAL;
        }
    }

}
