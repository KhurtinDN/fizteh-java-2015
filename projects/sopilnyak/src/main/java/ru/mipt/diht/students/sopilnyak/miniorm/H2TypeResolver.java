package ru.mipt.diht.students.sopilnyak.miniorm;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class H2TypeResolver {

    private static HashMap<Class, String> h2dbTypes;
    private static HashMap<String, String> h2dbPrimeTypes;

    static {
        h2dbPrimeTypes = new HashMap<>();
        h2dbPrimeTypes.put("int", "INTEGER");
        h2dbPrimeTypes.put("boolean", "BOOLEAN");
        h2dbPrimeTypes.put("byte", "INTEGER");
        h2dbPrimeTypes.put("short", "INTEGER");
        h2dbPrimeTypes.put("long", "BIGINT");
        h2dbPrimeTypes.put("float", "FLOAT");
        h2dbPrimeTypes.put("double", "DOUBLE");

        h2dbTypes = new HashMap<>();
        h2dbTypes.put(Integer.class, "INTEGER");
        h2dbTypes.put(Boolean.class, "BOOLEAN");
        h2dbTypes.put(Byte.class, "TINYINT");
        h2dbTypes.put(Short.class, "SMALLINT");
        h2dbTypes.put(Long.class, "BIGINT");
        h2dbTypes.put(Double.class, "DOUBLE");
        h2dbTypes.put(Float.class, "FLOAT");
        h2dbTypes.put(Time.class, "TIME");
        h2dbTypes.put(Date.class, "DATE");
        h2dbTypes.put(Timestamp.class, "TIMESTAMP");
        h2dbTypes.put(Character.class, "CHAR");
        h2dbTypes.put(String.class, "VARCHAR(2000)");
        h2dbTypes.put(UUID.class, "UUID");
    }

    public static String resolveType(Class<?> type) {
        if (type.isArray()) {
            return "ARRAY";
        }

        if (type.isPrimitive()) {
            if (h2dbPrimeTypes.containsKey(type.toString())) {
                return h2dbPrimeTypes.get(type.toString());
            }
            //return null;
        } else if (h2dbTypes.containsKey(type)) {
            return h2dbTypes.get(type);
        }
        return "";
    }
}
