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
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            System.err.println("PropertiesHelper can't read file(" + file + "), field(" + field + "): "
                    + e.getMessage());
            throw e;
        }
        return properties.getProperty(field);
    }
}
