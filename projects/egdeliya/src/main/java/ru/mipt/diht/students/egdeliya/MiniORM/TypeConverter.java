package ru.mipt.diht.students.egdeliya.MiniORM;

/**
 * Created by Эгделия on 19.12.2015.
 */

//todo
public class TypeConverter {

    private String sqlType;

    TypeConverter(Class sqlClass) {
        if (sqlClass == Integer.class) {
            sqlType = "INTEGER";
        } else if (sqlClass == String.class) {
            sqlType = "VARCHAR(255)";
        }
    }
    public final String toSqlType() {
        return sqlType;
    }
}
