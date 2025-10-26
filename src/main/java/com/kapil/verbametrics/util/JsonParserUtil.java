package com.kapil.verbametrics.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing JSON data without external dependencies.
 * Provides simple JSON parsing capabilities for the application.
 *
 * @author Kapil Garg
 */
public class JsonParserUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParserUtil.class);

    /**
     * Parse training data from JSON string.
     * Handles JSON array format with text, label, and features fields.
     *
     * @param trainingDataJson the training data as JSON string
     * @return parsed training data
     */
    public static List<Map<String, Object>> parseTrainingData(String trainingDataJson) {
        try {
            String json = trainingDataJson.trim();
            if (!json.startsWith("[") || !json.endsWith("]")) {
                throw new IllegalArgumentException("Training data must be a JSON array");
            }
            List<Map<String, Object>> trainingData = new ArrayList<>();
            String content = json.substring(1, json.length() - 1).trim();
            if (content.isEmpty()) {
                return trainingData;
            }
            String[] objects = extractJsonObjects(content);
            for (String obj : objects) {
                Map<String, Object> dataPoint = parseJsonObject(obj.trim());
                trainingData.add(dataPoint);
            }
            return trainingData;
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
            List<Map<String, Object>> predictionData = new ArrayList<>();
            if (json.startsWith("[") && json.endsWith("]")) {
                // Handle JSON array format
                String content = json.substring(1, json.length() - 1).trim();
                if (!content.isEmpty()) {
                    String[] objects = extractJsonObjects(content);
                    for (String obj : objects) {
                        Map<String, Object> dataPoint = parseJsonObject(obj.trim());
                        predictionData.add(dataPoint);
                    }
                }
            } else if (json.startsWith("{") && json.endsWith("}")) {
                // Handle single JSON object format
                Map<String, Object> dataPoint = parseJsonObject(json);
                predictionData.add(dataPoint);
            } else {
                throw new IllegalArgumentException("Prediction data must be a JSON object or array");
            }
            return predictionData;
        } catch (Exception e) {
            LOGGER.error("Failed to parse prediction data JSON", e);
            throw new RuntimeException("Failed to parse prediction data: " + e.getMessage(), e);
        }
    }

    /**
     * Split JSON array content into individual objects.
     *
     * @param content the JSON array content
     * @return array of JSON object strings
     */
    private static String[] extractJsonObjects(String content) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int start = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    objects.add(content.substring(start, i + 1));
                    while (i + 1 < content.length() && (content.charAt(i + 1) == ',' || Character.isWhitespace(content.charAt(i + 1)))) {
                        i++;
                    }
                    start = i + 1;
                }
            }
        }
        return objects.toArray(new String[0]);
    }

    /**
     * Parse a single JSON object into Map.
     *
     * @param json the JSON object string
     * @return parsed Map of key-value pairs
     */
    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new HashMap<>();
        String content = json.substring(1, json.length() - 1).trim();
        String[] pairs = extractKeyValuePairs(content);
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) continue;
            String key = parseString(keyValue[0].trim());
            String value = keyValue[1].trim();
            if (key.equals("features") && value.startsWith("[")) {
                result.put(key, parseDoubleArray(value));
            } else if (value.startsWith("\"") && value.endsWith("\"")) {
                result.put(key, parseString(value));
            } else {
                result.put(key, parseNumber(value));
            }
        }
        return result;
    }

    /**
     * Split JSON object content into key-value pairs.
     *
     * @param content the JSON object content
     * @return array of key-value pair strings
     */
    private static String[] extractKeyValuePairs(String content) {
        List<String> pairs = new ArrayList<>();
        int braceCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;
        boolean escaped = false;
        int start = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (!inQuotes) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                } else if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                } else if (c == ',' && braceCount == 0 && bracketCount == 0) {
                    pairs.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }
        if (start < content.length()) {
            pairs.add(content.substring(start).trim());
        }
        return pairs.toArray(new String[0]);
    }

    /**
     * Parse string value from JSON.
     *
     * @param json the JSON string value
     * @return parsed string
     */
    private static String parseString(String json) {
        if (json.startsWith("\"") && json.endsWith("\"")) {
            return json.substring(1, json.length() - 1);
        }
        return json;
    }

    /**
     * Parse double array from JSON.
     *
     * @param json the JSON array string
     * @return parsed double array
     */
    private static double[] parseDoubleArray(String json) {
        String content = json.substring(1, json.length() - 1).trim();
        if (content.isEmpty()) {
            return new double[0];
        }
        String[] values = content.split(",");
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Double.parseDouble(values[i].trim());
        }
        return result;
    }

    /**
     * Parse number value from JSON.
     *
     * @param json the JSON number value
     * @return parsed number (Integer or Double) or original string if not a number
     */
    private static Object parseNumber(String json) {
        try {
            if (json.contains(".")) {
                return Double.parseDouble(json);
            } else {
                return Integer.parseInt(json);
            }
        } catch (NumberFormatException e) {
            return json;
        }
    }

}
