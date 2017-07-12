package com.template.lib.support;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by sbt-velichko-aa on 02.03.2016.
 */
@Slf4j
public class Props {

    private static Props instance;
    private static Properties props;

    public synchronized static Props getInstance() {
        if (instance == null) {
            instance = new Props();
        }
        return instance;
    }

    /**
     * load properties
     */
    public Props() {
        try {
            String configFile = System.getProperty("configFile", "config/application.properties");
            log.info("Loading properties from: " + configFile);
            //first try
            InputStream in = Props.class.getClassLoader().getResourceAsStream(configFile);
            //if failed, second try
            if (in == null) {
                in = new FileInputStream(configFile);
            }
            props = new Properties();
            props.load(in);
        } catch (IOException e) {
            log.error("Failed to initialize props.", e);
        }
    }

    public String getProp (String name) {
        String val = getProps().getProperty(name, "");
        if (val.isEmpty())
            log.error(String.format("Property %s was not found on Props", name));
        return val.trim();
    }

    public static String get(String prop) {
        return Props.getInstance().getProp(prop);
    }

    public static String get (String prop, String defaultValue) {
        String val = getProps().getProperty(prop);
        if (val.isEmpty())
            return defaultValue;
        return val.trim();
    }

    public static Properties getProps() {
        return props;
    }
}
