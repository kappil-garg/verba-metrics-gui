package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.util.TypeSafeCastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
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

    @Autowired
    public ModelEvaluationEngine(ModelFileManager fileManager) {
        this.fileManager = fileManager;
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
        LOGGER.debug("Starting model evaluation for model: {} with {} test points", modelId, testData.size());
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
        Object model = fileManager.loadModelFromFile(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        Map<String, Object> evaluationMetrics = performModelEvaluation(model, testData);
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
    private Map<String, Object> performModelEvaluation(Object model, List<Map<String, Object>> testData) throws Exception {
        if (model instanceof Classifier) {
            return evaluateWekaModel((Classifier) model, testData);
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
    private Map<String, Object> evaluateWekaModel(Classifier model, List<Map<String, Object>> testData) throws Exception {
        Instances testDataset = createWekaDataset(testData);
        weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(testDataset);
        evaluation.evaluateModel(model, testDataset);
        double accuracy = evaluation.pctCorrect() / 100.0;
        double precision = evaluation.precision(0);
        double recall = evaluation.recall(0);
        double f1Score = 2.0 * (precision * recall) / (precision + recall);
        double auc = evaluation.areaUnderROC(0);
        double[][] confusionMatrix = evaluation.confusionMatrix();
        Map<String, Object> confusionMap = Map.of(
                "TP", (int) confusionMatrix[0][0],
                "TN", (int) confusionMatrix[1][1],
                "FP", (int) confusionMatrix[1][0],
                "FN", (int) confusionMatrix[0][1]
        );
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
     * Creates a Weka dataset from test data.
     *
     * @param testData The test dataset
     * @return Weka Instances object
     */
    private Instances createWekaDataset(List<Map<String, Object>> testData) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("text", (ArrayList<String>) null));
        attributes.add(new Attribute("label"));
        Instances dataset = new Instances("TestDataset", attributes, testData.size());
        dataset.setClassIndex(1);
        for (Map<String, Object> data : testData) {
            DenseInstance instance = new DenseInstance(2);
            instance.setValue(0, (String) data.get("text"));
            instance.setValue(1, (Integer) data.getOrDefault("label", 0));
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
