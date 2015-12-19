package ru.mipt.diht.students.elinrin.miniorm;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class
ClassConverter {
    private static Map<Class, String> classes;
    static {
        classes = new HashMap<>();
        classes.put(Integer.class, "INTEGER");
        classes.put(Boolean.class, "BOOLEAN");
        classes.put(Byte.class, "TINYINT");
        classes.put(Short.class, "SMALLINT");
        classes.put(Long.class, "BIGINT");
        classes.put(Double.class, "DOUBLE");
        classes.put(Float.class, "FLOAT");
        classes.put(Time.class, "TIME");
        classes.put(Date.class, "DATE");
        classes.put(Timestamp.class, "TIMESTAMP");
        classes.put(Character.class, "CHAR");
        classes.put(String.class, "CLOB");
        classes.put(UUID.class, "UUID");
    }

    public static String convert(final Class currClass) {
        if (classes.containsKey(currClass)) {
            return classes.get(currClass);
        }
        return "OTHER";
    }

}
