package com.template.lib.cucumber;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by sbt-velichko-aa on 12.07.17.
 */
@Slf4j
public class I18N {
    public static final String DEFAULT_BUNDLE_PATH = "i18n";
    private static final Map<String, I18N> BUNDLE_STORAGE = new HashMap<>();
    private static final String BUNDLE_ENCODING = "UTF-8";
    private final Properties properties = new Properties();
    private String bundleFile;

    /**
     * Gets bundle with default system locale and com as parent path
     *
     * @param callerClass Class resource for
     * @return Resources for given class
     */
    public static final I18N getI18n(Class callerClass) {
        return getI18n(callerClass, Locale.getDefault(), DEFAULT_BUNDLE_PATH);
    }

    /**
     * Gets bundle with given locale and com as parent path
     *
     * @param callerClass Class resource for
     * @param locale      Resources locale
     * @return Resources for given class
     */
    public static final I18N getI18n(Class callerClass, Locale locale) {
        return getI18n(callerClass, locale, DEFAULT_BUNDLE_PATH);
    }

    /**
     * Gets bundle with default system locale and given parent path
     *
     * @param callerClass Class resource for
     * @param bundlePath  Resources parent path
     * @return Resources for given class
     */
    public static final I18N getI18n(Class callerClass, String bundlePath) {
        return getI18n(callerClass, Locale.getDefault(), bundlePath);
    }

    /**
     * Gets bundle with given locale and parent path
     *
     * @param callerClass Class resource for
     * @param locale      Resources locale
     * @param bundlePath  Resources parent path
     * @return Resources for given class
     */
    public static final I18N getI18n(Class callerClass, Locale locale, String bundlePath) {
        String className = callerClass.getSimpleName();
        String s = "/";
        String classPath = callerClass.getPackage().getName().replaceAll("\\.", s);
        String resourceFile = bundlePath + s + classPath + s + className + s
                + locale.getLanguage() + ".properties";
        log.info("Loading com bundle from {}", resourceFile);
        if (BUNDLE_STORAGE.get(resourceFile) == null) {
            I18N bundle = new I18N();
            bundle.bundleFile = resourceFile;
            try (InputStream streamFromResources = I18N.class.getClassLoader().getResourceAsStream(resourceFile)) {
                InputStreamReader isr = new InputStreamReader(streamFromResources, BUNDLE_ENCODING);
                bundle.properties.load(isr);
            } catch (IOException | NullPointerException e) {
                throw new I18NRuntimeException("Failed to access bundle properties file", e);
            }
            synchronized (BUNDLE_STORAGE) {
                BUNDLE_STORAGE.put(resourceFile, bundle);
            }
        }
        return BUNDLE_STORAGE.get(resourceFile);
    }

    /**
     * Get translation by key
     *
     * @param key translation key
     * @return Translation or key if no translation found
     */
    public String get(String key) {
        String translation = properties.getProperty(key);
        if (translation == null) {
            log.debug("There is no \"{}\" key in \"{}\" bundle. Failing back to {}", key, this.bundleFile, key);
            translation = key;
        }
        return translation;
    }

    /**
     * Swap keys and values in bundle
     *
     * @return Reversed bundle
     */
    public I18N reverse() {
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        I18N reversed = new I18N();
        for (Map.Entry<Object, Object> entry : entries) {
            reversed.properties.put(entry.getValue(), entry.getKey());
        }
        return reversed;
    }

    /**
     * Represent bundle as map
     *
     * @return Bundle as map
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        return map;
    }
}
