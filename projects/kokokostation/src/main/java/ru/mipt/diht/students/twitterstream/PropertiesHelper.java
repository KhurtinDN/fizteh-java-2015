package ru.mipt.diht.students.twitterstream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mikhail on 19.12.15.
 */
public class PropertiesHelper {
    public static String getProperty(String file, String field) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        return properties.getProperty(field);
    }
}
