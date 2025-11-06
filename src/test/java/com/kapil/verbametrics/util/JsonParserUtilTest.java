package com.kapil.verbametrics.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JsonParserUtil.
 *
 * @author Kapil Garg
 */
class JsonParserUtilTest {

    @Test
    @DisplayName("parseTrainingData: supports JSONL with multiple lines")
    void parseTrainingData_jsonl() {
        String jsonl = "{" +
                "\"text\":\"hello\",\"label\":\"positive\"}" +
                "\n{" +
                "\"text\":\"bye\",\"label\":\"negative\"}";
        List<Map<String, Object>> result = JsonParserUtil.parseTrainingData(jsonl);
        assertEquals(2, result.size());
        assertEquals("hello", result.get(0).get("text"));
        assertEquals("negative", result.get(1).get("label"));
    }

    @Test
    @DisplayName("parseTrainingData: supports JSON array")
    void parseTrainingData_array() {
        String json = "[{\"text\":\"hello\",\"label\":\"positive\"},{\"text\":\"bye\",\"label\":\"negative\"}]";
        List<Map<String, Object>> result = JsonParserUtil.parseTrainingData(json);
        assertEquals(2, result.size());
        assertFalse(result.isEmpty(), "Data cannot be null or empty");
        assertEquals("positive", result.getFirst().get("label"));
    }

    @Test
    @DisplayName("parseTrainingData: supports single JSON object")
    void parseTrainingData_object() {
        String json = "{\"text\":\"only one\",\"label\":\"neutral\"}";
        List<Map<String, Object>> result = JsonParserUtil.parseTrainingData(json);
        assertEquals(1, result.size());
        assertFalse(result.isEmpty(), "Data cannot be null or empty");
        assertEquals("only one", result.getFirst().get("text"));
    }

    @Test
    @DisplayName("parseTrainingData: invalid input throws RuntimeException")
    void parseTrainingData_invalid() {
        String invalid = "not json";
        RuntimeException ex = assertThrows(RuntimeException.class, () -> JsonParserUtil.parseTrainingData(invalid));
        assertTrue(ex.getMessage().toLowerCase().contains("failed to parse"));
    }

    @Test
    @DisplayName("parsePredictionData: supports array and object; invalid throws")
    void parsePredictionData_variants() {
        String arr = "[{\"text\":\"hello\"}]";
        String obj = "{\"text\":\"hello\"}";
        String bad = "hello";
        List<Map<String, Object>> r1 = JsonParserUtil.parsePredictionData(arr);
        assertEquals(1, r1.size());
        assertFalse(r1.isEmpty(), "Data cannot be null or empty");
        assertEquals("hello", r1.getFirst().get("text"));
        List<Map<String, Object>> r2 = JsonParserUtil.parsePredictionData(obj);
        assertEquals(1, r2.size());
        assertFalse(r2.isEmpty(), "Data cannot be null or empty");
        assertEquals("hello", r2.getFirst().get("text"));
        assertThrows(RuntimeException.class, () -> JsonParserUtil.parsePredictionData(bad));
    }

}
