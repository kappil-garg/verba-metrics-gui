package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelPredictionEngine.
 *
 * @author Kapil Garg
 */
class ModelPredictionEngineTest {

    @Test
    @DisplayName("predict: returns label and confidence with stub classifier")
    void predict_success() {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        ClassValueManager classValueManager = Mockito.mock(ClassValueManager.class);
        String modelId = "m1";
        when(fileManager.loadModelFromFile(modelId)).thenReturn(Optional.of(new StubClassifier()));
        when(classValueManager.getClassValues(modelId)).thenReturn(List.of("negative", "positive"));
        ModelPredictionEngine engine = new ModelPredictionEngine(fileManager, classValueManager);
        Map<String, Object> input = Map.of("text", "hello", "features", List.of(0.2, 0.3));
        Map<String, Object> result = engine.predict(modelId, input);
        assertEquals("positive", String.valueOf(result.get("prediction")));
        double confidence = ((Number) result.get("confidence")).doubleValue();
        assertTrue(confidence >= 0.0 && confidence <= 1.0);
    }

    private static class StubClassifier implements Classifier {

        @Override
        public void buildClassifier(Instances data) {
        }

        @Override
        public double classifyInstance(Instance instance) {
            return 1.0;
        }

        @Override
        public weka.core.Capabilities getCapabilities() {
            return new weka.core.Capabilities(null);
        }

        @Override
        public double[] distributionForInstance(Instance instance) {
            int n = Math.max(1, instance.numClasses());
            double[] dist = new double[n];
            if (n > 1) {
                dist[1] = 0.9;
                dist[0] = 0.1;
            } else {
                dist[0] = 1.0;
            }
            return dist;
        }

    }

}
