package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import com.kapil.verbametrics.ml.domain.MLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manager for ML model file operations.
 * Handles saving, loading, and managing model files on disk.
 *
 * @author Kapil Garg
 */
@Component
public class ModelFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelFileManager.class);

    private final MLModelProperties properties;

    @Autowired
    public ModelFileManager(MLModelProperties properties) {
        this.properties = properties;
    }

    /**
     * Saves a model to file.
     *
     * @param modelId the model ID
     * @param model   the model to save
     * @return the file path where the model was saved
     */
    public String saveModelToFile(String modelId, MLModel model) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(model, "Model cannot be null");
        try {
            String basePath = properties.getFileSettings().getOrDefault("base-path", "/models");
            String format = properties.getFileSettings().getOrDefault("format", "json");
            String fileName = modelId + "." + format;
            Path filePath = Paths.get(basePath, fileName);
            Files.createDirectories(filePath.getParent());
            // TODO: Implement actual model serialization
            Files.write(filePath, ("Model: " + modelId).getBytes());
            LOGGER.debug("Model saved to file: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            LOGGER.error("Failed to save model to file: {}", modelId, e);
            throw new RuntimeException("Failed to save model to file: " + e.getMessage(), e);
        }
    }

    /**
     * Loads a model from file.
     *
     * @param modelId the model ID
     * @return the loaded model if found
     */
    public Optional<MLModel> loadModelFromFile(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        try {
            String filePath = getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                LOGGER.debug("Model file not found: {}", filePath);
                return Optional.empty();
            }
            // TODO: Implement actual model deserialization
            LOGGER.debug("Model file found: {}", filePath);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Failed to load model from file: {}", modelId, e);
            return Optional.empty();
        }
    }

    /**
     * Deletes a model file.
     *
     * @param modelId the model ID
     * @return true if the file was deleted successfully
     */
    public boolean deleteModelFile(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        try {
            String filePath = getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.debug("Model file deleted: {}", filePath);
                return true;
            }
            LOGGER.debug("Model file not found for deletion: {}", filePath);
            return false;
        } catch (IOException e) {
            LOGGER.error("Failed to delete model file: {}", modelId, e);
            return false;
        }
    }

    /**
     * Checks if a model file exists.
     *
     * @param modelId the model ID
     * @return true if the model file exists
     */
    public boolean modelFileExists(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        try {
            String filePath = getModelFilePath(modelId);
            return Files.exists(Paths.get(filePath));
        } catch (Exception e) {
            LOGGER.error("Failed to check if model file exists: {}", modelId, e);
            return false;
        }
    }

    /**
     * Gets the file path for a model.
     *
     * @param modelId the model ID
     * @return the file path
     */
    public String getModelFilePath(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        String basePath = properties.getFileSettings().getOrDefault("base-path", "/models");
        String format = properties.getFileSettings().getOrDefault("format", "json");
        String fileName = modelId + "." + format;
        return Paths.get(basePath, fileName).toString();
    }

    /**
     * Lists all model files.
     *
     * @return list of model file paths
     */
    public List<String> listModelFiles() {
        String basePath = properties.getFileSettings().getOrDefault("base-path", "/models");
        Path baseDir = Paths.get(basePath);
        if (!Files.exists(baseDir)) {
            return new ArrayList<>();
        }
        try (var stream = Files.list(baseDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            LOGGER.error("Failed to list model files", e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets model file statistics.
     *
     * @param modelId the model ID
     * @return file statistics map
     */
    public Map<String, Object> getModelFileStatistics(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Map<String, Object> stats = new HashMap<>();
        try {
            String filePath = getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                stats.put("exists", true);
                stats.put("filePath", filePath);
                stats.put("fileSize", Files.size(path));
                stats.put("lastModified", Files.getLastModifiedTime(path));
            } else {
                stats.put("exists", false);
                stats.put("filePath", filePath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get model file statistics: {}", modelId, e);
            stats.put("error", e.getMessage());
        }
        return stats;
    }

}
