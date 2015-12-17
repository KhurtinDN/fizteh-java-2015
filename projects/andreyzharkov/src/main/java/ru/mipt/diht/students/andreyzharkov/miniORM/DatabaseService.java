package ru.mipt.diht.students.andreyzharkov.miniORM;

import com.google.common.base.CaseFormat;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.Column;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.PrimaryKey;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 16.12.2015.
 */
@SuppressWarnings("Duplicates")
public class DatabaseService<T> {
    private Class<T> typeClass;
    private List<AnnotatedField> columns;
    private int primaryKey = -1;
    private boolean hasTable = false;
    private String tableName;
    private static final String DATABASE_NAME = "jdbc:h2:~/azharkov";

    public DatabaseService(Class<T> typeClas) throws DatabaseServiceException {
        columns = new ArrayList<>();
        this.typeClass = typeClas;
        Table table = typeClass.getAnnotation(Table.class);
        if (table == null) {
            throw new DatabaseServiceException("Class must be annotated with Table");
        }

        tableName = table.name();
        if (tableName.equals("")) {
            tableName = typeClass.getSimpleName();
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName);
        }

        for (Field field : typeClass.getDeclaredFields()) {
            if (field.getAnnotation(Column.class) != null) {
                columns.add(new AnnotatedField(field));
            }
            if (field.getAnnotation(PrimaryKey.class) != null) {
                if (field.getAnnotation(Column.class) == null) {
                    throw new DatabaseServiceException("Primary key must be column");
                }
                if (primaryKey != -1) {
                    throw new DatabaseServiceException("Primary key must be only one");
                }
                primaryKey = columns.size() - 1;
            }
        }

        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (ResultSet resultSet = connection.getMetaData().getTables(null, null,
                    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, tableName), null)) {
                if (resultSet.next()) {
                    hasTable = true;
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final void createTable() throws DatabaseServiceException {
        if (hasTable) {
            throw new DatabaseServiceException("There is table already");
        }

        StringBuilder createRequest = new StringBuilder();
        createRequest.append("CREATE TABLE ").append(tableName).append(" (");

        for (AnnotatedField field : columns) {
            createRequest.append(field.getColumnName()).append(" ");
            createRequest.append(field.getSqlType());
            if (field.getField().isAnnotationPresent(PrimaryKey.class)) {
                createRequest.append(" NOT NULL PRIMARY KEY");
            }
            createRequest.append(", ");
        }
        createRequest.deleteCharAt(createRequest.lastIndexOf(","));
        createRequest.append(")");

        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(createRequest.toString());
                hasTable = true;
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final void dropTable() throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no database to drop");
        }
        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + tableName);
                hasTable = false;
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    //if element is not in table return null
    public final <K> T queryById(K key) throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        if (primaryKey == -1) {
            throw new DatabaseServiceException("primary key should exist for this operation");
        }
        if (!columns.get(primaryKey).getField().getType().isInstance(key)) {
            throw new IllegalArgumentException("key should have same type as primary key");
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");

        List<T> result;
        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                statement.setObject(1, key);
                try (ResultSet resultSet = statement.executeQuery()) {
                    result = convertResult(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
        if (result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public final List<T> queryForAll() throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        try {
            return queryWithRequest("SELECT * FROM " + tableName);
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final boolean isTableCreated() {
        return hasTable;
    }

    public final void insert(T element) throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        StringBuilder insertRequest = new StringBuilder();
        insertRequest.append("INSERT INTO ").append(tableName).append(" ( ");
        for (AnnotatedField field : columns) {
            insertRequest.append(field.getColumnName()).append(", ");
        }
        insertRequest.deleteCharAt(insertRequest.lastIndexOf(","));
        insertRequest.append(") VALUES (");

        for (AnnotatedField field : columns) {
            insertRequest.append("?").append(", ");
        }
        insertRequest.deleteCharAt(insertRequest.lastIndexOf(","));
        insertRequest.append(")");

        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (PreparedStatement statement = connection.prepareStatement(insertRequest.toString())) {
                for (int i = 0; i < columns.size(); ++i) {
                    try {
                        statement.setObject(i + 1, columns.get(i).getField().get(element));
                    } catch (IllegalAccessException e) {
                        throw new DatabaseServiceException("bad argument for insert");
                    }
                }
                statement.execute();
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final void update(T element) throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        if (primaryKey == -1) {
            throw new DatabaseServiceException("primary key must exist for this operation");
        }
        StringBuilder updateRequest = new StringBuilder();
        updateRequest.append("UPDATE ").append(tableName).append(" SET ");
        for (AnnotatedField field : columns) {
            updateRequest.append(field.getColumnName()).append(" = ?, ");
        }
        updateRequest.deleteCharAt(updateRequest.lastIndexOf(","));
        updateRequest.append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");
        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (PreparedStatement statement = connection.prepareStatement(updateRequest.toString())) {
                try {
                    for (int i = 0; i < columns.size(); ++i) {
                        statement.setObject(i + 1, columns.get(i).getField().get(element));
                    }
                    statement.setObject(columns.size() + 1, columns.get(primaryKey).getField().get(element));
                } catch (IllegalAccessException e) {
                    throw new DatabaseServiceException("bad element for update");
                }
                statement.execute();
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final <K> void deleteByKey(K key) throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        if (primaryKey == -1) {
            throw new DatabaseServiceException("primary key must exist for this operation");
        }
        if (!columns.get(primaryKey).getField().getType().isInstance(key)) {
            throw new IllegalArgumentException("key should have same type as primary key");
        }
        StringBuilder deleteRequest = new StringBuilder();
        deleteRequest.append("DELETE FROM ").append(tableName).append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");

        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (PreparedStatement statement = connection.prepareStatement(deleteRequest.toString())) {
                statement.setObject(1, key);
                statement.execute();
            }
        } catch (SQLException ex) {
            throw new DatabaseServiceException("Connection with database failed!", ex);
        }
    }

    public final void delete(T line) throws DatabaseServiceException {
        if (!hasTable) {
            throw new DatabaseServiceException("there is no table");
        }
        Object key = null;
        try {
            key = columns.get(primaryKey).getField().get(line);
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException("bad element for delete");
        }
        deleteByKey(key);
    }

    private List<T> queryWithRequest(String query) throws SQLException, DatabaseServiceException {
        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    return convertResult(resultSet);
                }
            }
        }
    }

    private List<T> convertResult(ResultSet resultSet) throws SQLException, DatabaseServiceException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            T newElement;
            try {
                newElement = typeClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DatabaseServiceException("Can not create new element");
            }
            for (int i = 0; i < columns.size(); ++i) {
                try {
                    AnnotatedField currentField = columns.get(i);
                    switch (currentField.getSqlType()) {
                        case "INT":
                            Long number = resultSet.getLong(i + 1);
                            if (currentField.getField().getType().equals(Byte.class)
                                    || currentField.getField().getType().equals(Byte.TYPE)) {
                                currentField.getField().set(newElement, number.byteValue());
                            } else if (currentField.getField().getType().equals(Short.class)
                                    || currentField.getField().getType().equals(Short.TYPE)) {
                                currentField.getField().set(newElement, number.shortValue());
                            } else if (currentField.getField().getType().equals(Integer.class)
                                    || currentField.getField().getType().equals(Integer.TYPE)) {
                                currentField.getField().set(newElement, number.intValue());
                            } else {
                                currentField.getField().set(newElement, number);
                            }
                            break;
                        case "DOUBLE":
                            Double doubleNumber = resultSet.getDouble(i + 1);
                            if (currentField.getField().getType().equals(Float.class)
                                    || currentField.getField().getType().equals(Float.TYPE)) {
                                currentField.getField().set(newElement, doubleNumber.floatValue());
                            } else {
                                currentField.getField().set(newElement, doubleNumber);
                            }
                            break;
                        case "VARCHAR(10)":
                            if (columns.get(i).getField().getType().equals(String.class)) {
                                String string = resultSet.getString(i + 1);
                                columns.get(i).getField().set(newElement, string);
                            } else {
                                char c = resultSet.getString(i + 1).charAt(0);
                                columns.get(i).getField().set(newElement, c);
                            }
                            break;
                        default:
                            throw new DatabaseServiceException("Type of field in class unsupported");
                    }
                } catch (IllegalAccessException e) {
                    throw new DatabaseServiceException("Can not initialize new element");
                }
            }
            result.add(newElement);
        }
        return result;
    }
}
