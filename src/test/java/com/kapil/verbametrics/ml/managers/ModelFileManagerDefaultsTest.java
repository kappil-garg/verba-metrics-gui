package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ModelFileManager default configurations.
 *
 * @author Kapil Garg
 */
class ModelFileManagerDefaultsTest {

    @Test
    @DisplayName("getBasePath uses default when not configured")
    void default_base_path() {
        MLModelProperties properties = new MLModelProperties();
        ModelFileManager m = new ModelFileManager(properties);
        assertEquals("/models", m.getBasePath());
        String p = m.getModelFilePath("id");
        assertTrue(p.endsWith("id.ser"));
    }

}
