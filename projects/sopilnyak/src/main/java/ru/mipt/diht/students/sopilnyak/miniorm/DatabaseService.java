package ru.mipt.diht.students.sopilnyak.miniorm;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

public class DatabaseService<T> {

    private static final String DATABASE_PATH = "jdbc:h2:./database/miniorm";

    private Class<T> databaseClass;
    private String table;
    private Field[] fields;
    private int primaryKeyFieldId;
    private ArrayList<String> fieldsNames;

    @Retention(RUNTIME)
    @Target(TYPE)
    public @interface Table {
        String name() default "";
    }

    @Retention(RUNTIME)
    @Target(FIELD)
    public @interface Column {
        String name() default "";
    }

    DatabaseService(Class<T> inputClass) {
        Table annotation;

        databaseClass = inputClass;
        annotation = inputClass.getAnnotation(Table.class);
        if (annotation.name().length() > 0) {
            table = annotation.name();
        } else {
            table = toSnakeCase(databaseClass.getSimpleName());
        }

        fields = databaseClass.getDeclaredFields();
        validate();
        buildFieldNames();
    }

    public <K> List<T> queryById(K key) throws DatabaseException {
        String name = fieldsNames.get(primaryKeyFieldId);

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("SELECT * FROM ").append(table)
                .append(" WHERE ").append(name).append(" = ?");

        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement statement = connection.prepareStatement(requestBuilder.toString());
            statement.setObject(1, key);
            ResultSet result = statement.executeQuery();
            return buildRequestAnswer(result);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public List<T> queryForAll() throws DatabaseException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ").append(table);
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement statement = connection.prepareStatement(stringBuilder.toString());
            ResultSet result = statement.executeQuery();
            return buildRequestAnswer(result);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void insert(T entry) throws DatabaseException {
        if (entry.getClass() != databaseClass) {
            throw new DatabaseException("Type of object is wrong");
        }

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                String name = fieldsNames.get(i);
                if (name != null) {
                    columns.add(name);
                    values.add(fields[i].get(entry));
                }
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }

        String columnsLine = columns.stream().collect(joining(", "));
        String valuesLine = values.stream().map(x -> " ? ").collect(joining(", "));

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("INSERT INTO ").append(table)
                .append(" (").append(columnsLine).append(") ")
                .append("VALUES (").append(valuesLine).append(")");

        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement insertStatement = connection.prepareStatement(requestBuilder.toString());
            for (int i = 0; i < values.size(); i++) {
                insertStatement.setObject(i + 1, values.get(i));
            }
            insertStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void update(T entry) throws DatabaseException {
        try {
            if (delete(fields[primaryKeyFieldId].get(entry))) {
                insert(entry);
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public <K> boolean delete (K key) throws DatabaseException {
        String name = fieldsNames.get(primaryKeyFieldId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ").append(table)
                .append(" WHERE ").append(name).append(" = ?");

        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement deleteStatement = connection.prepareStatement(stringBuilder.toString());
            deleteStatement.setObject(1, key);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void createTable() {

    }

    public String toSnakeCase(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            stringBuilder.append(input.charAt(i));
            if (Character.isUpperCase(input.charAt(i + 1))
                    && i < input.length() - 1) {
                stringBuilder.append("_");
            }
        }
        return stringBuilder.toString().toLowerCase();
    }

    public void validate() {
        // validate
    }

    public void buildFieldNames() {
        fieldsNames = new ArrayList<>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                fieldsNames.add(null);
            }
            else {
                if (column.name().length() > 0) {
                    fieldsNames.add(column.name());
                } else {
                    fieldsNames.add(toSnakeCase(field.getName()));
                }
            }
        }
    }

    public List<T> buildRequestAnswer(ResultSet resultSet) throws DatabaseException {
        try {

            List<T> result = new LinkedList<>();

            while (resultSet.next()) {
                T entry = databaseClass.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    String name = fieldsNames.get(i);

                    if (name != null) {
                        fields[i].set(entry, resultSet.getObject(name));
                    }
                }
                result.add(entry);
            }
            return result;
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        } catch (InstantiationException e) {
            throw new DatabaseException(e.getMessage());
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}
