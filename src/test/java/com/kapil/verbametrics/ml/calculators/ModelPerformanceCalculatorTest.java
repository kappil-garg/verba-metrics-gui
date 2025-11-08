package com.kapil.verbametrics.ml.calculators;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModelPerformanceCalculator.
 *
 * @author Kapil Garg
 */
@DisplayName("ModelPerformanceCalculator Tests")
class ModelPerformanceCalculatorTest {

    private MLModelProperties properties;
    private ModelPerformanceCalculator calculator;

    @BeforeEach
    void setUp() {
        properties = new MLModelProperties();
        Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("excellent", 0.9);
        thresholds.put("good", 0.8);
        thresholds.put("fair", 0.7);
        thresholds.put("poor", 0.6);
        properties.setPerformanceThresholds(thresholds);
        calculator = new ModelPerformanceCalculator(properties);
    }

    @Test
    @DisplayName("calculatePerformanceMetrics with TrainingResult should calculate all metrics correctly")
    void testCalculatePerformanceMetrics_TrainingResult() {
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.85,
                0.83,
                0.87,
                0.85,
                10000L,
                500,
                200,
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(trainingResult);
        assertNotNull(metrics);
        assertEquals(0.85, metrics.get("accuracy"));
        assertEquals(0.83, metrics.get("precision"));
        assertEquals(0.87, metrics.get("recall"));
        assertEquals(0.85, metrics.get("f1Score"));
        assertEquals(10000L, metrics.get("trainingTimeMs"));
        assertEquals(500, metrics.get("trainingDataSize"));
        // Quality score = (0.85 + 0.83 + 0.87 + 0.85) / 4 = 0.85
        assertEquals(0.85, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_GOOD, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics with perfect TrainingResult should return EXCELLENT")
    void testCalculatePerformanceMetrics_TrainingResult_Perfect() {
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-123",
                "CLASSIFICATION",
                true,
                1.0,
                1.0,
                1.0,
                1.0,
                5000L,
                500,
                200,
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(trainingResult);
        assertEquals(1.0, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics with poor TrainingResult should return appropriate level")
    void testCalculatePerformanceMetrics_TrainingResult_Poor() {
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.55,
                0.52,
                0.58,
                0.55,
                8000L,
                500,
                200,
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(trainingResult);
        // Quality score = (0.55 + 0.52 + 0.58 + 0.55) / 4 = 0.55
        assertEquals(0.55, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics with EvaluationResult should calculate all metrics correctly")
    void testCalculatePerformanceMetrics_EvaluationResult() {
        ModelEvaluationResult evaluationResult = new ModelEvaluationResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.90,
                0.88,
                0.92,
                0.90,
                0.91,
                3000L,
                200,
                Map.of(),
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(evaluationResult);
        assertNotNull(metrics);
        assertEquals(0.90, metrics.get("accuracy"));
        assertEquals(0.88, metrics.get("precision"));
        assertEquals(0.92, metrics.get("recall"));
        assertEquals(0.90, metrics.get("f1Score"));
        assertEquals(3000L, metrics.get("evaluationTimeMs"));
        assertEquals(200, metrics.get("testDataSize"));
        // Quality score = (0.90 + 0.88 + 0.92 + 0.90) / 4 = 0.9
        assertEquals(0.9, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics with fair EvaluationResult should return FAIR")
    void testCalculatePerformanceMetrics_EvaluationResult_Fair() {
        ModelEvaluationResult evaluationResult = new ModelEvaluationResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.72,
                0.70,
                0.74,
                0.72,
                0.73,
                3000L,
                200,
                Map.of(),
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(evaluationResult);
        // Quality score = (0.72 + 0.70 + 0.74 + 0.72) / 4 = 0.72
        assertEquals(0.72, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_FAIR, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should return EXCELLENT for score >= 0.9")
    void testCalculatePerformanceLevel_Excellent() {
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, calculator.calculatePerformanceLevel(0.90));
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, calculator.calculatePerformanceLevel(0.95));
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, calculator.calculatePerformanceLevel(1.0));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should return GOOD for score >= 0.8")
    void testCalculatePerformanceLevel_Good() {
        assertEquals(VerbaMetricsConstants.K_GOOD, calculator.calculatePerformanceLevel(0.80));
        assertEquals(VerbaMetricsConstants.K_GOOD, calculator.calculatePerformanceLevel(0.85));
        assertEquals(VerbaMetricsConstants.K_GOOD, calculator.calculatePerformanceLevel(0.89));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should return FAIR for score >= 0.7")
    void testCalculatePerformanceLevel_Fair() {
        assertEquals(VerbaMetricsConstants.K_FAIR, calculator.calculatePerformanceLevel(0.70));
        assertEquals(VerbaMetricsConstants.K_FAIR, calculator.calculatePerformanceLevel(0.75));
        assertEquals(VerbaMetricsConstants.K_FAIR, calculator.calculatePerformanceLevel(0.79));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should return POOR for score >= 0.6")
    void testCalculatePerformanceLevel_Poor() {
        assertEquals(VerbaMetricsConstants.K_POOR, calculator.calculatePerformanceLevel(0.60));
        assertEquals(VerbaMetricsConstants.K_POOR, calculator.calculatePerformanceLevel(0.65));
        assertEquals(VerbaMetricsConstants.K_POOR, calculator.calculatePerformanceLevel(0.69));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should return VERY_POOR for score < 0.6")
    void testCalculatePerformanceLevel_VeryPoor() {
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, calculator.calculatePerformanceLevel(0.59));
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, calculator.calculatePerformanceLevel(0.5));
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, calculator.calculatePerformanceLevel(0.0));
    }

    @ParameterizedTest
    @CsvSource({
            "0.95, EXCELLENT",
            "0.90, EXCELLENT",
            "0.89, GOOD",
            "0.85, GOOD",
            "0.80, GOOD",
            "0.79, FAIR",
            "0.75, FAIR",
            "0.70, FAIR",
            "0.69, POOR",
            "0.65, POOR",
            "0.60, POOR",
            "0.59, VERY_POOR",
            "0.50, VERY_POOR",
            "0.30, VERY_POOR"
    })
    @DisplayName("calculatePerformanceLevel should return correct level for various quality scores")
    void testCalculatePerformanceLevel_VariousScores(double score, String expectedLevel) {
        String level = calculator.calculatePerformanceLevel(score);
        assertEquals(expectedLevel, level);
    }

    @Test
    @DisplayName("calculatePerformanceLevel should handle edge case at exact threshold boundaries")
    void testCalculatePerformanceLevel_ExactBoundaries() {
        // Test exact boundary values
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, calculator.calculatePerformanceLevel(0.9));
        assertEquals(VerbaMetricsConstants.K_GOOD, calculator.calculatePerformanceLevel(0.8));
        assertEquals(VerbaMetricsConstants.K_FAIR, calculator.calculatePerformanceLevel(0.7));
        assertEquals(VerbaMetricsConstants.K_POOR, calculator.calculatePerformanceLevel(0.6));
        // Test just below boundaries
        assertEquals(VerbaMetricsConstants.K_GOOD, calculator.calculatePerformanceLevel(0.8999999));
        assertEquals(VerbaMetricsConstants.K_FAIR, calculator.calculatePerformanceLevel(0.7999999));
        assertEquals(VerbaMetricsConstants.K_POOR, calculator.calculatePerformanceLevel(0.6999999));
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, calculator.calculatePerformanceLevel(0.5999999));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should use custom thresholds when provided")
    void testCalculatePerformanceLevel_CustomThresholds() {
        Map<String, Double> customThresholds = new HashMap<>();
        customThresholds.put("excellent", 0.95);
        customThresholds.put("good", 0.85);
        customThresholds.put("fair", 0.75);
        customThresholds.put("poor", 0.65);
        properties.setPerformanceThresholds(customThresholds);
        ModelPerformanceCalculator customCalculator = new ModelPerformanceCalculator(properties);
        // Now 0.90 should be GOOD instead of EXCELLENT
        assertEquals(VerbaMetricsConstants.K_GOOD, customCalculator.calculatePerformanceLevel(0.90));
        // 0.95 should be EXCELLENT
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, customCalculator.calculatePerformanceLevel(0.95));
        // 0.75 should be FAIR
        assertEquals(VerbaMetricsConstants.K_FAIR, customCalculator.calculatePerformanceLevel(0.75));
        // 0.65 should be POOR
        assertEquals(VerbaMetricsConstants.K_POOR, customCalculator.calculatePerformanceLevel(0.65));
        // 0.64 should be VERY_POOR
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, customCalculator.calculatePerformanceLevel(0.64));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should handle missing threshold values with defaults")
    void testCalculatePerformanceLevel_MissingThresholds() {
        Map<String, Double> partialThresholds = new HashMap<>();
        // Only set some thresholds
        partialThresholds.put("excellent", 0.92);
        // Missing: good, fair, poor
        properties.setPerformanceThresholds(partialThresholds);
        ModelPerformanceCalculator partialCalculator = new ModelPerformanceCalculator(properties);
        // Should use default values (0.8, 0.7, 0.6) for missing thresholds
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, partialCalculator.calculatePerformanceLevel(0.92));
        assertEquals(VerbaMetricsConstants.K_GOOD, partialCalculator.calculatePerformanceLevel(0.85));
        assertEquals(VerbaMetricsConstants.K_FAIR, partialCalculator.calculatePerformanceLevel(0.75));
        assertEquals(VerbaMetricsConstants.K_POOR, partialCalculator.calculatePerformanceLevel(0.65));
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, partialCalculator.calculatePerformanceLevel(0.55));
    }

    @Test
    @DisplayName("calculatePerformanceLevel should handle empty threshold map")
    void testCalculatePerformanceLevel_EmptyThresholds() {
        properties.setPerformanceThresholds(Map.of());
        ModelPerformanceCalculator emptyCalculator = new ModelPerformanceCalculator(properties);
        // Should use default values: excellent=0.9, good=0.8, fair=0.7, poor=0.6
        assertEquals(VerbaMetricsConstants.K_EXCELLENT, emptyCalculator.calculatePerformanceLevel(0.95));
        assertEquals(VerbaMetricsConstants.K_GOOD, emptyCalculator.calculatePerformanceLevel(0.85));
        assertEquals(VerbaMetricsConstants.K_FAIR, emptyCalculator.calculatePerformanceLevel(0.75));
        assertEquals(VerbaMetricsConstants.K_POOR, emptyCalculator.calculatePerformanceLevel(0.65));
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, emptyCalculator.calculatePerformanceLevel(0.55));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics should handle TrainingResult with zero metrics")
    void testCalculatePerformanceMetrics_TrainingResult_ZeroMetrics() {
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-123",
                "CLASSIFICATION",
                false,
                0.0,
                0.0,
                0.0,
                0.0,
                1000L,
                500,
                200,
                Map.of(),
                "Error occurred",
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(trainingResult);
        assertNotNull(metrics);
        assertEquals(0.0, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics should handle EvaluationResult with zero metrics")
    void testCalculatePerformanceMetrics_EvaluationResult_ZeroMetrics() {
        ModelEvaluationResult evaluationResult = new ModelEvaluationResult(
                "model-123",
                "CLASSIFICATION",
                false,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                500L,
                100,
                Map.of(),
                Map.of(),
                "Error occurred",
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(evaluationResult);
        assertNotNull(metrics);
        assertEquals(0.0, (Double) metrics.get("qualityScore"), 0.0001);
        assertEquals(VerbaMetricsConstants.K_VERY_POOR, metrics.get("performanceLevel"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics should include all required keys in result map")
    void testCalculatePerformanceMetrics_RequiredKeys() {
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.85,
                0.83,
                0.87,
                0.85,
                10000L,
                500,
                200,
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(trainingResult);
        assertTrue(metrics.containsKey("accuracy"));
        assertTrue(metrics.containsKey("precision"));
        assertTrue(metrics.containsKey("recall"));
        assertTrue(metrics.containsKey("f1Score"));
        assertTrue(metrics.containsKey("qualityScore"));
        assertTrue(metrics.containsKey("performanceLevel"));
        assertTrue(metrics.containsKey("trainingTimeMs"));
        assertTrue(metrics.containsKey("trainingDataSize"));
    }

    @Test
    @DisplayName("calculatePerformanceMetrics for EvaluationResult should include all required keys")
    void testCalculatePerformanceMetrics_EvaluationResult_RequiredKeys() {
        ModelEvaluationResult evaluationResult = new ModelEvaluationResult(
                "model-123",
                "CLASSIFICATION",
                true,
                0.90,
                0.88,
                0.92,
                0.90,
                0.91,
                3000L,
                200,
                Map.of(),
                Map.of(),
                null,
                LocalDateTime.now()
        );
        Map<String, Object> metrics = calculator.calculatePerformanceMetrics(evaluationResult);
        assertTrue(metrics.containsKey("accuracy"));
        assertTrue(metrics.containsKey("precision"));
        assertTrue(metrics.containsKey("recall"));
        assertTrue(metrics.containsKey("f1Score"));
        assertTrue(metrics.containsKey("qualityScore"));
        assertTrue(metrics.containsKey("performanceLevel"));
        assertTrue(metrics.containsKey("evaluationTimeMs"));
        assertTrue(metrics.containsKey("testDataSize"));
    }

}
