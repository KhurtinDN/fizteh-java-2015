package ru.fizteh.fivt.students.JenkaEff.MiniORM;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class H2StringsResolver {
    private static Map<Class, String> h2Map;
    static {
        h2Map = new HashMap<>();
        h2Map.put(Integer.class, "INTEGER");
        h2Map.put(Boolean.class, "BOOLEAN");
        h2Map.put(Byte.class, "TINYINT");
        h2Map.put(Short.class, "SMALLINT");
        h2Map.put(Long.class, "BIGINT");
        h2Map.put(Double.class, "DOUBLE");
        h2Map.put(Float.class, "FLOAT");
        h2Map.put(Time.class, "TIME");
        h2Map.put(Date.class, "DATE");
        h2Map.put(Timestamp.class, "TIMESTAMP");
        h2Map.put(Character.class, "CHAR");
        h2Map.put(String.class, "CLOB");
        h2Map.put(UUID.class, "UUID");
    }

    public static String resolve(Class clazz) {
        if (clazz.isArray()) {
            return "ARRAY";
        }
        if (h2Map.containsKey(clazz)) {
            return h2Map.get(clazz);
        }
        return "OTHER";
    }
}   