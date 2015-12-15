package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class SqlStatementBuilder<T> {
    private String tableName = null;
    private List<ItemColumn> columnList = null;
    private ItemColumn primaryKey = null;
    private Class itemsClass;

    public SqlStatementBuilder(String newTableName,
                               List<ItemColumn> newColumnList,
                               ItemColumn newPrimaryKey,
                               Class newItemsClass) {
        this.tableName = newTableName;
        this.columnList = newColumnList;
        this.primaryKey = newPrimaryKey;
        this.itemsClass = newItemsClass;
    }

    public final String buildCreate() {
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        List<String> columns = new ArrayList<>();
        for (ItemColumn column : columnList) {
            StringBuilder columnsBuilder = new StringBuilder();
            columnsBuilder.append(column.getName())
                    .append(" ")
                    .append(column.getType());
            if (column == primaryKey) {
                columnsBuilder.append(" NOT NULL");
            }
            columns.add(columnsBuilder.toString());
        }

        createQuery.append(columns.stream().collect(joining(", "))).append(")");
        return createQuery.toString();
    }

    public final String buildInsert(T newItem) {
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO ")
                .append(tableName)
                .append(" VALUES (");

        List<String> columns = new ArrayList<>();
        for (ItemColumn column : columnList) {
            Field field = column.getField();
            field.setAccessible(true);

            try {
                columns.add(Utils.getSqlValue(field.get(newItem)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        insertQuery.append(columns.stream().collect(joining(", "))).append(")");
        return insertQuery.toString();
    }

    public final String buildUpdate(T item) {
        StringBuilder updateStatement = new StringBuilder();
        updateStatement.append("UPDATE ")
                .append(tableName)
                .append(" SET ");

        List<String> columns = new ArrayList<>();
        for (ItemColumn column : columnList) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(column.getName())
                    .append("=");

            Field field = column.getField();
            field.setAccessible(true);
            try {
                columnBuilder.append(Utils.getSqlValue(field.get(item)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            columns.add(columnBuilder.toString());
        }

        updateStatement.append(columns.stream().collect(joining(", ")));
        try {
            updateStatement.append(" WHERE ")
                    .append(primaryKey.getName())
                    .append("=")
                    .append(Utils.getSqlValue(primaryKey.getField().get(item)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return updateStatement.toString();
    }
}
