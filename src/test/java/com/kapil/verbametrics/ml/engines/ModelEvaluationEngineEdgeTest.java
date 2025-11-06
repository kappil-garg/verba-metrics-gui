package com.kapil.verbametrics.ml.engines;

import com.kapil.verbametrics.ml.config.ClassValueManager;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Edge test cases for ModelEvaluationEngine.
 *
 * @author Kapil Garg
 */
class ModelEvaluationEngineEdgeTest {

    @Test
    @DisplayName("evaluateModel: returns failure on empty test data")
    void evaluate_emptyData() {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        ClassValueManager classValueManager = Mockito.mock(ClassValueManager.class);
        ModelEvaluationEngine engine = new ModelEvaluationEngine(fileManager, classValueManager);
        var result = engine.evaluateModel("id", List.of());
        assertFalse(result.success());
        assertNotNull(result.errorMessage());
    }

    @Test
    @DisplayName("evaluateModel: handles non-classifier model with failure result")
    void evaluate_nonClassifier() {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        ClassValueManager classValueManager = Mockito.mock(ClassValueManager.class);
        when(fileManager.loadModelFromFile("id")).thenReturn(Optional.of(new Object()));
        ModelEvaluationEngine engine = new ModelEvaluationEngine(fileManager, classValueManager);
        List<Map<String, Object>> data = List.of(Map.of("features", List.of(1.0, 2.0), "label", "x"));
        var result = engine.evaluateModel("id", data);
        assertFalse(result.success());
        assertNotNull(result.errorMessage());
    }

}
