package com.kapil.verbametrics.ml.services;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;

import java.util.List;
import java.util.Map;

/**
 * Service interface for machine learning model operations.
 * Handles model training, evaluation, and prediction for text analysis.
 *
 * @author Kapil Garg
 */
public interface MLModelService {
    
    /**
     * Trains a machine learning model with the provided dataset.
     *
     * @param modelType    The type of model to train (SENTIMENT, TOPIC_MODELING, CLASSIFICATION)
     * @param trainingData The training dataset
     * @param parameters   Model-specific parameters
     * @return Training result with model performance metrics
     */
    ModelTrainingResult trainModel(String modelType, List<Map<String, Object>> trainingData, 
                                   Map<String, Object> parameters);
    
    /**
     * Evaluates a trained model using test data.
     *
     * @param modelId  The ID of the trained model
     * @param testData The test dataset
     * @return Evaluation result with performance metrics
     */
    ModelEvaluationResult evaluateModel(String modelId, List<Map<String, Object>> testData);
    
    /**
     * Makes predictions using a trained model.
     *
     * @param modelId The ID of the trained model
     * @param input   The input data for prediction
     * @return Prediction result with confidence scores
     */
    Map<String, Object> predict(String modelId, Map<String, Object> input);
    
    /**
     * Gets information about a trained model.
     *
     * @param modelId The ID of the model
     * @return Model information
     */
    MLModel getModel(String modelId);
    
    /**
     * Lists all available trained models.
     *
     * @return List of model information
     */
    List<MLModel> listModels();
    
    /**
     * Lists models by type.
     *
     * @param modelType The type of models to list
     * @return List of models of the specified type
     */
    List<MLModel> listModelsByType(String modelType);
    
    /**
     * Lists active models only.
     *
     * @return List of active models
     */
    List<MLModel> listActiveModels();
    
    /**
     * Deletes a trained model.
     *
     * @param modelId The ID of the model to delete
     * @return true if deletion was successful
     */
    boolean deleteModel(String modelId);
    
    /**
     * Updates model metadata.
     *
     * @param modelId The ID of the model to update
     * @param updates Map of fields to update
     * @return Updated model information
     */
    MLModel updateModel(String modelId, Map<String, Object> updates);
    
    /**
     * Gets model performance statistics.
     *
     * @param modelId The ID of the model
     * @return Performance statistics
     */
    Map<String, Object> getModelStatistics(String modelId);
    
}
