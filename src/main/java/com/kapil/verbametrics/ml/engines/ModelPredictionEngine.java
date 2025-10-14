package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.managers.ModelFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Engine for model prediction operations.
 * Handles the core logic for making predictions using trained models.
 *
 * @author Kapil Garg
 */
@Component
public class ModelPredictionEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelPredictionEngine.class);

    private final ModelFileManager fileManager;

    @Autowired
    public ModelPredictionEngine(ModelFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Makes predictions using a trained model.
     *
     * @param modelId the ID of the trained model
     * @param input   the input data for prediction
     * @return prediction result with confidence scores
     */
    public Map<String, Object> predict(String modelId, Map<String, Object> input) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");
        LOGGER.debug("Making prediction with model: {}", modelId);
        try {
            Object model = fileManager.loadModelFromFile(modelId)
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
            Map<String, Object> prediction = performPrediction(model, input);
            LOGGER.debug("Prediction completed for model: {}", modelId);
            return prediction;
        } catch (Exception e) {
            LOGGER.error("Failed to make prediction with model: {}", modelId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", true);
            errorResult.put("message", "Prediction failed: " + e.getMessage());
            errorResult.put("modelId", modelId);
            return errorResult;
        }
    }

    /**
     * Performs prediction based on the model type.
     *
     * @param model The trained model
     * @param input The input data for prediction
     * @return Prediction result with confidence scores
     * @throws Exception if prediction fails
     */
    private Map<String, Object> performPrediction(Object model, Map<String, Object> input) throws Exception {
        if (model instanceof Classifier) {
            return predictWithWekaModel((Classifier) model, input);
        } else {
            throw new IllegalArgumentException("Unsupported model type: " + model.getClass().getSimpleName());
        }
    }

    /**
     * Makes prediction using a Weka Classifier model based on input data.
     *
     * @param model the Weka Classifier model
     * @param input the input data for prediction
     * @return prediction result with confidence scores
     * @throws Exception if prediction fails
     */
    private Map<String, Object> predictWithWekaModel(Classifier model, Map<String, Object> input) throws Exception {
        Instances dataset = createWekaInstance(input);
        weka.core.Instance instance = dataset.instance(0);
        double prediction = model.classifyInstance(instance);
        double[] distribution = model.distributionForInstance(instance);
        Map<String, Object> result = new HashMap<>();
        result.put("prediction", (int) prediction);
        result.put("confidence", distribution[(int) prediction]);
        result.put("probabilities", distribution);
        result.put("modelType", model.getClass().getSimpleName());
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * Creates a Weka Instances object from input data for prediction.
     *
     * @param input the input data
     * @return Weka Instances object
     */
    private Instances createWekaInstance(Map<String, Object> input) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("text", (ArrayList<String>) null));
        attributes.add(new Attribute("label"));
        Instances dataset = new Instances("PredictionDataset", attributes, 1);
        dataset.setClassIndex(1);
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(0, (String) input.get("text"));
        instance.setValue(1, 0);
        dataset.add(instance);
        return dataset;
    }

}
