package com.kapil.verbametrics.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for type-safe casting operations.
 * Provides methods to safely cast objects to specific types.
 *
 * @author Kapil Garg
 */
public class TypeSafeCastUtil {

    private TypeSafeCastUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Safely casts an object to a Map<String, Object>.
     * If the object is not a map or contains non-string keys, returns an empty map.
     *
     * @param obj the object to be cast
     * @return the cast map or an empty map if casting is not possible
     */
    public static Map<String, Object> safeCastToMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    result.put((String) entry.getKey(), entry.getValue());
                }
            }
            return result;
        }
        return Map.of();
    }

}
