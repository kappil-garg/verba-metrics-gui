package com.kapil.verbametrics.ml.utils;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for creating Weka datasets from various data formats via centralized methods.
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
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("text"));
        attributes.add(new Attribute("label"));
        Instances dataset = new Instances(datasetName, attributes, data.size());
        dataset.setClassIndex(1);
        for (Map<String, Object> dataPoint : data) {
            DenseInstance instance = new DenseInstance(2);
            instance.setValue(0, (String) dataPoint.get("text"));
            instance.setValue(1, (Integer) dataPoint.getOrDefault("label", 0));
            dataset.add(instance);
        }
        return dataset;
    }

    /**
     * Creates a Weka Instances dataset from a single input data point for prediction.
     * This method is optimized for single-instance predictions.
     *
     * @param input       The input data map containing "text" key
     * @param datasetName The name for the dataset (e.g., "PredictionDataset")
     * @return A Weka Instances object with a single instance ready for prediction
     */
    public static Instances createSingleInstanceDataset(Map<String, Object> input, String datasetName) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (datasetName == null || datasetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Dataset name cannot be null or empty");
        }
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("text"));
        attributes.add(new Attribute("label"));
        Instances dataset = new Instances(datasetName, attributes, 1);
        dataset.setClassIndex(1);
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(0, (String) input.get("text"));
        instance.setValue(1, 0);    // Placeholder label
        dataset.add(instance);
        return dataset;
    }

}
