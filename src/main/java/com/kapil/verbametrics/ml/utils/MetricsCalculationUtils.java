package com.kapil.verbametrics.ml.utils;

/**
 * Utility class for calculating various machine learning metrics via centralized methods.
 *
 * @author Kapil Garg
 */
public class MetricsCalculationUtils {

    /**
     * Calculates F1-score from precision and recall.
     * F1-score is the harmonic mean of precision and recall.
     *
     * @param precision The precision value (0.0 to 1.0)
     * @param recall    The recall value (0.0 to 1.0)
     * @return F1-score value (0.0 to 1.0), or 0.0 if precision + recall = 0
     */
    public static double calculateF1Score(double precision, double recall) {
        // Handle edge cases: NaN, infinite values, or negative values
        if (Double.isNaN(precision) || Double.isNaN(recall) ||
                Double.isInfinite(precision) || Double.isInfinite(recall) ||
                precision < 0.0 || recall < 0.0) {
            return 0.0;
        }
        // Handle division by zero with floating-point precision tolerance
        double sum = precision + recall;
        if (Math.abs(sum) < 1e-10) { // Use epsilon for floating-point comparison
            return 0.0;
        }
        return 2.0 * (precision * recall) / sum;
    }

    /**
     * Calculates overall quality score as the average of multiple metrics.
     *
     * @param accuracy  The accuracy value
     * @param precision The precision value
     * @param recall    The recall value
     * @param f1Score   The F1-score value
     * @return Average quality score (0.0 to 1.0)
     */
    public static double calculateQualityScore(double accuracy, double precision, double recall, double f1Score) {
        return (accuracy + precision + recall + f1Score) / 4.0;
    }

}
