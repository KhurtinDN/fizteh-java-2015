package mini.orm.core.model;

import mini.orm.api.Column;
import mini.orm.core.utils.NameUtils;

import java.lang.reflect.Field;

/**
 * this class describes one column of table.
 */
public class ColumnDescriptor {
    private String name;
    private Class<?> type;
    private Field columnField;

    ColumnDescriptor(String columnName, Class<?> columnType, Field columnField1) {
        this.name = columnName;
        this.type = columnType;
        this.columnField = columnField1;
        columnField.setAccessible(true);
    }

    public final String getColumnName() {
        return name;
    }

    public final Class<?> getColumnType() {
        return type;
    }

    public final Field getColumnField() {
        return columnField;
    }

    public static ColumnDescriptor fromEntityField(Field entityField) {
        if (!entityField.isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Field = " + entityField + " not annotated by @Column");
        }

        String columnName = entityField.getAnnotation(Column.class).name();
        if (columnName.isEmpty()) {
            // use default value - field name
            columnName = NameUtils.convertFromCamelCaseToUnderscore(entityField.getName());
        }
        Class<?> columnType = entityField.getType();
        return new ColumnDescriptor(columnName, columnType, entityField);
    }
}