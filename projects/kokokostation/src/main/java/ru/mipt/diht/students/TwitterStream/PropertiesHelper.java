package ru.mipt.diht.students.TwitterStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mikhail on 19.12.15.
 */
class PropertiesHelper {
    public static String getProperty(String file, String field) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            System.err.println("PropertiesHelper can't read file(" + file + "), field(" + field + "): "
                    + e.getMessage());
            System.exit(1);
        }
        return properties.getProperty(field);
    }
}
