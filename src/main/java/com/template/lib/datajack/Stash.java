package com.template.lib.datajack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nestor on 11.07.2017.
 */
public class Stash {
    private static final Map<String, Object> VAULT = new HashMap<>();

    private Stash() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Gets stash as map
     *
     * @return stash as a {@link java.util.Map} object
     */
    public static Map<String, Object> asMap() {
        return VAULT;
    }

    /**
     * Puts value in stash
     *
     * @param key the key as a {@link java.lang.String} object
     * @param value Any object
     */
    public static void put(String key, Object value) {
        VAULT.put(key, value);
    }

    /**
     * Gets a stash value by key
     *
     * @param <T> the type to return
     * @param key the key as a {@link java.lang.String} object
     * @return an object found by specified key
     */
    public static <T> T getValue(String key) {
        return (T) VAULT.get(key);
    }

    /**
     * Removes value from stash
     *
     * @param <T> the type to return
     * @param key the key as a {@link java.lang.String} object
     * @return an object removed by specified key
     */
    public static <T> T remove(String key) {
        return (T) VAULT.remove(key);
    }

    /**
     * Clear stash
     *
     */
    public static void clear() {
        VAULT.clear();
    }
}
