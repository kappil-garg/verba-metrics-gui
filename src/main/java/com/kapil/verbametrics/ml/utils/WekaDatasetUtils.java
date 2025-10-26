package com.kapil.verbametrics.ml.utils;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.*;

/**
 * Utility class for creating Weka datasets from various data formats used in machine learning tasks.
 *
 * @author Kapil Garg
 */
public class WekaDatasetUtils {

    /**
     * Creates a Weka Instances dataset from a list of training/test data.
     * This method handles the common pattern of creating datasets with text and label attributes.
     *
     * @param data        The list of data maps containing "text" and "label" keys
     * @param datasetName The name for the dataset (e.g., "TrainingDataset", "TestDataset")
     * @return A Weka Instances object ready for machine learning operations
     */
    public static Instances createDataset(List<Map<String, Object>> data, String datasetName) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        if (datasetName == null || datasetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Dataset name cannot be null or empty");
        }
        Map<String, Object> firstData = data.getFirst();
        DatasetMetadata metadata = createDatasetSchema(firstData, data);
        Instances dataset = new Instances(datasetName, metadata.attributes(), data.size());
        dataset.setClassIndex(metadata.attributes().size() - 1);
        for (Map<String, Object> dataPoint : data) {
            DenseInstance instance = createInstance(dataPoint, dataset, metadata);
            dataset.add(instance);
        }
        return dataset;
    }

    /**
     * Build dataset metadata including attributes and feature information.
     *
     * @param sample  A sample data point to infer structure
     * @param allData The complete list of data points for label analysis
     * @return DatasetMetadata containing attributes and feature info
     */
    private static DatasetMetadata createDatasetSchema(Map<String, Object> sample, List<Map<String, Object>> allData) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<String> classValues = new ArrayList<>();
        attributes.add(new Attribute("text", (List<String>) null));
        int featureCount = createFeatureAttributes(sample, attributes);
        boolean isStringLabel = addLabelAttribute(sample, allData, attributes, classValues);
        return new DatasetMetadata(attributes, featureCount, isStringLabel, classValues);
    }

    /**
     * Create feature attributes based on the sample data point.
     *
     * @param sample     A sample data point to infer feature structure
     * @param attributes The list of attributes to append feature attributes to
     * @return The number of feature attributes added
     */
    private static int createFeatureAttributes(Map<String, Object> sample, ArrayList<Attribute> attributes) {
        if (sample == null || !sample.containsKey("features") || sample.get("features") == null) {
            return 0;
        }
        Object feats = sample.get("features");
        int featureCount = 0;
        if (feats instanceof double[]) {
            featureCount = ((double[]) feats).length;
        } else if (feats instanceof List) {
            featureCount = ((List<?>) feats).size();
        }
        for (int i = 0; i < featureCount; i++) {
            attributes.add(new Attribute("feature_" + i));
        }
        return featureCount;
    }

    /**
     * Add label attribute based on data type and collect class values if needed.
     *
     * @param sample      A sample data point to infer label structure
     * @param allData     The complete list of data points for label analysis
     * @param attributes  The list of attributes to append the label attribute to
     * @param classValues The list to collect unique string label values
     * @return true if the label is of string type, false otherwise
     */
    private static boolean addLabelAttribute(Map<String, Object> sample, List<Map<String, Object>> allData,
                                             ArrayList<Attribute> attributes, ArrayList<String> classValues) {
        boolean isStringLabel = sample != null && sample.get("label") instanceof String;
        if (isStringLabel) {
            collectClassValues(allData, classValues);
            attributes.add(new Attribute("label", classValues));
        } else {
            attributes.add(new Attribute("label"));
        }
        return isStringLabel;
    }

    /**
     * Collect unique string label values from all data points.
     *
     * @param allData     The complete list of data points
     * @param classValues The list to collect unique string label values
     */
    private static void collectClassValues(List<Map<String, Object>> allData, ArrayList<String> classValues) {
        Set<String> seen = new LinkedHashSet<>();
        for (Map<String, Object> dataPoint : allData) {
            Object label = dataPoint.get("label");
            if (label instanceof String) {
                seen.add((String) label);
            }
        }
        classValues.addAll(seen);
    }

    /**
     * Create a Weka DenseInstance from a data point.
     *
     * @param dataPoint The data point map containing "text", "features", and "label" keys
     * @param dataset   The Weka Instances dataset to which this instance will belong
     * @param metadata  The dataset metadata containing attribute and feature info
     * @return A Weka DenseInstance representing the data point
     */
    private static DenseInstance createInstance(Map<String, Object> dataPoint, Instances dataset, DatasetMetadata metadata) {
        int attributeCount = metadata.attributes().size();
        DenseInstance instance = new DenseInstance(attributeCount);
        instance.setDataset(dataset);
        Object textObj = dataPoint.get("text");
        instance.setValue(0, textObj == null ? "" : textObj.toString());
        int featureCount = metadata.featureCount();
        if (featureCount > 0) {
            Object features = dataPoint.get("features");
            if (features instanceof double[] arr) {
                for (int i = 0; i < featureCount; i++) {
                    double value = i < arr.length ? arr[i] : 0.0;
                    instance.setValue(1 + i, value);
                }
            } else if (features instanceof List<?> list) {
                for (int i = 0; i < featureCount; i++) {
                    double value = 0.0;
                    if (i < list.size() && list.get(i) instanceof Number) {
                        value = ((Number) list.get(i)).doubleValue();
                    }
                    instance.setValue(1 + i, value);
                }
            } else {
                for (int i = 0; i < featureCount; i++) {
                    instance.setValue(1 + i, 0.0);
                }
            }
        }
        int labelIndex = attributeCount - 1;
        if (metadata.stringLabel()) {
            Object labelObj = dataPoint.get("label");
            String label = labelObj == null ? null : labelObj.toString();
            Attribute labelAttr = dataset.attribute(labelIndex);
            if (label != null) {
                if (labelAttr.indexOfValue(label) == -1) {
                    instance.setMissing(labelAttr);
                } else {
                    instance.setValue(labelAttr, label);
                }
            } else {
                instance.setMissing(labelAttr);
            }
        } else {
            Object labelValue = dataPoint.get("label");
            double value = 0.0;
            if (labelValue instanceof Number) {
                value = ((Number) labelValue).doubleValue();
            }
            instance.setValue(labelIndex, value);
        }
        return instance;
    }

    /**
     * Metadata holder for dataset creation.
     *
     * @param attributes   The list of Weka attributes
     * @param featureCount The number of feature attributes
     * @param stringLabel  Whether the label is of string type
     * @param classValues  The list of unique string label values
     */
    private record DatasetMetadata(
            ArrayList<Attribute> attributes,
            int featureCount,
            boolean stringLabel,
            ArrayList<String> classValues
    ) {
    }

}
