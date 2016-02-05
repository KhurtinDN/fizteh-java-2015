package ru.mipt.diht.students.miniorm;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;

/**
 * Created by mikhail on 29.01.16.
 */
public class Column {
    public enum Type {INT, BOOLEAN, DOUBLE, TIME, DATE, VARCHAR}
    private final String name;
    private final Field field;
    private final Type type;

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public Column(String name, Field field) throws DatabaseServiceException {
        this.name = name;
        this.field = field;

        Class<?> fieldType = field.getType();
        if (fieldType == Integer.class) {
            type = Type.INT;
        } else if (fieldType == Boolean.class) {
            type = Type.BOOLEAN;
        } else if (fieldType == Double.class) {
            type = Type.DOUBLE;
        } else if (fieldType == Time.class) {
            type = Type.TIME;
        } else if (fieldType == Date.class) {
            type = Type.DATE;
        } else if (fieldType == String.class) {
            type = Type.VARCHAR;
        } else {
            throw new DatabaseServiceException("Invalid column type: " + field.getName());
        }
    }

    public String toSQL(Object object) {
        if (object == null) {
            return "NULL";
        } else if (type == Type.VARCHAR) {
            return "\'" + object.toString() + "\'";
        } else {
            return object.toString();
        }
    }

    public boolean checkIfSuits(Object object) {
        return field.getType() == object.getClass();
    }
}
