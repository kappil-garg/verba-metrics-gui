package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.engines.ModelEvaluationEngine;
import com.kapil.verbametrics.ml.engines.ModelPredictionEngine;
import com.kapil.verbametrics.ml.entities.MLModelEntity;
import com.kapil.verbametrics.ml.mapper.MLModelMapper;
import com.kapil.verbametrics.ml.repository.MLModelRepository;
import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.ml.services.ModelTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service implementation for machine learning model operations using Spring Boot.
 * Handles model training, evaluation, and prediction for text analysis.
 *
 * @author Kapil Garg
 */
@Service
@Transactional
public class MLModelServiceImpl implements MLModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLModelServiceImpl.class.getName());

    private final MLModelMapper modelMapper;
    private final MLModelRepository modelRepository;
    private final ModelTrainingService trainingService;
    private final ModelEvaluationEngine evaluationEngine;
    private final ModelPredictionEngine predictionEngine;

    @Autowired
    public MLModelServiceImpl(MLModelRepository modelRepository,
                              MLModelMapper modelMapper,
                              ModelTrainingService trainingService,
                              ModelEvaluationEngine evaluationEngine,
                              ModelPredictionEngine predictionEngine) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
        this.trainingService = trainingService;
        this.evaluationEngine = evaluationEngine;
        this.predictionEngine = predictionEngine;
    }

    @Override
    public ModelTrainingResult trainModel(String modelType, List<Map<String, Object>> trainingData,
                                          Map<String, Object> parameters) {
        Objects.requireNonNull(modelType, "Model type cannot be null");
        Objects.requireNonNull(trainingData, "Training data cannot be null");
        Objects.requireNonNull(parameters, "Parameters cannot be null");
        LOGGER.debug("Starting model training for type: {} with {} data points", modelType, trainingData.size());
        try {
            ModelTrainingResult result = trainingService.trainModel(modelType, trainingData, parameters);
            MLModel model = createMLModelFromResult(result, modelType, parameters);
            MLModelEntity entity = modelMapper.toEntity(model);
            modelRepository.save(entity);
            LOGGER.debug("Model training completed and saved: {}", result.modelId());
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to train model", e);
            throw new RuntimeException("Model training failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ModelEvaluationResult evaluateModel(String modelId, List<Map<String, Object>> testData) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(testData, "Test data cannot be null");
        LOGGER.debug("Starting model evaluation for model: {} with {} test points", modelId, testData.size());
        try {
            if (!modelRepository.existsById(modelId)) {
                throw new IllegalArgumentException("Model not found: " + modelId);
            }
            if (testData.isEmpty()) {
                throw new IllegalArgumentException("Test data cannot be empty");
            }
            return evaluationEngine.evaluateModel(modelId, testData);
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate model", e);
            throw new RuntimeException("Model evaluation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> predict(String modelId, Map<String, Object> input) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");
        LOGGER.debug("Making prediction with model: {}", modelId);
        try {
            MLModel model = getModel(modelId);
            if (!model.isReadyForUse()) {
                throw new IllegalStateException("Model is not ready for use: " + modelId);
            }
            return predictionEngine.predict(modelId, input);
        } catch (Exception e) {
            LOGGER.error("Failed to make prediction", e);
            throw new RuntimeException("Prediction failed: " + e.getMessage(), e);
        }
    }

    @Override
    public MLModel getModel(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        MLModelEntity entity = modelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        return modelMapper.toDomain(entity);
    }

    @Override
    public List<MLModel> listModels() {
        LOGGER.debug("Listing all models");
        return modelRepository.findAll().stream()
                .map(modelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MLModel> listModelsByType(String modelType) {
        Objects.requireNonNull(modelType, "Model type cannot be null");
        return modelRepository.findByModelType(modelType).stream()
                .map(modelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MLModel> listActiveModels() {
        return modelRepository.findByIsActiveTrue().stream()
                .map(modelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteModel(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        LOGGER.debug("Deleting model: {}", modelId);
        if (modelRepository.existsById(modelId)) {
            modelRepository.deleteById(modelId);
            LOGGER.debug("Model deleted successfully: {}", modelId);
            return true;
        } else {
            LOGGER.warn("Model not found for deletion: {}", modelId);
            return false;
        }
    }

    @Override
    public MLModel updateModel(String modelId, Map<String, Object> updates) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(updates, "Updates cannot be null");
        LOGGER.debug("Updating model: {}", modelId);
        MLModelEntity entity = modelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        if (updates.containsKey("name")) {
            entity.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            entity.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("isActive")) {
            entity.setIsActive((Boolean) updates.get("isActive"));
        }
        if (updates.containsKey("status")) {
            entity.setStatus((String) updates.get("status"));
        }
        MLModelEntity savedEntity = modelRepository.save(entity);
        LOGGER.debug("Model updated successfully: {}", modelId);
        return modelMapper.toDomain(savedEntity);
    }

    @Override
    public Map<String, Object> getModelStatistics(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        MLModel model = getModel(modelId);
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("modelId", model.modelId());
        statistics.put("modelType", model.modelType());
        statistics.put("accuracy", model.accuracy());
        statistics.put("isActive", model.isActive());
        statistics.put("status", model.status());
        statistics.put("performanceLevel", model.getPerformanceLevel());
        statistics.put("trainingDataSize", model.trainingDataSize());
        statistics.put("createdAt", model.createdAt());
        statistics.put("lastUsed", model.lastUsed());
        return statistics;
    }


    /**
     * Creates an ML model from training result.
     *
     * @param result     The training result containing performance metrics
     * @param modelType  The type of the model
     * @param parameters The parameters used for training
     * @return The created MLModel instance
     */
    private MLModel createMLModelFromResult(ModelTrainingResult result, String modelType, Map<String, Object> parameters) {
        return new MLModel(
                result.modelId(),
                modelType,
                (String) parameters.getOrDefault("name", "Model_" + result.modelId()),
                (String) parameters.getOrDefault("description", "Trained " + modelType + " model"),
                "1.0",
                LocalDateTime.now(),
                LocalDateTime.now(),
                parameters,
                Map.of("accuracy", result.accuracy(), "f1Score", result.f1Score()),
                "/models/" + result.modelId(),
                true,
                "system",
                result.trainingDataSize(),
                result.accuracy(),
                "TRAINED"
        );
    }

}
