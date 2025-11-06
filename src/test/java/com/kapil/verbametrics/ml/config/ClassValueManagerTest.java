package com.kapil.verbametrics.ml.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ClassValueManager.
 *
 * @author Kapil Garg
 */
class ClassValueManagerTest {

    @Test
    @DisplayName("storeClassValues and getClassValues round-trip copy")
    void store_and_get_copy() {
        ClassValueManager m = new ClassValueManager();
        m.storeClassValues("m1", List.of("a", "b"));
        List<String> v1 = m.getClassValues("m1");
        assertEquals(List.of("a", "b"), v1);
        v1.add("c");
        List<String> v2 = m.getClassValues("m1");
        assertEquals(List.of("a", "b"), v2);
    }

    @Test
    @DisplayName("getClassValues returns empty list when not found")
    void get_empty_when_missing() {
        ClassValueManager m = new ClassValueManager();
        assertTrue(m.getClassValues("nope").isEmpty());
    }

}
