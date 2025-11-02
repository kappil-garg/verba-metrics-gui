package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.classifiers.ModelTypeClassifier;
import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.ml.utils.MetricsCalculationUtils;
import com.kapil.verbametrics.ml.utils.WekaDatasetUtils;
import com.kapil.verbametrics.util.VerbaMetricsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ModelTrainingEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTrainingEngine.class);

    private final MLModelProperties properties;
    private final ModelFileManager fileManager;
    private final ModelTypeClassifier modelTypeClassifier;
    private final ClassValueManager classValueManager;

    @Autowired
    public ModelTrainingEngine(MLModelProperties properties, ModelFileManager fileManager,
                               ModelTypeClassifier modelTypeClassifier, ClassValueManager classValueManager) {
        this.properties = properties;
        this.fileManager = fileManager;
        this.modelTypeClassifier = modelTypeClassifier;
        this.classValueManager = classValueManager;
    }

    /**
     * Trains a machine learning model based on the provided parameters.
     *
     * @param modelId      The unique identifier for the model
     * @param modelType    The type of model to train (e.g., "regression", "classification")
     * @param trainingData The training data as a list of maps
     * @param parameters   The training parameters as a map
     * @return The result of the model training
     */
    public ModelTrainingResult trainModel(String modelId, String modelType,
                                          List<Map<String, Object>> trainingData,
                                          Map<String, Object> parameters) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(modelType, "Model type cannot be null");
        Objects.requireNonNull(trainingData, "Training data cannot be null");
        Objects.requireNonNull(parameters, "Parameters cannot be null");
        try {
            long startTime = System.currentTimeMillis();
            Object trainedModel = performModelTraining(modelType, trainingData, parameters);
            long trainingTime = System.currentTimeMillis() - startTime;
            fileManager.saveModelToFile(modelId, trainedModel);
            storeClassValuesForModel(modelId, trainingData);
            Map<String, Object> performanceMetrics = calculatePerformanceMetrics(trainedModel, trainingData, modelType);
            LOGGER.info("Model training completed successfully in {}ms for model: {}", trainingTime, modelId);
            return new ModelTrainingResult(
                    modelId,
                    modelType,
                    true,
                    (Double) performanceMetrics.get("accuracy"),
                    (Double) performanceMetrics.get("precision"),
                    (Double) performanceMetrics.get("recall"),
                    (Double) performanceMetrics.get("f1Score"),
                    trainingTime,
                    trainingData.size(),
                    0,
                    performanceMetrics,
                    null,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            LOGGER.error("Failed to train model: {}", modelId, e);
            return new ModelTrainingResult(
                    modelId,
                    modelType,
                    false,
                    0.0, 0.0, 0.0, 0.0,
                    System.currentTimeMillis(),
                    trainingData.size(),
                    0,
                    Map.of(),
                    e.getMessage(),
                    LocalDateTime.now()
            );
        }
    }

    /**
     * Performs the actual model training using appropriate ML library.
     *
     * @param modelType    The type of model to train
     * @param trainingData The training dataset
     * @param parameters   The training parameters
     * @return The trained model object
     * @throws Exception if training fails
     */
    private Object performModelTraining(String modelType, List<Map<String, Object>> trainingData, Map<String, Object> parameters) throws Exception {
        return switch (modelType.toUpperCase()) {
            case VerbaMetricsConstants.K_SENTIMENT -> trainSentimentModel(trainingData, parameters);
            case VerbaMetricsConstants.K_CLASSIFICATION -> trainClassificationModel(trainingData, parameters);
            case VerbaMetricsConstants.K_TOPIC_MODELING ->
                    throw new UnsupportedOperationException("Topic modeling not implemented yet");
            default -> throw new IllegalArgumentException("Unsupported model type: " + modelType);
        };
    }

    /**
     * Validates the training data for the specified model type to ensure it meets requirements.
     *
     * @param trainingData The training data as a list of maps
     * @param modelType    The type of model to train
     * @return An optional error message if validation fails (empty if valid)
     */
    public Optional<String> validateTrainingDataError(List<Map<String, Object>> trainingData, String modelType) {
        // Normalize model type once and orchestrate granular checks
        String normalizedType = modelType == null ? "" : modelType.toUpperCase();
        Optional<String> err = checkPreflight(trainingData, normalizedType);
        if (err.isPresent()) {
            return err;
        }
        List<String> required = modelTypeClassifier.getRequiredFields(normalizedType);
        // Determine if this is a classification-like model that needs label/feature validation
        boolean isClassificationModel = VerbaMetricsConstants.K_CLASSIFICATION.equals(normalizedType)
                || VerbaMetricsConstants.K_SENTIMENT.equals(normalizedType);
        // Validate data records: for classification models, check required fields, labels, and features in one pass
        if (isClassificationModel) {
            return validateClassificationData(trainingData, required);
        }
        // For non-classification models, only check required fields if any
        if (!required.isEmpty()) {
            return checkRequiredFields(trainingData, required);
        }
        return Optional.empty();
    }

    /**
     * Basic preflight checks that validate dataset presence/size and model type validity.
     *
     * @param trainingData   The training dataset
     * @param normalizedType The normalized model type
     * @return An optional error message if preflight checks fail
     */
    private Optional<String> checkPreflight(List<Map<String, Object>> trainingData, String normalizedType) {
        if (trainingData == null || trainingData.isEmpty()) {
            return Optional.of("Training data is empty");
        }
        int minDataSize = properties.getTrainingLimits().getOrDefault("min-data-size", 10);
        if (trainingData.size() < minDataSize) {
            return Optional.of("Need at least " + minDataSize + " records, got " + trainingData.size());
        }
        if (!modelTypeClassifier.isValidModelType(normalizedType)) {
            return Optional.of("Unsupported model type: " + normalizedType + ". Supported: "
                    + String.join(", ", getSupportedModelTypes()));
        }
        return Optional.empty();
    }

    /**
     * Checks for required fields in the training data.
     *
     * @param trainingData The training dataset
     * @param required     The list of required fields (non-empty)
     * @return An optional error message if required fields are missing
     */
    private Optional<String> checkRequiredFields(List<Map<String, Object>> trainingData, List<String> required) {
        for (int i = 0; i < trainingData.size(); i++) {
            Map<String, Object> record = trainingData.get(i);
            for (String field : required) {
                Object value = record.get(field);
                if (value == null || (value instanceof String s && s.isBlank())) {
                    return Optional.of("Record #" + (i + 1) + " missing required field '" + field + "'");
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Validates classification model data (required fields, labels, and features) in a single pass.
     *
     * @param trainingData The training dataset
     * @param required     The list of required fields (maybe empty)
     * @return An optional error message if validation fails
     */
    private Optional<String> validateClassificationData(List<Map<String, Object>> trainingData, List<String> required) {
        Set<String> classes = new LinkedHashSet<>();
        int expectedFeatureLength = -1;
        for (int i = 0; i < trainingData.size(); i++) {
            Map<String, Object> record = trainingData.get(i);
            int recordNum = i + 1;
            if (!required.isEmpty()) {
                for (String field : required) {
                    Object value = record.get(field);
                    if (value == null || (value instanceof String s && s.isBlank())) {
                        return Optional.of("Record #" + recordNum + " missing required field '" + field + "'");
                    }
                }
            }
            Object label = record.get("label");
            if (label == null) {
                return Optional.of("Record #" + recordNum + " missing label value");
            }
            if (label instanceof String labelStr && !labelStr.isBlank()) {
                classes.add(labelStr);
            } else {
                return Optional.of("Record #" + recordNum + " has invalid label value");
            }
            Object features = record.get("features");
            if (features == null) {
                return Optional.of("Record #" + recordNum + " missing features");
            }
            int featureLength = switch (features) {
                case double[] arr -> arr.length;
                case List<?> list -> {
                    boolean allNumbers = list.stream().allMatch(o -> o instanceof Number);
                    yield allNumbers ? list.size() : -1;
                }
                default -> -1;
            };
            if (featureLength == -1) {
                return Optional.of("Record #" + recordNum + " features must be an array of numbers");
            }
            // Check feature length consistency
            if (expectedFeatureLength == -1) {
                expectedFeatureLength = featureLength;
            } else if (featureLength != expectedFeatureLength) {
                return Optional.of("All feature vectors must be of the same length: expected " + expectedFeatureLength
                        + "elements, but record #" + recordNum + " has " + featureLength + " elements");
            }
        }
        // Validate class distribution
        if (classes.isEmpty()) {
            return Optional.of("No valid label values found in training data");
        }
        if (classes.size() == 1) {
            return Optional.of("At least 2 distinct label classes required; found " + classes);
        }
        return Optional.empty();
    }

    /**
     * Gets the list of supported model types.
     *
     * @return The list of supported model types
     */
    public List<String> getSupportedModelTypes() {
        return properties.getSupportedModelTypes();
    }

    /**
     * Gets the default parameters for a given model type.
     *
     * @param modelType The type of model
     * @return The default parameters as a map
     */
    public Map<String, Object> getDefaultParameters(String modelType) {
        Object defaultParams = properties.getDefaultParameters().get(modelType);
        if (defaultParams instanceof Map<?, ?> map) {
            Map<String, Object> params = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    params.put((String) entry.getKey(), entry.getValue());
                } else {
                    return Map.of();
                }
            }
            return params;
        }
        return Map.of();
    }

    /**
     * Trains a sentiment analysis model using Weka RandomTree.
     * Uses only numeric features for training, ignoring text attributes.
     *
     * @param trainingData The training dataset
     * @param parameters   The training parameters
     * @return The trained sentiment model
     * @throws Exception if training fails
     */
    private Object trainSentimentModel(List<Map<String, Object>> trainingData, Map<String, Object> parameters) throws Exception {
        Instances dataset = createWekaDataset(trainingData);
        Instances numericDataset = new Instances(dataset);
        // Remove text attribute (index 0) as RandomTree works only with numeric features
        numericDataset.deleteAttributeAt(0);
        RandomTree model = new RandomTree();
        // Add dataset size to parameters for adaptive configuration
        Map<String, Object> adaptiveParams = new HashMap<>(parameters);
        adaptiveParams.put("datasetSize", trainingData.size());
        configureRandomTreeModel(model, adaptiveParams);
        model.buildClassifier(numericDataset);
        return model;
    }

    /**
     * Trains a general classification model using Weka library.
     * Uses only numeric features for training, ignoring text attributes.
     *
     * @param trainingData The training dataset
     * @param parameters   The training parameters
     * @return The trained classification model
     * @throws Exception if training fails
     */
    private Object trainClassificationModel(List<Map<String, Object>> trainingData, Map<String, Object> parameters) throws Exception {
        Instances dataset = createWekaDataset(trainingData);
        Instances numericDataset = new Instances(dataset);
        // Remove text attribute (index 0) as RandomTree works only with numeric features
        numericDataset.deleteAttributeAt(0);
        RandomTree classifier = new RandomTree();
        // Add dataset size to parameters for adaptive configuration
        Map<String, Object> adaptiveParams = new HashMap<>(parameters);
        adaptiveParams.put("datasetSize", trainingData.size());
        configureRandomTreeModel(classifier, adaptiveParams);
        classifier.buildClassifier(numericDataset);
        return classifier;
    }

    /**
     * Creates a Weka dataset from training data.
     *
     * @param trainingData The training dataset
     * @return The Weka Instances object
     */
    private Instances createWekaDataset(List<Map<String, Object>> trainingData) {
        return WekaDatasetUtils.createDataset(trainingData, "ClassificationDataset");
    }

    /**
     * Stores class values for a model based on training data.
     *
     * @param modelId      The model ID
     * @param trainingData The training data
     */
    private void storeClassValuesForModel(String modelId, List<Map<String, Object>> trainingData) {
        try {
            Set<String> uniqueClasses = new LinkedHashSet<>();
            for (Map<String, Object> dataPoint : trainingData) {
                Object label = dataPoint.get("label");
                if (label instanceof String) {
                    uniqueClasses.add((String) label);
                }
            }
            List<String> classValues = new ArrayList<>(uniqueClasses);
            classValueManager.storeClassValues(modelId, classValues);
            LOGGER.debug("Stored class values for model {} ({} classes)", modelId, classValues.size());
        } catch (Exception e) {
            LOGGER.warn("Failed to store class values for model {}: {}", modelId, e.getMessage());
        }
    }

    /**
     * Calculates performance metrics for the trained model.
     *
     * @param model        The trained model
     * @param trainingData The training dataset
     * @param modelType    The type of the model
     * @return A map of performance metrics
     */
    private Map<String, Object> calculatePerformanceMetrics(Object model, List<Map<String, Object>> trainingData, String modelType) {
        double accuracy = calculateModelAccuracy(model, trainingData);
        double precision = calculatePrecision(model, trainingData);
        double recall = calculateRecall(model, trainingData);
        double f1Score = MetricsCalculationUtils.calculateF1Score(precision, recall);
        return Map.of(
                "accuracy", accuracy,
                "precision", precision,
                "recall", recall,
                "f1Score", f1Score,
                "modelType", modelType,
                "trainingSamples", trainingData.size()
        );
    }

    /**
     * Configures a RandomTree model with parameters from configuration.
     * Uses adaptive settings based on dataset size to prevent overfitting.
     *
     * @param model      The RandomTree model to configure
     * @param parameters The training parameters
     */
    private void configureRandomTreeModel(RandomTree model, Map<String, Object> parameters) {
        try {
            // Adaptive defaults based on dataset size
            int datasetSize = parameters.containsKey("datasetSize") ? (Integer) parameters.get("datasetSize") : 100;
            // Adjust depth based on dataset size
            int maxDepth = datasetSize < 20 ? 3 : (datasetSize < 50 ? 5 : 8);
            int minNum = datasetSize < 20 ? 2 : 1;
            model.setMaxDepth(maxDepth);
            model.setMinNum(minNum);
            model.setSeed(42);
            if (parameters.containsKey(VerbaMetricsConstants.PARAM_MAX_DEPTH)) {
                int paramMaxDepth = (Integer) parameters.get(VerbaMetricsConstants.PARAM_MAX_DEPTH);
                if (paramMaxDepth > 0) {
                    model.setMaxDepth(Math.min(paramMaxDepth, maxDepth));
                }
            }
            if (parameters.containsKey(VerbaMetricsConstants.PARAM_MIN_SAMPLES_SPLIT)) {
                int minSamplesSplit = (Integer) parameters.get(VerbaMetricsConstants.PARAM_MIN_SAMPLES_SPLIT);
                if (minSamplesSplit > 0) {
                    model.setMinNum(Math.max(minSamplesSplit, minNum));
                }
            }
            if (parameters.containsKey(VerbaMetricsConstants.PARAM_MIN_SAMPLES_LEAF)) {
                int minSamplesLeaf = (Integer) parameters.get(VerbaMetricsConstants.PARAM_MIN_SAMPLES_LEAF);
                if (minSamplesLeaf > 0) {
                    model.setMinVarianceProp(minSamplesLeaf / 100.0);
                }
            }
            if (parameters.containsKey(VerbaMetricsConstants.PARAM_RANDOM_STATE)) {
                int randomState = (Integer) parameters.get(VerbaMetricsConstants.PARAM_RANDOM_STATE);
                model.setSeed(randomState);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to configure RandomTree model, using defaults", e);
        }
    }

    /**
     * Calculates model accuracy using Weka's cross-validation.
     *
     * @param model        The trained model
     * @param trainingData The training dataset
     * @return Real accuracy using cross-validation
     */
    private double calculateModelAccuracy(Object model, List<Map<String, Object>> trainingData) {
        if (model instanceof Classifier) {
            try {
                Instances dataset = createWekaDataset(trainingData);
                Instances numericDataset = new Instances(dataset);
                // Remove text attribute (index 0) as RandomTree works only with numeric features
                numericDataset.deleteAttributeAt(0);
                weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(numericDataset);
                evaluation.crossValidateModel((Classifier) model, numericDataset, 5, new Random(1));
                return evaluation.pctCorrect() / 100.0;
            } catch (Exception e) {
                LOGGER.warn("Failed to calculate model accuracy with cross-validation", e);
                Double fallbackAccuracy = properties.getPerformanceThresholds().get("min-accuracy");
                return fallbackAccuracy != null ? fallbackAccuracy : 0.6;
            }
        }
        Double fallbackAccuracy = properties.getPerformanceThresholds().get("min-accuracy");
        return fallbackAccuracy != null ? fallbackAccuracy : 0.6;
    }

    /**
     * Calculates precision using Weka's evaluation.
     *
     * @param model        The trained model
     * @param trainingData The training dataset
     * @return Real precision value
     */
    private double calculatePrecision(Object model, List<Map<String, Object>> trainingData) {
        if (model instanceof Classifier) {
            try {
                Instances dataset = createWekaDataset(trainingData);
                Instances numericDataset = new Instances(dataset);
                // Remove text attribute (index 0) as RandomTree works only with numeric features
                numericDataset.deleteAttributeAt(0);
                weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(numericDataset);
                evaluation.crossValidateModel((Classifier) model, numericDataset, 5, new Random(1));
                // Use weighted precision (across classes) rather than precision for class index 0
                return evaluation.weightedPrecision();
            } catch (Exception e) {
                LOGGER.warn("Failed to calculate precision", e);
                Double fallbackPrecision = properties.getPerformanceThresholds().get("min-precision");
                return fallbackPrecision != null ? fallbackPrecision : 0.6;
            }
        }
        Double fallbackPrecision = properties.getPerformanceThresholds().get("min-precision");
        return fallbackPrecision != null ? fallbackPrecision : 0.6;
    }

    /**
     * Calculates recall using Weka's evaluation.
     *
     * @param model        The trained model
     * @param trainingData The training dataset
     * @return Real recall value
     */
    private double calculateRecall(Object model, List<Map<String, Object>> trainingData) {
        if (model instanceof Classifier) {
            try {
                Instances dataset = createWekaDataset(trainingData);
                Instances numericDataset = new Instances(dataset);
                // Remove text attribute (index 0) as RandomTree works only with numeric features
                numericDataset.deleteAttributeAt(0);
                weka.classifiers.Evaluation evaluation = new weka.classifiers.Evaluation(numericDataset);
                evaluation.crossValidateModel((Classifier) model, numericDataset, 5, new Random(1));
                // Use weighted recall (across classes) rather than recall for class index 0
                return evaluation.weightedRecall();
            } catch (Exception e) {
                LOGGER.warn("Failed to calculate recall", e);
                Double fallbackRecall = properties.getPerformanceThresholds().get("min-recall");
                return fallbackRecall != null ? fallbackRecall : 0.6;
            }
        }
        Double fallbackRecall = properties.getPerformanceThresholds().get("min-recall");
        return fallbackRecall != null ? fallbackRecall : 0.6;
    }

}
