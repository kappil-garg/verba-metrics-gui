package com.kapil.verbametrics.ml.mapper;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.entities.MLModelEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper service for converting between ML domain objects and JPA entities.
 * Handles the conversion for GUI application data flow.
 *
 * @author Kapil Garg
 */
@Component
public class MLModelMapper {

    /**
     * Converts MLModel domain object to MLModelEntity.
     *
     * @param model the domain model
     * @return the JPA entity
     */
    public MLModelEntity toEntity(MLModel model) {
        if (model == null) {
            return null;
        }
        return MLModelEntity.builder()
                .modelId(model.modelId())
                .modelType(model.modelType())
                .name(model.name())
                .description(model.description())
                .version(model.version())
                .createdAt(model.createdAt())
                .lastUsed(model.lastUsed())
                .modelPath(model.modelPath())
                .isActive(model.isActive())
                .createdBy(model.createdBy())
                .trainingDataSize(model.trainingDataSize())
                .accuracy(model.accuracy())
                .status(model.status())
                .parameters(convertToStringMap(model.parameters()))
                .performanceMetrics(convertToStringMap(model.performanceMetrics()))
                .build();
    }

    /**
     * Converts MLModelEntity to MLModel domain object.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    public MLModel toDomain(MLModelEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MLModel(
                entity.getModelId(),
                entity.getModelType(),
                entity.getName(),
                entity.getDescription(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getLastUsed(),
                convertToObjectMap(entity.getParameters()),
                convertToObjectMap(entity.getPerformanceMetrics()),
                entity.getModelPath(),
                entity.getIsActive(),
                entity.getCreatedBy(),
                entity.getTrainingDataSize(),
                entity.getAccuracy(),
                entity.getStatus()
        );
    }

    /**
     * Converts Map<String, Object> to Map<String, String> for JPA storage.
     *
     * @param objectMap the object map
     * @return the string map
     */
    private Map<String, String> convertToStringMap(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return new HashMap<>();
        }
        Map<String, String> stringMap = new HashMap<>();
        objectMap.forEach((key, value) -> {
            if (value != null) {
                stringMap.put(key, value.toString());
            }
        });
        return stringMap;
    }

    /**
     * Converts Map<String, String> to Map<String, Object> for domain objects.
     *
     * @param stringMap the string map
     * @return the object map
     */
    private Map<String, Object> convertToObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) {
            return new HashMap<>();
        }
        Map<String, Object> objectMap = new HashMap<>();
        stringMap.forEach((key, value) -> {
            if (value != null) {
                // Try to parse as number, otherwise keep as string
                try {
                    if (value.contains(".")) {
                        objectMap.put(key, Double.parseDouble(value));
                    } else {
                        objectMap.put(key, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    objectMap.put(key, value);
                }
            }
        });
        return objectMap;
    }

    /**
     * Updates entity with domain model data.
     *
     * @param entity the entity to update
     * @param model  the domain model with new data
     * @return the updated entity
     */
    public MLModelEntity updateEntity(MLModelEntity entity, MLModel model) {
        if (entity == null || model == null) {
            return entity;
        }
        entity.setModelType(model.modelType());
        entity.setName(model.name());
        entity.setDescription(model.description());
        entity.setVersion(model.version());
        entity.setLastUsed(model.lastUsed());
        entity.setModelPath(model.modelPath());
        entity.setIsActive(model.isActive());
        entity.setCreatedBy(model.createdBy());
        entity.setTrainingDataSize(model.trainingDataSize());
        entity.setAccuracy(model.accuracy());
        entity.setStatus(model.status());
        entity.setParameters(convertToStringMap(model.parameters()));
        entity.setPerformanceMetrics(convertToStringMap(model.performanceMetrics()));
        return entity;
    }

}
