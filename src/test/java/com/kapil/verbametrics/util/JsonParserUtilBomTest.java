package com.kapil.verbametrics.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for JsonParserUtil handling BOM and zero-width characters.
 *
 * @author Kapil Garg
 */
class JsonParserUtilBomTest {

    @Test
    @DisplayName("parseTrainingData handles BOM and zero-width characters")
    void parseTrainingData_cleans_invisible_chars() {
        String bom = "\uFEFF\u200B\u200C\u200D" + "{\"text\":\"t\",\"label\":\"l\"}";
        List<Map<String, Object>> out = JsonParserUtil.parseTrainingData(bom);
        assertEquals(1, out.size());
        assertEquals("t", out.getFirst().get("text"));
        assertEquals("l", out.getFirst().get("label"));
    }

}
