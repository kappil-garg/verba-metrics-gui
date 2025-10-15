package com.kapil.verbametrics.ml.calculators;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.utils.MetricsCalculationUtils;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for evaluating machine learning model performance metrics.
 * Handles metrics like accuracy, precision, recall, F1 score, and overall quality score.
 *
 * @author Kapil Garg
 */
@Component
public class ModelPerformanceCalculator {

    private final MLModelProperties properties;

    @Autowired
    public ModelPerformanceCalculator(MLModelProperties properties) {
        this.properties = properties;
    }

    /**
     * Calculates performance metrics from training result.
     *
     * @param trainingResult the training result
     * @return performance metrics map
     */
    public Map<String, Object> calculatePerformanceMetrics(ModelTrainingResult trainingResult) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", trainingResult.accuracy());
        metrics.put("precision", trainingResult.precision());
        metrics.put("recall", trainingResult.recall());
        metrics.put("f1Score", trainingResult.f1Score());
        metrics.put("qualityScore", MetricsCalculationUtils.calculateQualityScore(
                trainingResult.accuracy(), trainingResult.precision(),
                trainingResult.recall(), trainingResult.f1Score()));
        metrics.put("performanceLevel", calculatePerformanceLevel(
                MetricsCalculationUtils.calculateQualityScore(trainingResult.accuracy(), trainingResult.precision(),
                        trainingResult.recall(), trainingResult.f1Score())));
        metrics.put("trainingTimeMs", trainingResult.trainingTimeMs());
        metrics.put("trainingDataSize", trainingResult.trainingDataSize());
        return metrics;
    }

    /**
     * Calculates performance metrics from evaluation result.
     *
     * @param evaluationResult the evaluation result
     * @return performance metrics map
     */
    public Map<String, Object> calculatePerformanceMetrics(ModelEvaluationResult evaluationResult) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", evaluationResult.accuracy());
        metrics.put("precision", evaluationResult.precision());
        metrics.put("recall", evaluationResult.recall());
        metrics.put("f1Score", evaluationResult.f1Score());
        metrics.put("qualityScore", MetricsCalculationUtils.calculateQualityScore(
                evaluationResult.accuracy(), evaluationResult.precision(),
                evaluationResult.recall(), evaluationResult.f1Score()));
        metrics.put("performanceLevel", calculatePerformanceLevel(
                MetricsCalculationUtils.calculateQualityScore(evaluationResult.accuracy(), evaluationResult.precision(),
                        evaluationResult.recall(), evaluationResult.f1Score())));
        metrics.put("evaluationTimeMs", evaluationResult.evaluationTimeMs());
        metrics.put("testDataSize", evaluationResult.testDataSize());
        return metrics;
    }


    /**
     * Calculates model performance level based on metrics.
     *
     * @param qualityScore the quality score
     * @return performance level string
     */
    public String calculatePerformanceLevel(double qualityScore) {
        Map<String, Double> thresholds = properties.getPerformanceThresholds();
        double excellent = thresholds.getOrDefault("excellent", 0.9);
        double good = thresholds.getOrDefault("good", 0.8);
        double fair = thresholds.getOrDefault("fair", 0.7);
        double poor = thresholds.getOrDefault("poor", 0.6);
        if (qualityScore >= excellent) {
            return VerbaMetricsConstants.K_EXCELLENT;
        }
        if (qualityScore >= good) {
            return VerbaMetricsConstants.K_GOOD;
        }
        if (qualityScore >= fair) {
            return VerbaMetricsConstants.K_FAIR;
        }
        if (qualityScore >= poor) {
            return VerbaMetricsConstants.K_POOR;
        }
        return VerbaMetricsConstants.K_VERY_POOR;
    }

}
