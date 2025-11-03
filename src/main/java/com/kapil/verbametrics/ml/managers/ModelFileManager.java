package com.kapil.verbametrics.ml.managers;

import com.kapil.verbametrics.ml.config.MLModelProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Manager for handling model file operations such as saving and loading up model files.
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
     */
    public void saveModelToFile(String modelId, Object model) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        Objects.requireNonNull(model, "Model cannot be null");
        try {
            String filePath = getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
                oos.writeObject(model);
            }
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
    public Optional<Object> loadModelFromFile(String modelId) {
        Objects.requireNonNull(modelId, "Model ID cannot be null");
        try {
            String filePath = getModelFilePath(modelId);
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                LOGGER.debug("Model file not found for model: {}", modelId);
                return Optional.empty();
            }
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                Object model = ois.readObject();
                return Optional.of(model);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load model from file: {}", modelId, e);
            return Optional.empty();
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
        String format = properties.getFileSettings().getOrDefault("format", "ser");
        String fileName = modelId + "." + format;
        return Paths.get(basePath, fileName).toString();
    }

    /**
     * Gets the base path for model files.
     *
     * @return the base path
     */
    public String getBasePath() {
        return properties.getFileSettings().getOrDefault("base-path", "/models");
    }

}
