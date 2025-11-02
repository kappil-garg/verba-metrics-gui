package com.kapil.verbametrics.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
            String json = cleanJsonString(trainingDataJson);
            if (json.isEmpty()) {
                throw new IllegalArgumentException("Training data is empty");
            }
            String[] lines = json.split("\r?\n");
            boolean looksLikeJsonl = false;
            int nonEmptyLineCount = 0;
            for (String line : lines) {
                String trimmed = cleanJsonString(line);
                if (!trimmed.isEmpty()) {
                    nonEmptyLineCount++;
                    try {
                        // Try to parse the line as a JSON object to validate
                        OBJECT_MAPPER.readTree(trimmed);
                        looksLikeJsonl = trimmed.startsWith("{") && trimmed.endsWith("}");
                    } catch (Exception e) {
                        looksLikeJsonl = false;
                        break;
                    }
                }
            }
            if (looksLikeJsonl && nonEmptyLineCount >= 2) {
                ArrayList<Map<String, Object>> list = new ArrayList<>();
                for (String line : lines) {
                    String trimmed = cleanJsonString(line);
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    Map<String, Object> obj = OBJECT_MAPPER.readValue(trimmed, new TypeReference<>() {
                    });
                    list.add(obj);
                }
                if (!list.isEmpty()) {
                    return list;
                }
            }
            if (json.startsWith("[") && json.endsWith("]")) {
                String normalized = json
                        .replaceAll("}\\s*[\\r\\n]+\\s*\\{", "},{")
                        .replaceAll(",\\s*]", "]")
                        .replaceAll("\\[\\s*,", "[");
                return OBJECT_MAPPER.readValue(normalized, new TypeReference<>() {
                });
            }
            if (json.startsWith("{") && json.endsWith("}")) {
                Map<String, Object> single = OBJECT_MAPPER.readValue(json, new TypeReference<>() {
                });
                return List.of(single);
            }
            String joined = json
                    .replaceAll("}\\n+\\s*\\{", "},{")
                    .replaceAll("}\\r+\\s*\\{", "},{");
            if (!joined.equals(json)) {
                String asArray = "[" + joined + "]";
                return OBJECT_MAPPER.readValue(asArray, new TypeReference<>() {
                });
            }
            throw new IllegalArgumentException("Training data must be a JSON array, object, or JSONL (one JSON object per line)");
        } catch (Exception e) {
            LOGGER.error("Failed to parse training data JSON", e);
            throw new RuntimeException("Failed to parse training data: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans a JSON string by removing Unicode characters that can cause parsing issues.
     * Removes BOM (Byte Order Mark), zero-width spaces, and other invisible Unicode characters.
     *
     * @param input the input string to clean
     * @return the cleaned and trimmed string
     */
    private static String cleanJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u200C", "")
                .replace("\u200D", "")
                .trim();
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
