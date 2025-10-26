package com.kapil.verbametrics.ml.services.impl;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.managers.ModelFileManager;
import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.ml.services.ModelCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ModelCleanupService.
 * Handles cleanup of model files both manually and on application shutdown.
 *
 * @author Kapil Garg
 */
@Service
public class ModelCleanupServiceImpl implements ModelCleanupService, ApplicationListener<ContextClosedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCleanupServiceImpl.class);

    private final ModelFileManager fileManager;
    private final MLModelService modelService;

    @Autowired
    public ModelCleanupServiceImpl(ModelFileManager fileManager, MLModelService modelService) {
        this.fileManager = fileManager;
        this.modelService = modelService;
    }

    /**
     * Handles application shutdown event and cleans up orphaned model files.
     * Only deletes .ser files that don't have corresponding database entries.
     *
     * @param event the context closed event
     */
    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        LOGGER.info("Application is shutting down, cleaning up orphaned model files...");
        cleanupOrphanedModelFiles();
        LOGGER.info("Model cleanup completed");
    }

    /**
     * Cleans up orphaned model files (files without database entries).
     */
    @Override
    public void cleanupOrphanedModelFiles() {
        try {
            List<Path> orphanedFiles = findOrphanedModelFiles();
            if (orphanedFiles.isEmpty()) {
                LOGGER.debug("No orphaned model files found to clean up");
                return;
            }
            int deletedCount = 0;
            for (Path filePath : orphanedFiles) {
                try {
                    Files.delete(filePath);
                    deletedCount++;
                    LOGGER.debug("Deleted orphaned model file: {}", filePath.getFileName());
                } catch (Exception e) {
                    LOGGER.warn("Failed to delete orphaned model file: {}", filePath.getFileName(), e);
                }
            }
            if (deletedCount > 0) {
                LOGGER.info("Cleaned up {} orphaned model files on application exit", deletedCount);
            } else {
                LOGGER.debug("No orphaned model files found to clean up");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to cleanup orphaned model files on application exit", e);
        }
    }

    /**
     * Cleans up a specific model file by model ID.
     *
     * @param modelId the ID of the model whose file should be deleted
     * @return true if the file was successfully deleted, false otherwise
     */
    @Override
    public boolean cleanupModelFile(String modelId) {
        try {
            String filePath = fileManager.getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.debug("Deleted model file: {}", path.getFileName());
                return true;
            } else {
                LOGGER.debug("Model file does not exist: {}", modelId);
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to delete model file for model: {}", modelId, e);
            return false;
        }
    }

    /**
     * Finds all orphaned model files (files without database entries).
     * This is the common logic used by both cleanup and count methods.
     *
     * @return list of orphaned model file paths
     * @throws Exception if there's an error accessing the file system or database
     */
    private List<Path> findOrphanedModelFiles() throws Exception {
        // Get all model IDs from database
        List<String> dbModelIds = modelService.listModels().stream()
                .map(MLModel::modelId)
                .toList();
        Set<String> dbModelIdSet = Set.copyOf(dbModelIds);
        // Get all .ser files in models directory
        String basePath = fileManager.getModelFilePath("").replaceAll("/[^/]*$", ""); // Get base directory
        Path baseDir = Paths.get(basePath);
        if (!Files.exists(baseDir)) {
            LOGGER.debug("Models directory does not exist, nothing to clean up");
            return List.of();
        }
        List<Path> orphanedFiles = new ArrayList<>();
        try (var stream = Files.list(baseDir)) {
            for (Path filePath : stream.toList()) {
                if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".ser")) {
                    String fileName = filePath.getFileName().toString();
                    String modelId = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (!dbModelIdSet.contains(modelId)) {
                        orphanedFiles.add(filePath);
                    }
                }
            }
        }
        return orphanedFiles;
    }

}
