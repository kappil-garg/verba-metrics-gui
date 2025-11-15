package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.engines.ModelEvaluationEngine;
import com.kapil.verbametrics.ml.engines.ModelPredictionEngine;
import com.kapil.verbametrics.ml.entities.MLModelEntity;
import com.kapil.verbametrics.ml.mapper.MLModelMapper;
import com.kapil.verbametrics.ml.repository.MLModelRepository;
import com.kapil.verbametrics.ml.services.ModelTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for MLModelServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class MLModelServiceImplTest {

    @Mock
    private MLModelRepository modelRepository;

    @Mock
    private MLModelMapper modelMapper;

    @Mock
    private ModelTrainingService trainingService;

    @Mock
    private ModelEvaluationEngine evaluationEngine;

    @Mock
    private ModelPredictionEngine predictionEngine;

    private MLModelServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MLModelServiceImpl(modelRepository, modelMapper, trainingService,
                evaluationEngine, predictionEngine);
    }

    @Test
    @DisplayName("trainModel successfully trains and saves model")
    void trainModel_success() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of(
                Map.of("text", "Great!", "label", "POSITIVE", "features", new double[]{1.0, 2.0}),
                Map.of("text", "Bad!", "label", "NEGATIVE", "features", new double[]{3.0, 4.0})
        );
        Map<String, Object> parameters = Map.of("name", "TestModel");
        ModelTrainingResult trainingResult = new ModelTrainingResult(
                "model-1", modelType, true, 0.85, 0.80, 0.82, 0.81,
                100L, 2, 0, Map.of(), null, LocalDateTime.now()
        );
        MLModelEntity entity = MLModelEntity.builder()
                .modelId("model-1")
                .modelType(modelType)
                .name("TestModel")
                .build();
        when(trainingService.trainModel(modelType, trainingData, parameters)).thenReturn(trainingResult);
        when(modelMapper.toEntity(any(MLModel.class))).thenReturn(entity);
        when(modelRepository.save(any(MLModelEntity.class))).thenReturn(entity);
        ModelTrainingResult result = service.trainModel(modelType, trainingData, parameters);
        assertNotNull(result);
        assertEquals("model-1", result.modelId());
        verify(trainingService).trainModel(modelType, trainingData, parameters);
        verify(modelRepository).save(any(MLModelEntity.class));
    }

    @Test
    @DisplayName("trainModel throws exception when modelType is null")
    void trainModel_nullModelType_throwsException() {
        assertThrows(NullPointerException.class,
                () -> service.trainModel(null, List.of(), Map.of()));
    }

    @Test
    @DisplayName("trainModel throws exception when trainingData is null")
    void trainModel_nullTrainingData_throwsException() {
        assertThrows(NullPointerException.class,
                () -> service.trainModel("SENTIMENT", null, Map.of()));
    }

    @Test
    @DisplayName("trainModel throws exception when parameters is null")
    void trainModel_nullParameters_throwsException() {
        assertThrows(NullPointerException.class,
                () -> service.trainModel("SENTIMENT", List.of(), null));
    }

    @Test
    @DisplayName("trainModel handles training service exception")
    void trainModel_trainingServiceThrowsException_wrapsException() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of();
        Map<String, Object> parameters = Map.of();
        when(trainingService.trainModel(modelType, trainingData, parameters))
                .thenThrow(new RuntimeException("Training failed"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.trainModel(modelType, trainingData, parameters));
        assertTrue(exception.getMessage().contains("Model training failed"));
    }

    @Test
    @DisplayName("evaluateModel successfully evaluates model")
    void evaluateModel_success() {
        String modelId = "model-1";
        List<Map<String, Object>> testData = List.of(
                Map.of("text", "Test", "label", "POSITIVE", "features", new double[]{1.0, 2.0})
        );
        ModelEvaluationResult evaluationResult = new ModelEvaluationResult(
                modelId, "CROSS_VALIDATION", true, 0.85, 0.80, 0.82, 0.81, 0.83,
                50L, 1, Map.of(), Map.of(), null, LocalDateTime.now()
        );
        when(modelRepository.existsById(modelId)).thenReturn(true);
        when(evaluationEngine.evaluateModel(modelId, testData)).thenReturn(evaluationResult);
        ModelEvaluationResult result = service.evaluateModel(modelId, testData);
        assertNotNull(result);
        assertEquals(modelId, result.modelId());
        verify(evaluationEngine).evaluateModel(modelId, testData);
    }

    @Test
    @DisplayName("predict successfully makes prediction")
    void predict_success() {
        String modelId = "model-1";
        Map<String, Object> input = Map.of("features", new double[]{1.0, 2.0});
        Map<String, Object> prediction = Map.of("prediction", "POSITIVE", "confidence", 0.95);
        MLModel model = new MLModel(
                modelId, "SENTIMENT", "Test", "Desc", "1.0",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(),
                "/models/model-1", true, "system", 1, 0.85, "TRAINED"
        );
        MLModelEntity entity = MLModelEntity.builder()
                .modelId(modelId)
                .isActive(true)
                .status("TRAINED")
                .build();
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(entity));
        when(modelMapper.toDomain(entity)).thenReturn(model);
        when(predictionEngine.predict(modelId, input)).thenReturn(prediction);
        Map<String, Object> result = service.predict(modelId, input);
        assertNotNull(result);
        assertEquals("POSITIVE", result.get("prediction"));
        verify(predictionEngine).predict(modelId, input);
    }

    @Test
    @DisplayName("getModel successfully retrieves model")
    void getModel_success() {
        String modelId = "model-1";
        MLModel model = new MLModel(
                modelId, "SENTIMENT", "Test", "Desc", "1.0",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(),
                "/models/model-1", true, "system", 1, 0.85, "TRAINED"
        );
        MLModelEntity entity = MLModelEntity.builder()
                .modelId(modelId)
                .build();
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(entity));
        when(modelMapper.toDomain(entity)).thenReturn(model);
        MLModel result = service.getModel(modelId);
        assertNotNull(result);
        assertEquals(modelId, result.modelId());
    }

    @Test
    @DisplayName("getModel throws exception when model not found")
    void getModel_modelNotFound_throwsException() {
        String modelId = "non-existent";
        when(modelRepository.findById(modelId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getModel(modelId));
        assertTrue(exception.getMessage().contains("Model not found"));
    }

    @Test
    @DisplayName("listModels returns all models")
    void listModels_success() {
        MLModelEntity entity1 = MLModelEntity.builder().modelId("model-1").build();
        MLModelEntity entity2 = MLModelEntity.builder().modelId("model-2").build();
        MLModel model1 = new MLModel("model-1", "SENTIMENT", "n", "d", "v",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(),
                "p", true, "u", 1, 0.9, "TRAINED");
        MLModel model2 = new MLModel("model-2", "CLASSIFICATION", "n", "d", "v",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(),
                "p", true, "u", 1, 0.9, "TRAINED");
        when(modelRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(modelMapper.toDomain(entity1)).thenReturn(model1);
        when(modelMapper.toDomain(entity2)).thenReturn(model2);
        List<MLModel> result = service.listModels();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("deleteModel successfully deletes existing model")
    void deleteModel_success() {
        String modelId = "model-1";
        when(modelRepository.existsById(modelId)).thenReturn(true);
        doNothing().when(modelRepository).deleteById(modelId);
        boolean result = service.deleteModel(modelId);
        assertTrue(result);
        verify(modelRepository).deleteById(modelId);
    }

    @Test
    @DisplayName("deleteModel returns false when model not found")
    void deleteModel_modelNotFound_returnsFalse() {
        String modelId = "non-existent";
        when(modelRepository.existsById(modelId)).thenReturn(false);
        boolean result = service.deleteModel(modelId);
        assertFalse(result);
        verify(modelRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("deleteModel throws exception when modelId is null")
    void deleteModel_nullModelId_throwsException() {
        assertThrows(NullPointerException.class, () -> service.deleteModel(null));
    }

}
