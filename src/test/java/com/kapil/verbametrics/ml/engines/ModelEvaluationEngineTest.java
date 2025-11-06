package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelEvaluationEngine.
 *
 * @author Kapil Garg
 */
class ModelEvaluationEngineTest {

    @Test
    @DisplayName("evaluateModel: returns metrics and success when model is present")
    void evaluate_success() {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        ClassValueManager classValueManager = Mockito.mock(ClassValueManager.class);
        String modelId = "m1";
        when(fileManager.loadModelFromFile(modelId)).thenReturn(Optional.of(new StubClassifier()));
        when(classValueManager.getClassValues(modelId)).thenReturn(List.of("negative", "positive"));
        ModelEvaluationEngine engine = new ModelEvaluationEngine(fileManager, classValueManager);
        List<Map<String, Object>> testData = List.of(
                Map.of("features", List.of(0.1, 0.2), "label", "negative"),
                Map.of("features", List.of(0.3, 0.4), "label", "positive")
        );
        ModelEvaluationResult result = engine.evaluateModel(modelId, testData);
        assertTrue(result.success());
        assertEquals(testData.size(), result.testDataSize());
        assertTrue(result.evaluationTimeMs() >= 0);
        assertTrue(result.accuracy() >= 0.0 && result.accuracy() <= 1.0);
        assertNotNull(result.confusionMatrix());
    }

    @Test
    @DisplayName("evaluateModel: failure when model file missing")
    void evaluate_missingModel() {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        ClassValueManager classValueManager = Mockito.mock(ClassValueManager.class);
        String modelId = "m2";
        when(fileManager.loadModelFromFile(modelId)).thenReturn(Optional.empty());
        when(classValueManager.getClassValues(modelId)).thenReturn(List.of());
        ModelEvaluationEngine engine = new ModelEvaluationEngine(fileManager, classValueManager);
        List<Map<String, Object>> testData = List.of(
                Map.of("features", List.of(0.1, 0.2), "label", "negative")
        );
        ModelEvaluationResult result = engine.evaluateModel(modelId, testData);
        assertFalse(result.success());
        assertEquals(1, result.testDataSize());
        assertNotNull(result.errorMessage());
    }

    private static class StubClassifier implements Classifier {

        @Override
        public void buildClassifier(Instances data) {
            // no-op
        }

        @Override
        public double classifyInstance(Instance instance) {
            return 0.0;
        }

        @Override
        public double[] distributionForInstance(Instance instance) {
            int n = instance.numClasses();
            double[] dist = new double[Math.max(1, n)];
            dist[0] = 1.0;
            return dist;
        }

        @Override
        public Capabilities getCapabilities() {
            return new Capabilities(null);
        }

    }

}
