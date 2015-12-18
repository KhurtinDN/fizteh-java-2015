package ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations;
import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by semenfedotov on 15.12.15.
 */
public class SQLTypeConverter {
    static Map<Class, String> classSQLEquivalents = new HashMap<>();
    static {
        classSQLEquivalents.put(String.class, "VARCHAR(20)");
        classSQLEquivalents.put(java.math.BigDecimal.class, "NUMERIC");
        classSQLEquivalents.put(boolean.class, "BOOLEAN");
        classSQLEquivalents.put(byte.class, "TINYINT");
        classSQLEquivalents.put(short.class, "SHORTINT");
        classSQLEquivalents.put(int.class, "INTEGER");
        classSQLEquivalents.put(Integer.class, "INTEGER");
        classSQLEquivalents.put(long.class, "BIGINT");
        classSQLEquivalents.put(Long.class, "BIGINT");
        classSQLEquivalents.put(float.class, "REAL");
        classSQLEquivalents.put(double.class, "DOUBLE");
        classSQLEquivalents.put(Double.class, "DOUBLE");
        classSQLEquivalents.put(Date.class, "DATE");
        classSQLEquivalents.put(Time.class, "TIME");
        classSQLEquivalents.put(Timestamp.class, "TIMESTAMP");
        classSQLEquivalents.put(Clob.class, "CLOB");
        classSQLEquivalents.put(Blob.class, "BLOB");
        classSQLEquivalents.put(Array.class, "ARRAY");
    }

    public static String convertToSQLType(Class toConvert) {
        if (classSQLEquivalents.containsKey(toConvert)) {
            return classSQLEquivalents.get(toConvert);
        }
        else {
            return "SQL doesn't support your Class";
        }

    }
}


