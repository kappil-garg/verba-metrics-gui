package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.util.TypeSafeCastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Engine for model evaluation operations.
 * Handles the core logic for evaluating machine learning models.
 *
 * @author Kapil Garg
 */
@Component
public class ModelEvaluationEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelEvaluationEngine.class);

    private final ModelFileManager fileManager;
    private final ClassValueManager classValueManager;

    @Autowired
    public ModelEvaluationEngine(ModelFileManager fileManager, ClassValueManager classValueManager) {
        this.fileManager = fileManager;
        this.classValueManager = classValueManager;
    }

    /**
     * Evaluates a trained model using test data.
     *
     * @param modelId  the ID of the trained model
     * @param testData the test dataset
     * @return evaluation result with performance metrics
     */
    public ModelEvaluationResult evaluateModel(String modelId, List<Map<String, Object>> testData) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(testData, "Test data cannot be null");
        try {
            return doEvaluateModel(modelId, testData);
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate model: {}", modelId, e);
            return buildFailedEvaluationResult(modelId, testData, e);
        }
    }

    /**
     * Performs the actual model evaluation.
     *
     * @param modelId  the ID of the trained model
     * @param testData the test dataset
     * @return evaluation result with performance metrics
     * @throws Exception if evaluation fails
     */
    private ModelEvaluationResult doEvaluateModel(String modelId, List<Map<String, Object>> testData) throws Exception {
        long startTime = System.currentTimeMillis();
        if (testData.isEmpty()) {
            throw new IllegalArgumentException("Test data cannot be empty");
        }
        Object model = fileManager.loadModelFromFile(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        if (!(model instanceof Classifier)) {
            throw new IllegalArgumentException("Loaded model is not a Weka Classifier: " + model.getClass().getSimpleName());
        }
        Map<String, Object> evaluationMetrics = performModelEvaluation(model, testData, modelId);
        long evaluationTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Model evaluation completed in {}ms for model: {}", evaluationTime, modelId);
        return new ModelEvaluationResult(
                modelId,
                "EVALUATION",
                true,
                (Double) evaluationMetrics.get("accuracy"),
                (Double) evaluationMetrics.get("precision"),
                (Double) evaluationMetrics.get("recall"),
                (Double) evaluationMetrics.get("f1Score"),
                (Double) evaluationMetrics.get("auc"),
                evaluationTime,
                testData.size(),
                TypeSafeCastUtil.safeCastToMap(evaluationMetrics.get("confusionMatrix")),
                TypeSafeCastUtil.safeCastToMap(evaluationMetrics.get("additionalMetrics")),
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Performs the actual model evaluation using the loaded model.
     *
     * @param model    The trained model
     * @param testData The test dataset
     * @return Evaluation metrics
     * @throws Exception if evaluation fails
     */
    private Map<String, Object> performModelEvaluation(Object model, List<Map<String, Object>> testData, String modelId) throws Exception {
        if (model instanceof Classifier) {
            return evaluateWekaModel((Classifier) model, testData, modelId);
        } else {
            throw new IllegalArgumentException("Unsupported model type: " + model.getClass().getSimpleName());
        }
    }

    /**
     * Evaluates a Weka-based model.
     *
     * @param model    The Weka classifier
     * @param testData The test dataset
     * @return Evaluation metrics
     * @throws Exception if evaluation fails
     */
    private Map<String, Object> evaluateWekaModel(Classifier model, List<Map<String, Object>> testData, String modelId) throws Exception {
        Instances testDataset = createAlignedEvaluationDataset(testData, modelId);
        weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(testDataset);
        evaluation.evaluateModel(model, testDataset);
        double accuracy = evaluation.pctCorrect() / 100.0;
        double precision = evaluation.weightedPrecision();
        double recall = evaluation.weightedRecall();
        double f1Score = evaluation.weightedFMeasure();
        double auc = evaluation.weightedAreaUnderROC();
        double[][] confusionMatrix = evaluation.confusionMatrix();
        Map<String, Object> confusionMap = extractConfusionMatrix(confusionMatrix);
        return Map.of(
                "accuracy", accuracy,
                "precision", precision,
                "recall", recall,
                "f1Score", f1Score,
                "auc", auc,
                "confusionMatrix", confusionMap,
                "additionalMetrics", Map.of(
                        "correctPredictions", (int) evaluation.correct(),
                        "totalPredictions", testData.size(),
                        "incorrectPredictions", (int) evaluation.incorrect()
                )
        );
    }

    /**
     * Safely extracts confusion matrix values, handling cases with single class or variable dimensions.
     *
     * @param confusionMatrix The Weka confusion matrix
     * @return Map containing TP, TN, FP, FN values
     */
    private Map<String, Object> extractConfusionMatrix(double[][] confusionMatrix) {
        int rows = confusionMatrix.length;
        int cols = rows > 0 ? confusionMatrix[0].length : 0;
        if (rows == 0 || cols == 0) {
            return Map.of("TP", 0, "TN", 0, "FP", 0, "FN", 0);
        }
        if (rows == 1 && cols == 1) {
            int tp = (int) confusionMatrix[0][0];
            return Map.of("TP", tp, "TN", 0, "FP", 0, "FN", 0);
        }
        int tp = (int) confusionMatrix[0][0];
        int tn = rows > 1 && cols > 1 ? (int) confusionMatrix[1][1] : 0;
        int fp = rows > 1 ? (int) confusionMatrix[1][0] : 0;
        int fn = cols > 1 ? (int) confusionMatrix[0][1] : 0;
        return Map.of("TP", tp, "TN", tn, "FP", fp, "FN", fn);
    }

    /**
     * Creates an aligned evaluation dataset compatible with the trained model.
     *
     * @param testData The test dataset
     * @param modelId  The ID of the trained model
     * @return Aligned Weka Instances dataset
     */
    private Instances createAlignedEvaluationDataset(List<Map<String, Object>> testData, String modelId) {
        ArrayList<weka.core.Attribute> attributes = new ArrayList<>();
        int featureCount = 0;
        Map<String, Object> sample = testData.getFirst();
        Object feats = sample.get("features");
        if (feats instanceof double[] arr) {
            featureCount = arr.length;
            for (int i = 0; i < featureCount; i++) attributes.add(new weka.core.Attribute("feature_" + i));
        } else if (feats instanceof List<?> list) {
            featureCount = list.size();
            for (int i = 0; i < featureCount; i++) attributes.add(new weka.core.Attribute("feature_" + i));
        }
        List<String> classValues = classValueManager.getClassValues(modelId);
        if (classValues.isEmpty()) {
            // Fallback to common sentiment ordering if none stored
            classValues = List.of("negative", "neutral", "positive");
        }
        attributes.add(new weka.core.Attribute("label", new ArrayList<>(classValues)));
        weka.core.Instances dataset = new weka.core.Instances("EvaluationDataset", attributes, testData.size());
        dataset.setClassIndex(attributes.size() - 1);
        for (Map<String, Object> dp : testData) {
            weka.core.DenseInstance instance = new weka.core.DenseInstance(attributes.size());
            instance.setDataset(dataset);
            Object f = dp.get("features");
            if (f instanceof double[] arr2) {
                for (int i = 0; i < featureCount; i++) instance.setValue(i, i < arr2.length ? arr2[i] : 0.0);
            } else if (f instanceof List<?> l2) {
                for (int i = 0; i < featureCount; i++) {
                    Object v = i < l2.size() ? l2.get(i) : 0.0;
                    instance.setValue(i, v instanceof Number ? ((Number) v).doubleValue() : 0.0);
                }
            }
            Object lbl = dp.get("label");
            if (lbl != null) {
                String s = lbl.toString();
                if (dataset.classAttribute().indexOfValue(s) >= 0) {
                    instance.setValue(featureCount, s);
                } else {
                    instance.setMissing(featureCount);
                }
            } else {
                instance.setMissing(featureCount);
            }
            dataset.add(instance);
        }
        return dataset;
    }

    /**
     * Builds a failed evaluation result in case of exceptions.
     *
     * @param modelId  the ID of the trained model
     * @param testData the test dataset
     * @param e        the exception encountered
     * @return evaluation result indicating failure
     */
    private ModelEvaluationResult buildFailedEvaluationResult(String modelId, List<Map<String, Object>> testData, Exception e) {
        return new ModelEvaluationResult(
                modelId,
                "EVALUATION",
                false,
                0.0, 0.0, 0.0, 0.0, 0.0,
                System.currentTimeMillis(),
                testData.size(),
                Map.of(),
                Map.of(),
                e.getMessage(),
                LocalDateTime.now()
        );
    }

}
