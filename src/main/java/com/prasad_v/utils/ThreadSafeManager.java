package com.prasad_v.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadSafeManager {
    private static final ThreadLocal<Map<String, Object>> threadLocalMap = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        threadLocalMap.get().put(key, value);
    }

    public static Object get(String key) {
        return threadLocalMap.get().get(key);
    }

    public static String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    public static Integer getInteger(String key) {
        Object value = get(key);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static void remove(String key) {
        threadLocalMap.get().remove(key);
    }

    public static void clear() {
        threadLocalMap.get().clear();
    }

    public static void removeThreadLocal() {
        threadLocalMap.remove();
    }
}
