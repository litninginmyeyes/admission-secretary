package org.example.admissionsecretary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public final class Config {
    private static final Properties PROPS = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            PROPS.load(in);
        } catch (IOException e) {
        }
    }

    private Config() {}

    public static String get(String key) {
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        return env != null ? env : PROPS.getProperty(key);
    }
}
