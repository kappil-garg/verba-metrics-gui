package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModelFileManager.
 *
 * @author Kapil Garg
 */
class ModelFileManagerTest {

    /**
     * Helper to create MLModelProperties with given base directory setting.
     *
     * @param baseDir the base directory path
     * @return configured MLModelProperties
     */
    private static MLModelProperties propsWithBase(Path baseDir) {
        MLModelProperties props = new MLModelProperties();
        Map<String, String> fileSettings = new HashMap<>();
        fileSettings.put("base-path", baseDir.toString());
        fileSettings.put("format", "ser");
        props.setFileSettings(fileSettings);
        return props;
    }

    @Test
    @DisplayName("saveModelToFile and loadModelFromFile round-trip")
    void saveAndLoad(@TempDir Path tempDir) {
        ModelFileManager manager = new ModelFileManager(propsWithBase(tempDir));
        String modelId = "test-model";
        DummyObj obj = new DummyObj("hello");
        manager.saveModelToFile(modelId, obj);
        Path path = tempDir.resolve(modelId + ".ser");
        assertTrue(Files.exists(path));
        Optional<Object> loaded = manager.loadModelFromFile(modelId);
        assertTrue(loaded.isPresent());
        assertInstanceOf(DummyObj.class, loaded.get());
        assertEquals("hello", ((DummyObj) loaded.get()).value);
    }

    @Test
    @DisplayName("loadModelFromFile returns empty when file missing")
    void loadMissing(@TempDir Path tempDir) {
        ModelFileManager manager = new ModelFileManager(propsWithBase(tempDir));
        Optional<Object> loaded = manager.loadModelFromFile("nope");
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("getModelFilePath uses configured base-path and format")
    void getModelFilePath_usesConfig(@TempDir Path tempDir) {
        ModelFileManager manager = new ModelFileManager(propsWithBase(tempDir));
        String path = manager.getModelFilePath("abc");
        assertTrue(path.endsWith("abc.ser"));
        assertTrue(path.startsWith(tempDir.toString()));
    }

    /**
     * A simple serializable dummy object for testing.
     */
    private static class DummyObj implements Serializable {
        String value;

        DummyObj(String v) {
            this.value = v;
        }
    }

}
