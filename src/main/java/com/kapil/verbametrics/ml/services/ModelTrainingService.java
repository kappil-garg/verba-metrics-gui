package com.kapil.verbametrics.ml.services;

import com.kapil.verbametrics.ml.domain.ModelTrainingResult;

import java.util.List;
import java.util.Map;

/**
 * Service interface for model training operations.
 * Handles the training logic separately from model management.
 *
 * @author Kapil Garg
 */
public interface ModelTrainingService {
    
    /**
     * Trains a machine learning model with the provided dataset.
     *
     * @param modelType    The type of model to train
     * @param trainingData The training dataset
     * @param parameters   Model-specific parameters
     * @return Training result with model performance metrics
     */
    ModelTrainingResult trainModel(String modelType, List<Map<String, Object>> trainingData, 
                                   Map<String, Object> parameters);
    
    /**
     * Validates training data before training.
     *
     * @param trainingData The training dataset
     * @param modelType    The model type
     * @return true if data is valid for training
     */
    boolean validateTrainingData(List<Map<String, Object>> trainingData, String modelType);
    
    /**
     * Gets supported model types.
     *
     * @return List of supported model types
     */
    List<String> getSupportedModelTypes();
    
    /**
     * Gets default parameters for a model type.
     *
     * @param modelType The model type
     * @return Default parameters for the model type
     */
    Map<String, Object> getDefaultParameters(String modelType);
    
}
