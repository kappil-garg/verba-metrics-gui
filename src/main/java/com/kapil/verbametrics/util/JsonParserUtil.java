package com.kapil.verbametrics.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing JSON data using Jackson library.
 * Provides robust JSON parsing capabilities for the application.
 *
 * @author Kapil Garg
 */
@Component
public class JsonParserUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParserUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Parse training data from JSON string.
     * Handles JSON array format with text, label, and features fields.
     *
     * @param trainingDataJson the training data as JSON string
     * @return parsed training data
     */
    public static List<Map<String, Object>> parseTrainingData(String trainingDataJson) {
        try {
            return OBJECT_MAPPER.readValue(trainingDataJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            LOGGER.error("Failed to parse training data JSON", e);
            throw new RuntimeException("Failed to parse training data: " + e.getMessage(), e);
        }
    }

    /**
     * Parse prediction data from JSON string.
     * Handles both single JSON object and JSON array formats.
     *
     * @param predictionDataJson the prediction data as JSON string
     * @return parsed prediction data as list
     */
    public static List<Map<String, Object>> parsePredictionData(String predictionDataJson) {
        try {
            String json = predictionDataJson.trim();
            if (json.startsWith("[") && json.endsWith("]")) {
                // Handle JSON array format
                return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
                });
            } else if (json.startsWith("{") && json.endsWith("}")) {
                // Handle single JSON object format
                Map<String, Object> singleObject = OBJECT_MAPPER.readValue(json, new TypeReference<>() {
                });
                return List.of(singleObject);
            } else {
                throw new IllegalArgumentException("Prediction data must be a JSON object or array");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse prediction data JSON", e);
            throw new RuntimeException("Failed to parse prediction data: " + e.getMessage(), e);
        }
    }

}
