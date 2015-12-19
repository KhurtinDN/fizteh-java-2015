package mini.orm.core.model;

import mini.orm.api.PrimaryKey;
import mini.orm.api.Table;
import mini.orm.core.utils.NameUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * create descriptor to Table
 */
public final class TableDescriptor {
    private String name;
    private ColumnDescriptor primaryKey;
    private List<ColumnDescriptor> columns;

    TableDescriptor(String tableName) {
        this.name = tableName;
        this.columns = new ArrayList<>();
    }

    void addColumn(ColumnDescriptor columnDescriptor) {
        columns.add(columnDescriptor);
    }

    void setPrimaryKey(ColumnDescriptor primaryKeyColumn) {
        this.primaryKey = primaryKeyColumn;
    }

    public String getTableName() {
        return name;
    }

    public List<ColumnDescriptor> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<ColumnDescriptor> getColumnsExceptPK() {
        List<ColumnDescriptor> columnsExceptPK = new ArrayList<>(columns);
        columnsExceptPK.remove(primaryKey);
        return columnsExceptPK;
    }

    public ColumnDescriptor getPrimaryKey() {
        return primaryKey;
    }

    public static TableDescriptor fromEntityClass(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class = " + entityClass.getSimpleName()
                    + " is not annotated by @Table");
        }
        String tableName = entityClass.getAnnotation(Table.class).name();
        if (tableName.isEmpty()) {
            // use default - class name
            tableName = NameUtils.convertFromCamelCaseToUnderscore(entityClass.getSimpleName());
        }

        TableDescriptor tableDescriptor = new TableDescriptor(tableName);
        boolean primaryKeyExists = false;
        for (Field entityField : entityClass.getDeclaredFields()) {
            ColumnDescriptor columnDescriptor = ColumnDescriptor.fromEntityField(entityField);
            tableDescriptor.addColumn(columnDescriptor);
            if (entityField.isAnnotationPresent(PrimaryKey.class)) {
                tableDescriptor.setPrimaryKey(columnDescriptor);
                primaryKeyExists = true;
            }
        }
        if (!primaryKeyExists) {
            throw new IllegalArgumentException("Entity class = " + entityClass.getSimpleName()
                    + " does not have @PrimaryKey field");
        }

        return tableDescriptor;
    }
}
