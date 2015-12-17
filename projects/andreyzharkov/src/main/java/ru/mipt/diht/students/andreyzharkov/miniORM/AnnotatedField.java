package ru.mipt.diht.students.andreyzharkov.miniORM;

import com.google.common.base.CaseFormat;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.Column;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Андрей on 17.12.2015.
 */
@SuppressWarnings("Duplicates")
public class AnnotatedField {
    private String columnName;
    private Field field;

    private static final Map<Class, String> SQL_TYPE;

    static {
        SQL_TYPE = new HashMap<>();
        SQL_TYPE.put(Integer.class, "INT");
        SQL_TYPE.put(Long.class, "INT");
        SQL_TYPE.put(Byte.class, "INT");
        SQL_TYPE.put(Short.class, "INT");
        SQL_TYPE.put(Double.class, "DOUBLE");
        SQL_TYPE.put(Float.class, "DOUBLE");
        SQL_TYPE.put(String.class, "VARCHAR(10)");
        SQL_TYPE.put(Character.class, "VARCHAR(10)");
        SQL_TYPE.put(Integer.TYPE, "INT");
        SQL_TYPE.put(Long.TYPE, "INT");
        SQL_TYPE.put(Byte.TYPE, "INT");
        SQL_TYPE.put(Short.TYPE, "INT");
        SQL_TYPE.put(Double.TYPE, "DOUBLE");
        SQL_TYPE.put(Float.TYPE, "DOUBLE");
        SQL_TYPE.put(Character.TYPE, "VARCHAR(10)");
    }

    public final String getColumnName() {
        return columnName;
    }

    public final Field getField() {
        return field;
    }

    public final String getSqlType() throws DatabaseServiceException {
        if (SQL_TYPE.get(field.getType()) == null) {
            throw new DatabaseServiceException(columnName + " doesn't have good sql name!");
        }
        return SQL_TYPE.get(field.getType());
    }

    public AnnotatedField(Field fild) {
        this.field = fild;
        Column column = field.getAnnotation(Column.class);
        columnName = column.name();
        if (columnName.equals("")) {
            columnName = field.getName();
            columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
        }
    }
}
