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

    @Retention(RUNTIME)
    @Target(FIELD)
    public @interface PrimaryKey {
    }

    DatabaseService(Class<T> inputClass) throws DatabaseException {
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

    public final <K> List<T> queryById(K key) throws DatabaseException {
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

    public final List<T> queryForAll() throws DatabaseException {
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

    public final void insert(T entry) throws DatabaseException {
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

    public final void update(T entry) throws DatabaseException {
        try {
            if (delete(fields[primaryKeyFieldId].get(entry))) {
                insert(entry);
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public final <K> boolean delete(K key) throws DatabaseException {
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

    public final void createTable() throws DatabaseException {
        StringBuilder headBuilder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i != 0) {
                headBuilder.append(", ");
            }
            String name = fieldsNames.get(i);
            if (name != null) {
                headBuilder.append(name).append(" ").append(H2Type.
                        resolveType(fields[i].getType()));
                if (i == primaryKeyFieldId) {
                    headBuilder.append(" NOT NULL PRIMARY KEY");
                }
            }
        }
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table
                    + "(" + headBuilder.toString() + ")");
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public final void dropTable() throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            connection.createStatement().execute("DROP TABLE IF EXISTS " + table);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public final String toSnakeCase(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            stringBuilder.append(input.charAt(i));
            if ((i < input.length() - 1)
                    && Character.isUpperCase(input.charAt(i + 1))) {
                stringBuilder.append("_");
            }
        }
        return stringBuilder.toString().toLowerCase();
    }

    public final void validate() throws DatabaseException {
        int primaryField = 0;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getAnnotatedType() != null) {
                if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                    if (!fields[i].isAnnotationPresent(Column.class)) {
                        throw new DatabaseException("No Column annotation to PrimaryKey field");
                    }
                    primaryField++;
                    primaryKeyFieldId = i;
                }
                if (primaryField > 1) {
                    throw new DatabaseException("More than one PrimaryKey field");
                }
                if (H2Type.resolveType(fields[i].getType()) == null) {
                    throw new DatabaseException("Type isn\'t supported");
                }
            }
        }
        if (primaryField == 0) {
            throw new DatabaseException("No PrimaryKey field");
        }
    }

    public final void buildFieldNames() {
        fieldsNames = new ArrayList<>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                fieldsNames.add(null);
            } else {
                if (column.name().length() > 0) {
                    fieldsNames.add(column.name());
                } else {
                    fieldsNames.add(toSnakeCase(field.getName()));
                }
            }
        }
    }

    public final List<T> buildRequestAnswer(ResultSet resultSet) throws DatabaseException {
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
            throw new DatabaseException("IllegalAccessException");
        } catch (InstantiationException e) {
            throw new DatabaseException("InstantiationException");
        } catch (SQLException e) {
            throw new DatabaseException("SQLException");
        }
    }

}
