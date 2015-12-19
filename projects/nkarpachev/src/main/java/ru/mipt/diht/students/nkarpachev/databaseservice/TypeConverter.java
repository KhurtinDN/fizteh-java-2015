package ru.mipt.diht.students.nkarpachev.databaseservice;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class TypeConverter {

    private static HashMap<Class, String> h2ClassTypes;
    private static HashMap<String, String> h2PrimeTypes;

    public static void makeTypeMatch() {
        h2PrimeTypes = new HashMap<>();
        h2PrimeTypes.put("int", "INTEGER");
        h2PrimeTypes.put("boolean", "BOOLEAN");
        h2PrimeTypes.put("byte", "INTEGER");
        h2PrimeTypes.put("short", "INTEGER");
        h2PrimeTypes.put("long", "BIGINT");
        h2PrimeTypes.put("float", "FLOAT");
        h2PrimeTypes.put("double", "DOUBLE");

        h2ClassTypes = new HashMap<>();
        h2ClassTypes.put(Integer.class, "INTEGER");
        h2ClassTypes.put(Boolean.class, "BOOLEAN");
        h2ClassTypes.put(Byte.class, "TINYINT");
        h2ClassTypes.put(Short.class, "SMALLINT");
        h2ClassTypes.put(Long.class, "BIGINT");
        h2ClassTypes.put(Double.class, "DOUBLE");
        h2ClassTypes.put(Float.class, "FLOAT");
        h2ClassTypes.put(Time.class, "TIME");
        h2ClassTypes.put(Date.class, "DATE");
        h2ClassTypes.put(Timestamp.class, "TIMESTAMP");
        h2ClassTypes.put(Character.class, "CHAR");
        h2ClassTypes.put(String.class, "VARCHAR(2000)");
        h2ClassTypes.put(UUID.class, "UUID");
    }

    public static String convertType(Class<?> type) {
        if (h2PrimeTypes.containsKey(type.toString())) {
            return h2PrimeTypes.get(type.toString());
        } else if (h2ClassTypes.containsKey(type)) {
            return h2ClassTypes.get(type);
        }
        return null;
    }
}
