package com.kapil.verbametrics.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TypeSafeCastUtil.
 *
 * @author Kapil Garg
 */
class TypeSafeCastUtilTest {

    @Test
    @DisplayName("safeCastToMap returns map for valid Map<String, Object>")
    void safeCastToMap_validMap() {
        Map<String, Object> input = Map.of("key1", "value1", "key2", 42);
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals(42, result.get("key2"));
    }

    @Test
    @DisplayName("safeCastToMap handles empty map")
    void safeCastToMap_emptyMap() {
        Map<String, Object> input = Map.of();
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("safeCastToMap filters non-string keys")
    void safeCastToMap_nonStringKeys() {
        Map<Object, Object> input = new HashMap<>();
        input.put("validKey", "value1");
        input.put(123, "value2");
        input.put(true, "value3");
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(1, result.size());
        assertEquals("value1", result.get("validKey"));
        assertNull(result.get(123));
        assertNull(result.get(true));
    }

    @Test
    @DisplayName("safeCastToMap returns empty map for null")
    void safeCastToMap_null() {
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("safeCastToMap returns empty map for non-map object")
    void safeCastToMap_nonMapObject() {
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap("not a map");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("safeCastToMap returns empty map for number")
    void safeCastToMap_numberObject() {
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(42);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("safeCastToMap handles map with null values")
    void safeCastToMap_nullValues() {
        Map<String, Object> input = new HashMap<>();
        input.put("key1", null);
        input.put("key2", "value");
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(2, result.size());
        assertNull(result.get("key1"));
        assertEquals("value", result.get("key2"));
    }

    @Test
    @DisplayName("safeCastToMap handles nested maps")
    void safeCastToMap_nestedMaps() {
        Map<String, Object> nested = Map.of("nested", "value");
        Map<String, Object> input = Map.of("outer", nested);
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(1, result.size());
        assertEquals(nested, result.get("outer"));
    }

    @Test
    @DisplayName("safeCastToMap handles complex objects as values")
    void safeCastToMap_complexValues() {
        class CustomObject {
        }
        CustomObject obj = new CustomObject();
        Map<String, Object> input = Map.of("key", obj);
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(1, result.size());
        assertEquals(obj, result.get("key"));
    }

    @Test
    @DisplayName("safeCastToMap returns mutable map")
    void safeCastToMap_mutableResult() {
        Map<String, Object> input = Map.of("key1", "value1");
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        // Should be able to add to result
        assertDoesNotThrow(() -> result.put("key2", "value2"));
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("safeCastToMap handles large maps")
    void safeCastToMap_largeMap() {
        Map<String, Object> input = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            input.put("key" + i, "value" + i);
        }
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(1000, result.size());
        assertEquals("value500", result.get("key500"));
    }

    @Test
    @DisplayName("safeCastToMap preserves map with mixed value types")
    void safeCastToMap_mixedValueTypes() {
        Map<String, Object> input = new HashMap<>();
        input.put("string", "text");
        input.put("number", 42);
        input.put("double", 3.14);
        input.put("boolean", true);
        input.put("null", null);
        Map<String, Object> result = TypeSafeCastUtil.safeCastToMap(input);
        assertEquals(5, result.size());
        assertEquals("text", result.get("string"));
        assertEquals(42, result.get("number"));
        assertEquals(3.14, result.get("double"));
        assertEquals(true, result.get("boolean"));
        assertNull(result.get("null"));
    }

    @Test
    @DisplayName("Private constructor cannot be instantiated")
    void privateConstructor() throws Exception {
        var constructor = TypeSafeCastUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "Constructor should be private");
    }

}
