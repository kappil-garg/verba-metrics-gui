package com.kapil.verbametrics.ml.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MLModelProperties.
 *
 * @author Kapil Garg
 */
class MLModelPropertiesTest {

    private MLModelProperties properties;

    @BeforeEach
    void setUp() {
        properties = new MLModelProperties();
    }

    @Test
    @DisplayName("Constructor should initialize with empty collections")
    void constructor_initializesWithEmptyCollections() {
        assertNotNull(properties.getTrainingLimits(), "Training limits should not be null");
        assertNotNull(properties.getDefaultParameters(), "Default parameters should not be null");
        assertNotNull(properties.getSupportedAlgorithms(), "Supported algorithms should not be null");
        assertNotNull(properties.getSupportedModelTypes(), "Supported model types should not be null");
        assertNotNull(properties.getFileSettings(), "File settings should not be null");
        assertNotNull(properties.getCacheSettings(), "Cache settings should not be null");
        assertNotNull(properties.getRequiredFields(), "Required fields should not be null");
        assertNotNull(properties.getPerformanceThresholds(), "Performance thresholds should not be null");
        assertTrue(properties.getTrainingLimits().isEmpty(), "Training limits should be empty");
        assertTrue(properties.getDefaultParameters().isEmpty(), "Default parameters should be empty");
        assertTrue(properties.getSupportedAlgorithms().isEmpty(), "Supported algorithms should be empty");
        assertTrue(properties.getSupportedModelTypes().isEmpty(), "Supported model types should be empty");
        assertTrue(properties.getFileSettings().isEmpty(), "File settings should be empty");
        assertTrue(properties.getCacheSettings().isEmpty(), "Cache settings should be empty");
        assertTrue(properties.getRequiredFields().isEmpty(), "Required fields should be empty");
        assertTrue(properties.getPerformanceThresholds().isEmpty(), "Performance thresholds should be empty");
    }

    @Test
    @DisplayName("setTrainingLimits should update training limits")
    void setTrainingLimits_updatesLimits() {
        Map<String, Integer> limits = Map.of("maxDataSize", 1000, "maxIterations", 500);
        properties.setTrainingLimits(limits);
        assertEquals(limits, properties.getTrainingLimits(), "Should update training limits");
        assertEquals(1000, properties.getTrainingLimits().get("maxDataSize"), "Should have correct max data size");
    }

    @Test
    @DisplayName("setDefaultParameters should update default parameters")
    void setDefaultParameters_updatesParameters() {
        Map<String, Object> params = Map.of("learningRate", 0.01, "batchSize", 32);
        properties.setDefaultParameters(params);
        assertEquals(params, properties.getDefaultParameters(), "Should update default parameters");
    }

    @Test
    @DisplayName("setSupportedAlgorithms should update supported algorithms")
    void setSupportedAlgorithms_updatesAlgorithms() {
        Map<String, String> algorithms = Map.of("CLASSIFICATION", "RandomForest", "SENTIMENT", "NaiveBayes");
        properties.setSupportedAlgorithms(algorithms);
        assertEquals(algorithms, properties.getSupportedAlgorithms(), "Should update supported algorithms");
    }

    @Test
    @DisplayName("setSupportedModelTypes should update model types")
    void setSupportedModelTypes_updatesModelTypes() {
        List<String> types = List.of("CLASSIFICATION", "SENTIMENT", "REGRESSION");
        properties.setSupportedModelTypes(types);
        assertEquals(types, properties.getSupportedModelTypes(), "Should update supported model types");
        assertEquals(3, properties.getSupportedModelTypes().size(), "Should have 3 model types");
    }

    @Test
    @DisplayName("setFileSettings should update file settings")
    void setFileSettings_updatesFileSettings() {
        Map<String, String> fileSettings = Map.of("modelDirectory", "/models", "extension", ".model");
        properties.setFileSettings(fileSettings);
        assertEquals(fileSettings, properties.getFileSettings(), "Should update file settings");
    }

    @Test
    @DisplayName("setCacheSettings should update cache settings")
    void setCacheSettings_updatesCacheSettings() {
        Map<String, Integer> cacheSettings = Map.of("maxModels", 100, "ttlMinutes", 60);
        properties.setCacheSettings(cacheSettings);
        assertEquals(cacheSettings, properties.getCacheSettings(), "Should update cache settings");
    }

    @Test
    @DisplayName("setRequiredFields should update required fields")
    void setRequiredFields_updatesRequiredFields() {
        Map<String, List<String>> requiredFields = Map.of(
                "CLASSIFICATION", List.of("text", "label"),
                "SENTIMENT", List.of("text", "sentiment")
        );
        properties.setRequiredFields(requiredFields);
        assertEquals(requiredFields, properties.getRequiredFields(), "Should update required fields");
    }

    @Test
    @DisplayName("setPerformanceThresholds should update performance thresholds")
    void setPerformanceThresholds_updatesThresholds() {
        Map<String, Double> thresholds = Map.of("excellent", 0.9, "good", 0.8, "fair", 0.7);
        properties.setPerformanceThresholds(thresholds);
        assertEquals(thresholds, properties.getPerformanceThresholds(), "Should update performance thresholds");
    }

    @Test
    @DisplayName("Properties should support null values")
    void properties_supportNullValues() {
        properties.setTrainingLimits(null);
        properties.setDefaultParameters(null);
        properties.setSupportedAlgorithms(null);
        assertNull(properties.getTrainingLimits(), "Training limits can be null");
        assertNull(properties.getDefaultParameters(), "Default parameters can be null");
        assertNull(properties.getSupportedAlgorithms(), "Supported algorithms can be null");
    }

    @Test
    @DisplayName("Properties should handle multiple updates")
    void properties_handleMultipleUpdates() {
        Map<String, Integer> limits1 = Map.of("max", 100);
        Map<String, Integer> limits2 = Map.of("max", 200);
        properties.setTrainingLimits(limits1);
        assertEquals(100, properties.getTrainingLimits().get("max"), "Should have first value");
        properties.setTrainingLimits(limits2);
        assertEquals(200, properties.getTrainingLimits().get("max"), "Should have updated value");
    }

}
