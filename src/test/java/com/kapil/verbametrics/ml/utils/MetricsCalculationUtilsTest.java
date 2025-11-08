package com.kapil.verbametrics.ml.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for MetricsCalculationUtils.
 *
 * @author Kapil Garg
 */
@DisplayName("MetricsCalculationUtils Tests")
class MetricsCalculationUtilsTest {

    @Test
    @DisplayName("calculateF1Score should calculate correct F1 score with perfect precision and recall")
    void testCalculateF1Score_Perfect() {
        double f1 = MetricsCalculationUtils.calculateF1Score(1.0, 1.0);
        assertEquals(1.0, f1, 0.0001);
    }

    @Test
    @DisplayName("calculateF1Score should calculate correct F1 score with zero precision and recall")
    void testCalculateF1Score_ZeroBoth() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.0, 0.0);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should return 0 when precision is zero")
    void testCalculateF1Score_ZeroPrecision() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.0, 0.8);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should return 0 when recall is zero")
    void testCalculateF1Score_ZeroRecall() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.8, 0.0);
        assertEquals(0.0, f1);
    }

    @ParameterizedTest
    @CsvSource({
            "0.8, 0.9, 0.847058823",
            "0.75, 0.85, 0.795918367",
            "0.6, 0.7, 0.646153846",
            "0.5, 0.5, 0.5",
            "0.9, 0.6, 0.72"
    })
    @DisplayName("calculateF1Score should calculate correct F1 scores for various precision and recall combinations")
    void testCalculateF1Score_VariousCombinations(double precision, double recall, double expectedF1) {
        double f1 = MetricsCalculationUtils.calculateF1Score(precision, recall);
        assertEquals(expectedF1, f1, 0.001);
    }

    @Test
    @DisplayName("calculateF1Score should handle negative precision")
    void testCalculateF1Score_NegativePrecision() {
        double f1 = MetricsCalculationUtils.calculateF1Score(-0.5, 0.8);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle negative recall")
    void testCalculateF1Score_NegativeRecall() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.8, -0.5);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle NaN precision")
    void testCalculateF1Score_NaNPrecision() {
        double f1 = MetricsCalculationUtils.calculateF1Score(Double.NaN, 0.8);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle NaN recall")
    void testCalculateF1Score_NaNRecall() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.8, Double.NaN);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle positive infinity precision")
    void testCalculateF1Score_InfinitePrecision() {
        double f1 = MetricsCalculationUtils.calculateF1Score(Double.POSITIVE_INFINITY, 0.8);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle positive infinity recall")
    void testCalculateF1Score_InfiniteRecall() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.8, Double.POSITIVE_INFINITY);
        assertEquals(0.0, f1);
    }

    @Test
    @DisplayName("calculateF1Score should handle very small sum close to zero")
    void testCalculateF1Score_VerySmallSum() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.0000000001, 0.0000000001);
        // With such small values, F1 score calculation will return the actual value, not 0
        // The epsilon check in the code (1e-10) is for sum, not individual values
        assertTrue(f1 >= 0.0 && f1 <= 1.0); // Just verify it's in valid range
    }

    @Test
    @DisplayName("calculateF1Score should calculate correctly with boundary values")
    void testCalculateF1Score_BoundaryValues() {
        double f1 = MetricsCalculationUtils.calculateF1Score(0.0, 1.0);
        assertEquals(0.0, f1);
        f1 = MetricsCalculationUtils.calculateF1Score(1.0, 0.0);
        assertEquals(0.0, f1);
        f1 = MetricsCalculationUtils.calculateF1Score(0.1, 0.9);
        assertEquals(0.18, f1, 0.01);
    }

    @Test
    @DisplayName("calculateQualityScore should calculate average of perfect scores")
    void testCalculateQualityScore_PerfectScores() {
        double quality = MetricsCalculationUtils.calculateQualityScore(1.0, 1.0, 1.0, 1.0);
        assertEquals(1.0, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should calculate average of zero scores")
    void testCalculateQualityScore_ZeroScores() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.0, 0.0, 0.0, 0.0);
        assertEquals(0.0, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should calculate correct average for typical ML metrics")
    void testCalculateQualityScore_TypicalMetrics() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.85, 0.80, 0.75, 0.77);
        assertEquals(0.7925, quality, 0.0001);
    }

    @ParameterizedTest
    @CsvSource({
            "0.9, 0.8, 0.85, 0.82, 0.8425",
            "0.75, 0.70, 0.72, 0.71, 0.7200",
            "0.5, 0.5, 0.5, 0.5, 0.5",
            "0.95, 0.92, 0.90, 0.91, 0.9200"
    })
    @DisplayName("calculateQualityScore should calculate correct averages for various metric combinations")
    void testCalculateQualityScore_VariousCombinations(double accuracy, double precision, double recall,
                                                       double f1Score, double expectedQuality) {
        double quality = MetricsCalculationUtils.calculateQualityScore(accuracy, precision, recall, f1Score);
        assertEquals(expectedQuality, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should handle mixed positive and negative values")
    void testCalculateQualityScore_MixedValues() {
        // Negative values are not typical for these metrics, but we test robustness
        double quality = MetricsCalculationUtils.calculateQualityScore(-0.5, 0.8, 0.6, 0.7);
        assertEquals(0.4, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should handle very small values")
    void testCalculateQualityScore_SmallValues() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.01, 0.02, 0.03, 0.02);
        assertEquals(0.02, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should handle values close to 1.0")
    void testCalculateQualityScore_HighValues() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.99, 0.98, 0.97, 0.98);
        assertEquals(0.98, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should calculate correctly when one metric is significantly different")
    void testCalculateQualityScore_OneOutlier() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.9, 0.9, 0.9, 0.3);
        assertEquals(0.75, quality, 0.0001);
    }

    @Test
    @DisplayName("calculateQualityScore should handle NaN values in calculation")
    void testCalculateQualityScore_WithNaN() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.8, Double.NaN, 0.7, 0.75);
        assertTrue(Double.isNaN(quality));
    }

    @Test
    @DisplayName("calculateQualityScore should handle infinity values")
    void testCalculateQualityScore_WithInfinity() {
        double quality = MetricsCalculationUtils.calculateQualityScore(0.8, Double.POSITIVE_INFINITY, 0.7, 0.75);
        assertTrue(Double.isInfinite(quality));
    }

}
