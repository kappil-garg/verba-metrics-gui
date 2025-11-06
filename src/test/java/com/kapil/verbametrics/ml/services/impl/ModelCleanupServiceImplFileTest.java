package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.ml.services.MLModelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelCleanupServiceImpl file cleanup.
 *
 * @author Kapil Garg
 */
class ModelCleanupServiceImplFileTest {

    @Test
    @DisplayName("cleanupModelFile deletes existing file and returns true; false otherwise")
    void cleanupModelFile_behaviour(@TempDir Path tempDir) throws Exception {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        MLModelService modelService = Mockito.mock(MLModelService.class);
        String modelId = "m-file";
        Path file = tempDir.resolve(modelId + ".ser");
        Files.writeString(file, "x");
        when(fileManager.getModelFilePath(modelId)).thenReturn(file.toString());
        ModelCleanupServiceImpl svc = new ModelCleanupServiceImpl(fileManager, modelService);
        assertTrue(svc.cleanupModelFile(modelId));
        assertFalse(Files.exists(file));
        assertFalse(svc.cleanupModelFile(modelId));
    }

}
