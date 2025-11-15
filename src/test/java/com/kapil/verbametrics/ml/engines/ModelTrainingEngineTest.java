package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.classifiers.ModelTypeClassifier;
import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelTrainingEngine.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class ModelTrainingEngineTest {

    @Mock
    private MLModelProperties properties;

    @Mock
    private ModelFileManager fileManager;

    @Mock
    private ModelTypeClassifier modelTypeClassifier;

    @Mock
    private ClassValueManager classValueManager;

    private ModelTrainingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ModelTrainingEngine(properties, fileManager, modelTypeClassifier, classValueManager);
    }

    @Test
    @DisplayName("trainModel: throws exception when modelId is null")
    void trainModel_nullModelId_throwsException() {
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        Map<String, Object> parameters = new HashMap<>();
        assertThrows(NullPointerException.class, () ->
                engine.trainModel(null, VerbaMetricsConstants.K_SENTIMENT, trainingData, parameters));
    }

    @Test
    @DisplayName("trainModel: throws exception when modelType is null")
    void trainModel_nullModelType_throwsException() {
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        Map<String, Object> parameters = new HashMap<>();
        assertThrows(NullPointerException.class, () ->
                engine.trainModel("model-1", null, trainingData, parameters));
    }

    @Test
    @DisplayName("trainModel: throws exception when trainingData is null")
    void trainModel_nullTrainingData_throwsException() {
        Map<String, Object> parameters = new HashMap<>();
        assertThrows(NullPointerException.class, () ->
                engine.trainModel("model-1", VerbaMetricsConstants.K_SENTIMENT, null, parameters));
    }

    @Test
    @DisplayName("trainModel: throws exception when parameters is null")
    void trainModel_nullParameters_throwsException() {
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        assertThrows(NullPointerException.class, () ->
                engine.trainModel("model-1", VerbaMetricsConstants.K_SENTIMENT, trainingData, null));
    }

    @Test
    @DisplayName("trainModel: returns failed result for unsupported model type")
    void trainModel_unsupportedModelType_returnsFailed() {
        String modelId = "test-model-3";
        String modelType = "UNSUPPORTED_TYPE";
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        Map<String, Object> parameters = new HashMap<>();
        ModelTrainingResult result = engine.trainModel(modelId, modelType, trainingData, parameters);
        assertFalse(result.success());
        assertNotNull(result.errorMessage());
        assertTrue(result.errorMessage().contains("Unsupported model type"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns empty for valid sentiment data")
    void validateTrainingDataError_validSentimentData_returnsEmpty() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(true);
        when(modelTypeClassifier.getRequiredFields(modelType)).thenReturn(List.of());
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isEmpty());
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for null training data")
    void validateTrainingDataError_nullTrainingData_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        Optional<String> error = engine.validateTrainingDataError(null, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("empty"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for empty training data")
    void validateTrainingDataError_emptyTrainingData_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = new ArrayList<>();
        // Empty data check happens before training limits check, so no need to mock it
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("empty"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for insufficient training data")
    void validateTrainingDataError_insufficientData_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = createValidTrainingData(5);
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("at least"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for unsupported model type")
    void validateTrainingDataError_unsupportedModelType_returnsError() {
        String modelType = "UNSUPPORTED";
        List<Map<String, Object>> trainingData = createValidTrainingData(15);
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(false);
        when(properties.getSupportedModelTypes()).thenReturn(List.of("SENTIMENT", "CLASSIFICATION"));
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Unsupported model type"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for missing label")
    void validateTrainingDataError_missingLabel_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = new ArrayList<>();
        // Create enough records to pass min-data-size check
        for (int i = 0; i < 15; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("features", new double[]{0.1, 0.2, 0.3});
            trainingData.add(record);
        }
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(true);
        when(modelTypeClassifier.getRequiredFields(modelType)).thenReturn(List.of());
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("missing label"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for missing features")
    void validateTrainingDataError_missingFeatures_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = new ArrayList<>();
        // Create enough records to pass min-data-size check
        for (int i = 0; i < 15; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("label", i % 2 == 0 ? "positive" : "negative");
            trainingData.add(record);
        }
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(true);
        when(modelTypeClassifier.getRequiredFields(modelType)).thenReturn(List.of());
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("missing features"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for single class")
    void validateTrainingDataError_singleClass_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("label", "positive");
            record.put("features", new double[]{0.1, 0.2, 0.3});
            trainingData.add(record);
        }
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(true);
        when(modelTypeClassifier.getRequiredFields(modelType)).thenReturn(List.of());
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("At least 2 distinct label classes"));
    }

    @Test
    @DisplayName("validateTrainingDataError: returns error for inconsistent feature lengths")
    void validateTrainingDataError_inconsistentFeatureLengths_returnsError() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        List<Map<String, Object>> trainingData = new ArrayList<>();
        // Create enough records to pass min-data-size check
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("label", i % 2 == 0 ? "positive" : "negative");
            // First 5 records have 3 features, rest have 2 features
            if (i < 5) {
                record.put("features", new double[]{0.1, 0.2, 0.3});
            } else {
                record.put("features", new double[]{0.4, 0.5}); // Different length
            }
            trainingData.add(record);
        }
        when(properties.getTrainingLimits()).thenReturn(Map.of("min-data-size", 10));
        when(modelTypeClassifier.isValidModelType(modelType)).thenReturn(true);
        when(modelTypeClassifier.getRequiredFields(modelType)).thenReturn(List.of());
        Optional<String> error = engine.validateTrainingDataError(trainingData, modelType);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("same length") || error.get().contains("length"));
    }

    @Test
    @DisplayName("getSupportedModelTypes: returns list from properties")
    void getSupportedModelTypes_returnsListFromProperties() {
        List<String> expectedTypes = List.of("SENTIMENT", "CLASSIFICATION");
        when(properties.getSupportedModelTypes()).thenReturn(expectedTypes);
        List<String> result = engine.getSupportedModelTypes();
        assertEquals(expectedTypes, result);
    }

    @Test
    @DisplayName("getDefaultParameters: returns parameters from properties")
    void getDefaultParameters_returnsParametersFromProperties() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        Map<String, Object> expectedParams = Map.of("max-depth", 5, "min-samples-split", 2);
        when(properties.getDefaultParameters()).thenReturn(Map.of(modelType, expectedParams));
        Map<String, Object> result = engine.getDefaultParameters(modelType);
        assertEquals(expectedParams, result);
    }

    @Test
    @DisplayName("getDefaultParameters: returns empty map when not found")
    void getDefaultParameters_notFound_returnsEmptyMap() {
        String modelType = "UNKNOWN_TYPE";
        when(properties.getDefaultParameters()).thenReturn(Map.of());
        Map<String, Object> result = engine.getDefaultParameters(modelType);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDefaultParameters: returns empty map when value is not a map")
    void getDefaultParameters_invalidType_returnsEmptyMap() {
        String modelType = VerbaMetricsConstants.K_SENTIMENT;
        when(properties.getDefaultParameters()).thenReturn(Map.of(modelType, "not-a-map"));
        Map<String, Object> result = engine.getDefaultParameters(modelType);
        assertTrue(result.isEmpty());
    }

    /**
     * Creates valid training data for testing.
     *
     * @param size the number of records to create
     * @return a list of training data records
     */
    private List<Map<String, Object>> createValidTrainingData(int size) {
        List<Map<String, Object>> trainingData = new ArrayList<>();
        String[] labels = {"positive", "negative"};
        for (int i = 0; i < size; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("label", labels[i % 2]);
            record.put("features", new double[]{0.1 + i * 0.01, 0.2 + i * 0.01, 0.3 + i * 0.01});
            trainingData.add(record);
        }
        return trainingData;
    }

}
