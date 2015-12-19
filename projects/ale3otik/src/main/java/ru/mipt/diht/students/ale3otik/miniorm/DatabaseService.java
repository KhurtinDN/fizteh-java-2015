package ru.mipt.diht.students.ale3otik.miniorm;

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

/**
 * Created by alex on 15.12.15.
 */
@SuppressWarnings("Duplicates")
public class DatabaseService<T> {
    private Class<T> aClass;
    private Table annotation;
    private String table;
    private Field[] fields;
    private int primaryKeyFieldId;
    private Connection connection;
    private ArrayList<String> fieldNames;
    private static final String PROTOCOL_HEAD = "jdbc:h2:./database/miniorm";

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Table {
        String name() default "";
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface Column {
        String name() default "";
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface PrimaryKey {
    }

    private void validateClassType() throws DatabaseServiceException {

        int pkCount = 0;
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                if (!fields[i].isAnnotationPresent(Column.class)) {
                    throw new DatabaseServiceException("@PrimaryKey must have @Column annotation");
                }
                ++pkCount;
                primaryKeyFieldId = i;
            }
            if (pkCount > 1) {
                throw new DatabaseServiceException("not unique @PrimaryKey field");
            }
            if (H2DBTypeResolver.resolve(fields[i].getType()) == null) {
                throw new DatabaseServiceException(fields[i].getType().toString() + " is not allowed");
            }
        }
        if (pkCount == 0) {
            throw new DatabaseServiceException("@PrimaryKey wasn't found");
        }
    }

    private void buildFieldNames() {
        fieldNames = new ArrayList<>();
        for (Field f : fields) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                fieldNames.add(null);
            } else {
                if (column.name().length() > 0) {
                    fieldNames.add(column.name());
                } else {
                    fieldNames.add(toSnakeCase(f.getName()));
                }
            }
        }
    }

    private String toSnakeCase(String source) {
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < source.length(); ++i) {
            if (Character.isUpperCase(source.charAt(i))) {
                if (i > 0 && Character.isLowerCase(source.charAt(i - 1))) {
                    resultBuilder.append("_");
                }
                resultBuilder.append(Character.toLowerCase(source.charAt(i)));
            } else {
                resultBuilder.append(source.charAt(i));
            }
        }
        return resultBuilder.toString();
    }

    public DatabaseService(Class<T> dataClass) throws DatabaseServiceException {
        aClass = dataClass;
        annotation = aClass.getAnnotation(Table.class);
        if (annotation.name().length() > 0) {
            table = annotation.name();
        } else {
            table = toSnakeCase(dataClass.getSimpleName());
        }

        fields = aClass.getDeclaredFields();
        validateClassType();
        buildFieldNames();
        try {
            connection = DriverManager.getConnection(PROTOCOL_HEAD);
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    //    - возвращает запись по первичному ключу
    private <T> List<T> buildObjects(ResultSet rs) throws DatabaseServiceException {
        List<T> result = new LinkedList<>();

        try {
            while (rs.next()) {
                T instance = (T) aClass.newInstance();
                for (int i = 0; i < fields.length; ++i) {
                    fields[i].setAccessible(true);
                    String name = fieldNames.get(i);
                    if (name == null) {
                        continue;
                    }

                    fields[i].set(instance, rs.getObject(name));
                }
                result.add(instance);
            }
            return result;
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new DatabaseServiceException(e.getClass().toString() + " : " + e.getMessage());
        }

    }

    public final <K> T queryById(K key) throws DatabaseServiceException {
        try {
            String name = fieldNames.get(primaryKeyFieldId);
            PreparedStatement getStatement =
                    connection.prepareStatement("SELECT * FROM " + table + " WHERE " + name + " = ?");
            getStatement.setObject(1, key);
            ResultSet rs = getStatement.executeQuery();
            List<T> result = buildObjects(rs);
            if (result.size() == 0) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getClass().toString() + " : " + e.getMessage());
        }
    }

    public final List<T> queryForAll() throws DatabaseServiceException {
        try {
            PreparedStatement getStatement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = getStatement.executeQuery();

            return buildObjects(rs);
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getClass().toString() + " : " + e.getMessage());
        }
    }
    //- добавляет запись
    public final void insert(T entity) throws DatabaseServiceException {
        List<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        try {
            for (int i = 0; i < fields.length; ++i) {
                fields[i].setAccessible(true);
                String name = fieldNames.get(i);
                if (name != null) {
                    columns.add(name);
                    values.add(fields[i].get(entity));
                }
            }

            String columnsLine = columns.stream().collect(joining(", "));
            String valuesLine = values.stream()
                    .map(x -> " ? ")
                    .collect(joining(", "));

            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append("INSERT INTO ")
                    .append(table)
                    .append(" (").append(columnsLine).append(") ")
                    .append("VALUES (").append(valuesLine).append(")");
            PreparedStatement insertStatement
                    = connection.prepareStatement(requestBuilder.toString());
            for (int i = 0; i < values.size(); ++i) {
                insertStatement.setObject(i + 1, values.get(i));
            }
            insertStatement.execute();
        } catch (SQLException | IllegalAccessException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    // - редактирует запись по первичному ключу
    public final void update(T newRecord) throws DatabaseServiceException {
        try {
            if (deleteById(fields[primaryKeyFieldId].get(newRecord))) {
                insert(newRecord);
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    //    удаляет запись по первичному ключу
    public final <K> boolean deleteById(K key) throws DatabaseServiceException {
        String name = fieldNames.get(primaryKeyFieldId);
        try {
            PreparedStatement deleteStatement =
                    connection.prepareStatement("DELETE FROM " + table + " WHERE " + name + " = ?");
            deleteStatement.setObject(1, key);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public final boolean delete(T example) throws DatabaseServiceException {
        try {
            return deleteById(fields[primaryKeyFieldId].get(example));
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException(e.getClass().toString() + " " + e.getMessage());
        }
    }

    // - создаёт таблицу по метаданным класса T.
    public final void createTable() throws DatabaseServiceException {
        StringBuilder headBuilder = new StringBuilder();
        for (int i = 0; i < fields.length; ++i) {
            if (i != 0) {
                headBuilder.append(", ");
            }
            String name = fieldNames.get(i);
            if (name != null) {
                headBuilder.append(name).append(" ")
                        .append(H2DBTypeResolver.resolve(fields[i].getType()));
                if (i == primaryKeyFieldId) {
                    headBuilder.append(" NOT NULL PRIMARY KEY");
                }
            }
        }
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table
                    + "(" + headBuilder.toString() + ")");
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    // - удаляет таблицу, соответствующую T
    public final void dropTable() throws DatabaseServiceException {
        try {
            Statement createStatement = connection.createStatement();
            createStatement.execute("DROP TABLE IF EXISTS " + table);
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public final void close() throws DatabaseServiceException {
        try {
            if (connection.isClosed()) {
                return;
            }

            connection.close();
        } catch (SQLException e) {
            System.err.println("can't close connection: " + e.getMessage());
        }
    }
}
