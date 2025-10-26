package com.kapil.verbametrics.ui.controller;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.util.JsonParserUtil;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class to handle ML model operations.
 * Provides a simplified interface for ML model management operations.
 *
 * @param mlModelService the ML model service
 * @author Kapil Garg
 */
public record MLModelController(MLModelService mlModelService) {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLModelController.class);

    /**
     * Create parameters map for model training.
     *
     * @param modelName   the model name
     * @param description the model description
     * @return parameters map
     */
    private static Map<String, Object> createModelParameters(String modelName, String description) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", modelName);
        parameters.put("description", description);
        parameters.put(VerbaMetricsConstants.PARAM_MAX_DEPTH, 10);
        parameters.put(VerbaMetricsConstants.PARAM_MIN_SAMPLES_SPLIT, 2);
        parameters.put(VerbaMetricsConstants.PARAM_MIN_SAMPLES_LEAF, 1);
        parameters.put(VerbaMetricsConstants.PARAM_RANDOM_STATE, 42);
        return parameters;
    }

    /**
     * Get all available models.
     *
     * @return list of all models
     */
    public List<MLModel> getAllModels() {
        return mlModelService.listModels();
    }

    /**
     * Get a specific model by ID.
     *
     * @param modelId the model ID
     * @return the model
     */
    public MLModel getModel(String modelId) {
        return mlModelService.getModel(modelId);
    }

    /**
     * Delete a model.
     *
     * @param modelId the model ID
     * @return true if deletion was successful
     */
    public boolean deleteModel(String modelId) {
        return mlModelService.deleteModel(modelId);
    }

    /**
     * Train a new model.
     *
     * @param modelType        the type of model to train
     * @param modelName        the name for the model
     * @param description      the model description
     * @param trainingDataJson the training data as JSON string
     * @return training result
     */
    public ModelTrainingResult trainModel(String modelType, String modelName, String description, String trainingDataJson) {
        try {
            List<Map<String, Object>> trainingData = JsonParserUtil.parseTrainingData(trainingDataJson);
            Map<String, Object> parameters = createModelParameters(modelName, description);
            return mlModelService.trainModel(modelType, trainingData, parameters);
        } catch (Exception e) {
            LOGGER.error("Failed to train model", e);
            throw new RuntimeException("Model training failed: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate a model.
     *
     * @param modelId  the model ID
     * @param testData the test data
     * @return evaluation result
     */
    public ModelEvaluationResult evaluateModel(String modelId, List<Map<String, Object>> testData) {
        return mlModelService.evaluateModel(modelId, testData);
    }

    /**
     * Make a prediction using a model.
     *
     * @param modelId the model ID
     * @param input   the input data
     * @return prediction result
     */
    public Map<String, Object> predict(String modelId, Map<String, Object> input) {
        return mlModelService.predict(modelId, input);
    }

}
