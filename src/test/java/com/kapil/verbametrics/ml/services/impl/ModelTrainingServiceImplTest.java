package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.engines.ModelTrainingEngine;
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
import static org.mockito.Mockito.*;

/**
 * Test class for ModelTrainingServiceImpl.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class ModelTrainingServiceImplTest {

    @Mock
    private ModelTrainingEngine trainingEngine;

    private ModelTrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ModelTrainingServiceImpl(trainingEngine);
    }

    @Test
    @DisplayName("trainModel successfully trains model")
    void trainModel_success() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of(
                Map.of("text", "Great!", "label", "POSITIVE", "features", new double[]{1.0, 2.0})
        );
        Map<String, Object> parameters = Map.of("name", "TestModel");
        ModelTrainingResult expectedResult = new ModelTrainingResult(
                "model-1", modelType, true, 0.85, 0.80, 0.82, 0.81,
                100L, 1, 0, Map.of(), null, LocalDateTime.now()
        );
        when(trainingEngine.validateTrainingDataError(trainingData, modelType))
                .thenReturn(Optional.empty());
        when(trainingEngine.trainModel(anyString(), eq(modelType), eq(trainingData), eq(parameters)))
                .thenReturn(expectedResult);
        ModelTrainingResult result = service.trainModel(modelType, trainingData, parameters);
        assertNotNull(result);
        assertEquals("model-1", result.modelId());
        assertTrue(result.success());
        verify(trainingEngine).validateTrainingDataError(trainingData, modelType);
        verify(trainingEngine).trainModel(anyString(), eq(modelType), eq(trainingData), eq(parameters));
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
    @DisplayName("trainModel handles training engine exception")
    void trainModel_engineThrowsException_wrapsException() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of(
                Map.of("text", "Test", "label", "POSITIVE", "features", new double[]{1.0})
        );
        Map<String, Object> parameters = Map.of();
        when(trainingEngine.validateTrainingDataError(trainingData, modelType))
                .thenReturn(Optional.empty());
        when(trainingEngine.trainModel(anyString(), eq(modelType), eq(trainingData), eq(parameters)))
                .thenThrow(new RuntimeException("Training failed"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.trainModel(modelType, trainingData, parameters));
        assertTrue(exception.getMessage().contains("Model training failed"));
    }

    @Test
    @DisplayName("validateTrainingData returns true when valid")
    void validateTrainingData_valid_returnsTrue() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of(
                Map.of("text", "Test", "label", "POSITIVE", "features", new double[]{1.0})
        );
        when(trainingEngine.validateTrainingDataError(trainingData, modelType))
                .thenReturn(Optional.empty());
        boolean result = service.validateTrainingData(trainingData, modelType);
        assertTrue(result);
    }

    @Test
    @DisplayName("validateTrainingData returns false when invalid")
    void validateTrainingData_invalid_returnsFalse() {
        String modelType = "SENTIMENT";
        List<Map<String, Object>> trainingData = List.of();
        when(trainingEngine.validateTrainingDataError(trainingData, modelType))
                .thenReturn(Optional.of("Training data is empty"));
        boolean result = service.validateTrainingData(trainingData, modelType);
        assertFalse(result);
    }

    @Test
    @DisplayName("getSupportedModelTypes delegates to engine")
    void getSupportedModelTypes_delegates() {
        List<String> expectedTypes = List.of("SENTIMENT", "CLASSIFICATION");
        when(trainingEngine.getSupportedModelTypes()).thenReturn(expectedTypes);
        List<String> result = service.getSupportedModelTypes();
        assertEquals(expectedTypes, result);
        verify(trainingEngine).getSupportedModelTypes();
    }

    @Test
    @DisplayName("getDefaultParameters delegates to engine")
    void getDefaultParameters_delegates() {
        String modelType = "SENTIMENT";
        Map<String, Object> expectedParams = Map.of("maxDepth", 5);
        when(trainingEngine.getDefaultParameters(modelType)).thenReturn(expectedParams);
        Map<String, Object> result = service.getDefaultParameters(modelType);
        assertEquals(expectedParams, result);
        verify(trainingEngine).getDefaultParameters(modelType);
    }

}
