package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.ml.services.MLModelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for ModelCleanupServiceImpl.
 *
 * @author Kapil Garg
 */
class ModelCleanupServiceImplTest {

    @Test
    @DisplayName("cleanupOrphanedModelFiles deletes only orphan .ser files")
    void cleanup_orphans(@TempDir Path tempDir) throws Exception {
        ModelFileManager fileManager = Mockito.mock(ModelFileManager.class);
        MLModelService modelService = Mockito.mock(MLModelService.class);
        when(fileManager.getBasePath()).thenReturn(tempDir.toString());
        String keepId = "keep";
        String orphanId = "orphan";
        Path keepFile = tempDir.resolve(keepId + ".ser");
        Path orphanFile = tempDir.resolve(orphanId + ".ser");
        Files.writeString(keepFile, "x");
        Files.writeString(orphanFile, "y");
        MLModel keepModel = new MLModel(
                keepId, "SENTIMENT", "n", "d", "v",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(), keepFile.toString(), true, "u", 1, 0.9, "TRAINED"
        );
        when(modelService.listModels()).thenReturn(List.of(keepModel));
        ModelCleanupServiceImpl svc = new ModelCleanupServiceImpl(fileManager, modelService);
        svc.cleanupOrphanedModelFiles();
        assertTrue(Files.exists(keepFile));
        assertFalse(Files.exists(orphanFile));
    }

}
