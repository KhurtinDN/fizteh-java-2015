package mini.orm.core.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project constants.
 */
public class Constants {
    public static final String DB_CONFIG_FILE = "db.config";

    /**
     * correlate Java-types with SQL-types.
     */
    public static final Map<Class<?>, String> JAVA_TO_SQL_TYPES;
    static {
        JAVA_TO_SQL_TYPES = new HashMap<>();
        JAVA_TO_SQL_TYPES.put(Boolean.class,    "BOOLEAN");
        JAVA_TO_SQL_TYPES.put(boolean.class,    "BOOLEAN");
        JAVA_TO_SQL_TYPES.put(Integer.class,    "INTEGER");
        JAVA_TO_SQL_TYPES.put(int.class,        "INTEGER");
        JAVA_TO_SQL_TYPES.put(Long.class,       "BIGINT");
        JAVA_TO_SQL_TYPES.put(long.class,       "BIGINT");
        JAVA_TO_SQL_TYPES.put(String.class,     "VARCHAR(256)");
        JAVA_TO_SQL_TYPES.put(Date.class,       "TIMESTAMP");
    }
}