package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.engines.ModelTrainingEngine;
import com.kapil.verbametrics.ml.services.ModelTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of model training service.
 * Handles the actual training logic for different model types.
 *
 * @author Kapil Garg
 */
@Service
public class ModelTrainingServiceImpl implements ModelTrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTrainingServiceImpl.class.getName());

    private final ModelTrainingEngine trainingEngine;

    @Autowired
    public ModelTrainingServiceImpl(ModelTrainingEngine trainingEngine) {
        this.trainingEngine = trainingEngine;
    }

    @Override
    public ModelTrainingResult trainModel(String modelType, List<Map<String, Object>> trainingData,
                                          Map<String, Object> parameters) {
        Objects.requireNonNull(modelType, "Model type cannot be null");
        Objects.requireNonNull(trainingData, "Training data cannot be null");
        Objects.requireNonNull(parameters, "Parameters cannot be null");
        LOGGER.debug("Starting model training for type: {} with {} data points", modelType, trainingData.size());
        try {
            Optional<String> validationError = trainingEngine.validateTrainingDataError(trainingData, modelType);
            if (validationError.isPresent()) {
                throw new IllegalArgumentException(validationError.get());
            }
            String modelId = "model_" + System.currentTimeMillis();
            return trainingEngine.trainModel(modelId, modelType, trainingData, parameters);
        } catch (Exception e) {
            LOGGER.error("Failed to train model", e);
            throw new RuntimeException("Model training failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateTrainingData(List<Map<String, Object>> trainingData, String modelType) {
        // Call validateTrainingDataError directly to avoid unnecessary wrapper method call
        return trainingEngine.validateTrainingDataError(trainingData, modelType).isEmpty();
    }

    @Override
    public List<String> getSupportedModelTypes() {
        return trainingEngine.getSupportedModelTypes();
    }

    @Override
    public Map<String, Object> getDefaultParameters(String modelType) {
        return trainingEngine.getDefaultParameters(modelType);
    }

}
