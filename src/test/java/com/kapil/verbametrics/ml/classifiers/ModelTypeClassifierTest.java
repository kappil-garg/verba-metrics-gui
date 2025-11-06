package com.kapil.verbametrics.ml.classifiers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModelTypeClassifier.
 *
 * @author Kapil Garg
 */
class ModelTypeClassifierTest {

    /**
     * Helper to create MLModelProperties for testing.
     *
     * @return configured MLModelProperties
     */
    private static MLModelProperties props() {
        MLModelProperties p = new MLModelProperties();
        p.setSupportedModelTypes(List.of("SENTIMENT", "CLASSIFICATION"));
        Map<String, List<String>> req = new HashMap<>();
        req.put("SENTIMENT", List.of("text", "label"));
        req.put("CLASSIFICATION", List.of("text", "label"));
        p.setRequiredFields(req);
        return p;
    }

    @Test
    @DisplayName("isValidModelType checks case-insensitively")
    void isValidModelType() {
        ModelTypeClassifier c = new ModelTypeClassifier(props());
        assertTrue(c.isValidModelType("sentiment"));
        assertFalse(c.isValidModelType("topic_modeling"));
        assertFalse(c.isValidModelType(null));
    }

    @Test
    @DisplayName("getRequiredFields returns configured list or empty")
    void getRequiredFields() {
        ModelTypeClassifier c = new ModelTypeClassifier(props());
        assertEquals(List.of("text", "label"), c.getRequiredFields("SENTIMENT"));
        assertTrue(c.getRequiredFields("unknown").isEmpty());
    }

}
