package ru.mipt.diht.students.pitovsky.miniorm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableService<T> {
    private Class<T> tableClass;
    private String tableName;
    private Connection dbConnection;
    private List<TypedColumn> columns;

    private final class TypedColumn {
        private String type;
        private String name;
        private Field field;

        private TypedColumn(Field realField) throws DatabaseServiceException {
            field = realField;
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                throw new DatabaseServiceException("column field must be annotated");
            }
            name = columnAnnotation.name();
            if (name.equals("")) {
                name = toSnakeCase(field.getName());
            }
            if (field.getType().isInstance(Short.valueOf((short) 1)) || field.getType().isInstance(Integer.valueOf(1))
                    || field.getType().isInstance(Long.valueOf(1L))
                    || field.getType().isInstance(Byte.valueOf((byte) 1))) {
                type = "INT";
            }  else if (field.getType().isInstance(Float.valueOf(1.0F))
                    || field.getType().isInstance(Double.valueOf(1.0D))) {
                type = "DOUBLE";
            } else if (field.getType().isInstance(String.valueOf("str"))) {
                type = "TEXT";
            } else {
                throw new DatabaseServiceException("column must be one of supported types, but not "
                                  + field.getType().getSimpleName());
            }
        }
    }

    private static String toSnakeCase(String camelCase) {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < camelCase.length(); ++i) {
            if (camelCase.charAt(i) >= 'A' && camelCase.charAt(i) <= 'Z') {
                if (i > 0 && camelCase.charAt(i - 1) >= 'a' && camelCase.charAt(i - 1) <= 'z') {
                    nameBuilder.append('_');
                }
                nameBuilder.append((camelCase.charAt(i) - 'A') + 'a');
            } else {
                nameBuilder.append(camelCase.charAt(i));
            }
        }
        return nameBuilder.toString();
    }

    public DatabaseTableService(Class<T> annotatedTableClass) throws DatabaseServiceException, SQLException {
        tableClass = annotatedTableClass;
        Table tableAnnotation = tableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new DatabaseServiceException("table class must be annotated");
        }
        tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = toSnakeCase(tableClass.getSimpleName());
        }
        Field[] allFields = tableClass.getDeclaredFields();
        columns = new ArrayList<>();
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(new TypedColumn(field));
            }
        }
        dbConnection = DriverManager.getConnection("jdbc:h2:~/pitovskydb", "sa", "");
    }

    public final void createTable() {
        StringBuilder columnsString = new StringBuilder();
        for (TypedColumn column : columns) {
            columnsString.append(column.name)
                .append(' ')
                .append(column.type);
            if (column.field.isAnnotationPresent(PrimaryKey.class)) {
                columnsString.append(" PRIMARY KEY");
            }
            columnsString.append(',');
        }
        columnsString.deleteCharAt(columnsString.length() - 1); //remove last ','
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tableName
                    + "(" + columnsString.toString() + ")";
            System.err.println("try to execute: " + query);
            dbConnection.createStatement().execute(query);
        } catch (SQLException e) {
            System.err.println("bad query: " + e.getMessage());
        }
    }
}
