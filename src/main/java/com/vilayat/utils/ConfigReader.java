package com.vilayat.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        try {
            String path = "src/main/resources/config.properties";
            FileInputStream fis = new FileInputStream(path);
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties. Check the file path.", e);
        }
    }

    public static String getBrowser() {
        return properties.getProperty("browser");
    }

    public static String getBaseUrl() {
        return properties.getProperty("baseUrl");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicitWaitSeconds"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicitWaitSeconds"));
    }
}