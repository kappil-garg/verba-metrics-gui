package com.kapil.verbametrics.ml.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WekaDatasetUtils.
 *
 * @author Kapil Garg
 */
@DisplayName("WekaDatasetUtils Tests")
class WekaDatasetUtilsTest {

    @Test
    @DisplayName("createDataset should throw exception when data is null")
    void testCreateDataset_NullData() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WekaDatasetUtils.createDataset(null, "TestDataset"));
        assertEquals("Data cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("createDataset should throw exception when data is empty")
    void testCreateDataset_EmptyData() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WekaDatasetUtils.createDataset(Collections.emptyList(), "TestDataset"));
        assertEquals("Data cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("createDataset should throw exception when dataset name is null")
    void testCreateDataset_NullName() {
        List<Map<String, Object>> data = createSimpleData();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WekaDatasetUtils.createDataset(data, null));
        assertEquals("Dataset name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("createDataset should throw exception when dataset name is empty")
    void testCreateDataset_EmptyName() {
        List<Map<String, Object>> data = createSimpleData();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WekaDatasetUtils.createDataset(data, ""));
        assertEquals("Dataset name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("createDataset should throw exception when dataset name is blank")
    void testCreateDataset_BlankName() {
        List<Map<String, Object>> data = createSimpleData();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WekaDatasetUtils.createDataset(data, "   "));
        assertEquals("Dataset name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("createDataset should create dataset with text and string label only")
    void testCreateDataset_TextAndStringLabel() {
        List<Map<String, Object>> data = createSimpleData();
        Instances dataset = WekaDatasetUtils.createDataset(data, "TrainingDataset");
        assertNotNull(dataset);
        assertEquals("TrainingDataset", dataset.relationName());
        assertEquals(3, dataset.numInstances());
        assertEquals(2, dataset.numAttributes());   // text + label
        assertEquals(1, dataset.classIndex());      // label is class attribute
        assertEquals("text", dataset.attribute(0).name());
        assertTrue(dataset.attribute(0).isString());
        assertEquals("label", dataset.attribute(1).name());
        assertTrue(dataset.attribute(1).isNominal());
        assertEquals(2, dataset.attribute(1).numValues());
    }

    @Test
    @DisplayName("createDataset should create dataset with text and numeric label only")
    void testCreateDataset_TextAndNumericLabel() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", 1.0),
                Map.of("text", "Sample 2", "label", 0.0),
                Map.of("text", "Sample 3", "label", 1.0)
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NumericDataset");
        assertNotNull(dataset);
        assertEquals("NumericDataset", dataset.relationName());
        assertEquals(3, dataset.numInstances());
        assertEquals(2, dataset.numAttributes()); // text + label
        assertEquals("label", dataset.attribute(1).name());
        assertTrue(dataset.attribute(1).isNumeric());
    }

    @Test
    @DisplayName("createDataset should create dataset with text, features, and label")
    void testCreateDataset_WithDoubleArrayFeatures() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", new double[]{0.5, 0.8, 0.3}, "label", "positive"),
                Map.of("text", "Sample 2", "features", new double[]{0.2, 0.4, 0.6}, "label", "negative"),
                Map.of("text", "Sample 3", "features", new double[]{0.9, 0.7, 0.5}, "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "FeatureDataset");
        assertNotNull(dataset);
        assertEquals("FeatureDataset", dataset.relationName());
        assertEquals(3, dataset.numInstances());
        assertEquals(5, dataset.numAttributes()); // text + 3 features + label
        assertEquals(4, dataset.classIndex());
        assertEquals("feature_0", dataset.attribute(1).name());
        assertEquals("feature_1", dataset.attribute(2).name());
        assertEquals("feature_2", dataset.attribute(3).name());
    }

    @Test
    @DisplayName("createDataset should create dataset with list features")
    void testCreateDataset_WithListFeatures() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", Arrays.asList(0.5, 0.8, 0.3), "label", "positive"),
                Map.of("text", "Sample 2", "features", Arrays.asList(0.2, 0.4, 0.6), "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "ListFeatureDataset");
        assertNotNull(dataset);
        assertEquals(2, dataset.numInstances());
        assertEquals(5, dataset.numAttributes()); // text + 3 features + label
    }

    @Test
    @DisplayName("createDataset should handle single instance")
    void testCreateDataset_SingleInstance() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Single sample", "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "SingleInstance");
        assertNotNull(dataset);
        assertEquals(1, dataset.numInstances());
        assertEquals("Single sample", dataset.instance(0).stringValue(0));
    }

    @Test
    @DisplayName("createDataset should collect all unique string label values")
    void testCreateDataset_CollectAllLabels() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", "positive"),
                Map.of("text", "Sample 2", "label", "negative"),
                Map.of("text", "Sample 3", "label", "neutral"),
                Map.of("text", "Sample 4", "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "MultiLabelDataset");
        Attribute labelAttr = dataset.classAttribute();
        assertEquals(3, labelAttr.numValues()); // positive, negative, neutral
        assertTrue(labelAttr.indexOfValue("positive") >= 0);
        assertTrue(labelAttr.indexOfValue("negative") >= 0);
        assertTrue(labelAttr.indexOfValue("neutral") >= 0);
    }

    @Test
    @DisplayName("createDataset should preserve label order using LinkedHashSet")
    void testCreateDataset_LabelOrder() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", "positive"),
                Map.of("text", "Sample 2", "label", "negative"),
                Map.of("text", "Sample 3", "label", "neutral")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "LabelOrderDataset");
        Attribute labelAttr = dataset.classAttribute();
        assertEquals("positive", labelAttr.value(0));
        assertEquals("negative", labelAttr.value(1));
        assertEquals("neutral", labelAttr.value(2));
    }

    @Test
    @DisplayName("createDataset should handle null label value gracefully")
    void testCreateDataset_NullLabel() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", "positive"),
                createMapWithNullLabel()
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NullLabelDataset");
        assertNotNull(dataset);
        assertEquals(2, dataset.numInstances());
        assertTrue(dataset.instance(1).classIsMissing());
    }

    @Test
    @DisplayName("createDataset should handle unknown label value by setting missing")
    void testCreateDataset_UnknownLabel() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", "positive"),
                Map.of("text", "Sample 2", "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "KnownLabels");
        DenseInstance newInstance = new DenseInstance(2);
        newInstance.setDataset(dataset);
        newInstance.setValue(0, "Sample 3");
        assertNotNull(dataset);
    }

    @Test
    @DisplayName("createDataset should handle missing features field")
    void testCreateDataset_MissingFeatures() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "label", "positive"),
                Map.of("text", "Sample 2", "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NoFeatures");
        assertNotNull(dataset);
        assertEquals(2, dataset.numAttributes());
    }

    @Test
    @DisplayName("createDataset should handle null features field")
    void testCreateDataset_NullFeatures() {
        List<Map<String, Object>> data = List.of(
                createMapWithNullFeatures(),
                Map.of("text", "Sample 2", "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NullFeatures");
        assertNotNull(dataset);
        assertEquals(2, dataset.numAttributes());
    }

    @Test
    @DisplayName("createDataset should handle features with shorter array length")
    void testCreateDataset_ShorterFeatureArray() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", new double[]{0.5, 0.8, 0.3}, "label", "positive"),
                Map.of("text", "Sample 2", "features", new double[]{0.2}, "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "ShorterFeatures");
        assertNotNull(dataset);
        assertEquals(5, dataset.numAttributes());
        DenseInstance instance2 = (DenseInstance) dataset.instance(1);
        assertEquals(0.2, instance2.value(1), 0.0001);
        assertEquals(0.0, instance2.value(2), 0.0001);
        assertEquals(0.0, instance2.value(3), 0.0001);
    }

    @Test
    @DisplayName("createDataset should handle features with List containing non-Number values")
    void testCreateDataset_ListWithNonNumbers() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", Arrays.asList(0.5, "invalid", 0.3), "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NonNumberFeatures");
        assertNotNull(dataset);
        DenseInstance instance = (DenseInstance) dataset.instance(0);
        assertEquals(0.5, instance.value(1), 0.0001);
        assertEquals(0.0, instance.value(2), 0.0001);
        assertEquals(0.3, instance.value(3), 0.0001);
    }

    @Test
    @DisplayName("createDataset should handle features as unsupported type")
    void testCreateDataset_UnsupportedFeatureType() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", "not-an-array", "label", "positive"),
                Map.of("text", "Sample 2", "features", "not-an-array", "label", "negative")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "UnsupportedFeatures");
        assertNotNull(dataset);
        // Should default to 0 features when type is unsupported
        assertEquals(2, dataset.numAttributes());
    }

    @Test
    @DisplayName("createDataset should handle empty feature array")
    void testCreateDataset_EmptyFeatureArray() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Sample 1", "features", new double[]{}, "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "EmptyFeatures");
        assertNotNull(dataset);
        assertEquals(2, dataset.numAttributes());
    }

    @Test
    @DisplayName("createDataset should handle null text value")
    void testCreateDataset_NullText() {
        List<Map<String, Object>> data = List.of(
                createMapWithNullText()
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NullText");
        assertNotNull(dataset);
        assertEquals("", dataset.instance(0).stringValue(0));
    }

    @Test
    @DisplayName("createDataset should handle empty text value")
    void testCreateDataset_EmptyText() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "", "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "EmptyText");
        assertNotNull(dataset);
        assertEquals("", dataset.instance(0).stringValue(0));
    }

    @Test
    @DisplayName("createDataset should convert non-string text to string")
    void testCreateDataset_NonStringText() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", 12345, "label", "positive")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "NumericText");
        assertNotNull(dataset);
        assertEquals("12345", dataset.instance(0).stringValue(0));
    }

    @Test
    @DisplayName("createDataset should create correct dataset for classification task")
    void testCreateDataset_ClassificationScenario() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "This is great!", "features", new double[]{0.9, 0.1}, "label", "positive"),
                Map.of("text", "This is bad!", "features", new double[]{0.1, 0.9}, "label", "negative"),
                Map.of("text", "This is okay.", "features", new double[]{0.5, 0.5}, "label", "neutral")
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "SentimentDataset");
        assertEquals(3, dataset.numInstances());
        assertEquals(4, dataset.numAttributes());
        assertEquals(3, dataset.classIndex());
        assertEquals(3, dataset.classAttribute().numValues());
        DenseInstance first = (DenseInstance) dataset.instance(0);
        assertEquals("This is great!", first.stringValue(0));
        assertEquals(0.9, first.value(1), 0.0001);
        assertEquals(0.1, first.value(2), 0.0001);
        assertEquals("positive", first.stringValue(3));
    }

    @Test
    @DisplayName("createDataset should create correct dataset for regression task")
    void testCreateDataset_RegressionScenario() {
        List<Map<String, Object>> data = List.of(
                Map.of("text", "Text 1", "features", new double[]{1.0, 2.0}, "label", 5.5),
                Map.of("text", "Text 2", "features", new double[]{2.0, 3.0}, "label", 7.8),
                Map.of("text", "Text 3", "features", new double[]{3.0, 4.0}, "label", 9.2)
        );
        Instances dataset = WekaDatasetUtils.createDataset(data, "RegressionDataset");
        assertEquals(3, dataset.numInstances());
        assertEquals(4, dataset.numAttributes());
        assertTrue(dataset.classAttribute().isNumeric());
        assertEquals(5.5, dataset.instance(0).classValue(), 0.0001);
        assertEquals(7.8, dataset.instance(1).classValue(), 0.0001);
        assertEquals(9.2, dataset.instance(2).classValue(), 0.0001);
    }

    /**
     * Creates a simple dataset with text and string labels.
     *
     * @return List of maps representing the dataset
     */
    private List<Map<String, Object>> createSimpleData() {
        return List.of(
                Map.of("text", "This is a positive sample", "label", "positive"),
                Map.of("text", "This is a negative sample", "label", "negative"),
                Map.of("text", "Another positive sample", "label", "positive")
        );
    }

    /**
     * Creates a map with a null label value.
     *
     * @return Map with text and null label
     */
    private Map<String, Object> createMapWithNullLabel() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", "Sample with null label");
        map.put("label", null);
        return map;
    }

    /**
     * Creates a map with null features.
     *
     * @return Map with text, null features, and label
     */
    private Map<String, Object> createMapWithNullFeatures() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", "Sample 1");
        map.put("features", null);
        map.put("label", "positive");
        return map;
    }

    /**
     * Creates a map with null text.
     *
     * @return Map with null text and label
     */
    private Map<String, Object> createMapWithNullText() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", null);
        map.put("label", "positive");
        return map;
    }

}
