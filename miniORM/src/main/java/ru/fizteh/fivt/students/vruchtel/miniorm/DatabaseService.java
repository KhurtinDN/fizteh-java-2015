package ru.fizteh.fivt.students.vruchtel.miniorm;

import ru.fizteh.fivt.students.vruchtel.miniorm.annotations.Column;
import ru.fizteh.fivt.students.vruchtel.miniorm.annotations.PrimaryKey;
import ru.fizteh.fivt.students.vruchtel.miniorm.annotations.Table;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Серафима on 18.12.2015.
 */
public class DatabaseService<T> {
    private Class<T> typeClass;
    private List<AnnotatedField> columns;
    private int primaryKey = -1;
    private boolean hasTable = false;
    private String tableName;
    private static final String DATABASE_NAME = "jdbc:h2:~/database";

    public DatabaseService(Class<T> _typeClass) throws ClassNotFoundException, SQLException, InvalidParameterException {
        typeClass = _typeClass;
        Class.forName("org.h2.Driver");
        columns = new ArrayList<>();
        Table table = typeClass.getAnnotation(Table.class);

        if(table == null) {
            throw new InvalidParameterException("Class should be annotated with Table");
        }

        tableName = table.name();

        if(tableName.equals("")) {
            tableName = typeClass.getSimpleName();
            //tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName);
        }

        for(Field field : typeClass.getFields()) {
            if(field.getAnnotation(Column.class) != null) {
                columns.add(new AnnotatedField(field));
                }
            if(field.getAnnotation(PrimaryKey.class) != null) {
                if(field.getAnnotation(Column.class) == null) {
                    throw new InvalidParameterException("Primary key should be column");
                }
                if(primaryKey != -1) {
                    throw new InvalidParameterException("Primary key should be one");
                }
                primaryKey = columns.size() - 1;
            }
        }

        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(ResultSet resultSet = connection.getMetaData()
                    .getTables(null, null,
                                      tableName, null)) {
                if(resultSet.next()) {
                    hasTable = true;
                }
            }
        }
    }

    //Создание таблицы по метаданным класса T
    public void createTable() throws InvalidParameterException, SQLException {
        if(hasTable) {
            throw new InvalidParameterException("Table can be created only once for each class");
        }

        StringBuilder createRequest = new StringBuilder();
        createRequest.append("CREATE TABLE ").append(tableName).append(" (");

        for(AnnotatedField field : columns) {
            createRequest.append(field.getColumnName()).append(" ");
            createRequest.append(field.getSqlType()).append(" ");
            if(field.getField().isAnnotationPresent(PrimaryKey.class)) {
                createRequest.append("NOT NULL PRIMARY KEY ");
            }
            createRequest.append(" , ");
        }

        createRequest.deleteCharAt(createRequest.lastIndexOf(","));
        createRequest.append(")");

        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(Statement statement = connection.createStatement()) {
                statement.execute(createRequest.toString());
                hasTable = true;
            }
        }
    }

    //Удаление таблицы, соответствующей T
    public void dropTable() throws InvalidParameterException, SQLException{
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + tableName);
                hasTable = false;
            }
        }
    }

    //Возвращает запись по первичному ключу
    public <K> T queryByID(K key) throws InvalidParameterException, SQLException {

        if(!hasTable) {
            throw new InvalidParameterException("table should be created before");
        }
        if(primaryKey == -1) {
            throw new InvalidParameterException("primary key should exist for delete");
        }
        if(!columns.get(primaryKey).getField().getType().isInstance(key)) {
            throw new InvalidParameterException("key should have same type as primary key");
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");
        List<T> result;

        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(PreparedStatement statement = connection.prepareStatement(query.toString())) {
                statement.setObject(1, key);
                try(ResultSet resultSet = statement.executeQuery()) {
                    result = convertResult(resultSet);
                }
            }
        }

        if(result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    //Возвращает все записи из таблицы
    public List<T> queryForAll() throws InvalidParameterException, SQLException {
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        return queryWithRequest("SELECT * FROM " + tableName);
    }

    //Добавление записи
    public void insert(T element) throws InvalidParameterException, SQLException {
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        StringBuilder insertRequest = new StringBuilder();
        insertRequest.append("INSERT INTO ").append(tableName).append(" ( ");
        for(AnnotatedField field : columns) {
            insertRequest.append(field.getColumnName()).append(", ");
        }
        insertRequest.deleteCharAt(insertRequest.lastIndexOf(","));
        insertRequest.append(") VALUES (");
        for(AnnotatedField field : columns) {
            insertRequest.append("?").append(", ");
        }
        insertRequest.deleteCharAt(insertRequest.lastIndexOf(","));
        insertRequest.append(")");

        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(PreparedStatement statement = connection.prepareStatement(insertRequest.toString())) {
                for(int i = 0; i < columns.size(); ++i) {
                    try {
                        statement.setObject(i + 1, columns.get(i).getField().get(element));
                    } catch(IllegalAccessException e) {
                        throw new InvalidParameterException("Bad argument for insert");
                    }
                }
                statement.execute();
            }
        }
    }

    //Редактирует запись по первичному ключу
    public void update(T element) throws InvalidParameterException, SQLException {
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        if(primaryKey == -1) {
            throw new InvalidParameterException("Primary key should exist for update");
        }

        StringBuilder updateRequest = new StringBuilder();
        updateRequest.append("UPDATE ").append(tableName).append(" SET ");
        for(AnnotatedField field : columns) {
            updateRequest.append(field.getColumnName()).append(" = ?, ");
        }
        updateRequest.deleteCharAt(updateRequest.lastIndexOf(","));
        updateRequest.append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");
        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try(PreparedStatement statement = connection.prepareStatement(updateRequest.toString())) {
                try {
                    for(int i = 0; i < columns.size(); ++i) {
                        statement.setObject(i + 1, columns.get(i).getField().get(element));
                    }
                    statement.setObject(columns.size() + 1, columns.get(primaryKey).getField().get(element));
                } catch(IllegalAccessException e) {
                    throw new InvalidParameterException("Bad element for update");
                }
                statement.execute();
            }
        }
    }

    //Удаляет запись по первичному ключу
    public void delete(T line) throws InvalidParameterException, SQLException {
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        Object key = null;
        try {
            key = columns.get(primaryKey).getField().get(line);
        } catch(IllegalAccessException e) {
            throw new InvalidParameterException("Bad element for delete");
        }
        deleteByKey(key);
    }

    private <K> void deleteByKey(K key) throws InvalidParameterException, SQLException {
        if(!hasTable) {
            throw new InvalidParameterException("Table should be created before");
        }
        if(primaryKey == -1) {
            throw new InvalidParameterException("Primary key should exist for delete");
        }
        if(!columns.get(primaryKey).getField().getType().isInstance(key)) {
            throw new IllegalArgumentException("key should have same type as primary key");
        }
        StringBuilder deleteRequest = new StringBuilder();
        deleteRequest.append("DELETE FROM ").append(tableName).append(" WHERE ")
                .append(columns.get(primaryKey).getColumnName()).append(" = ?");
        try(Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (PreparedStatement statement = connection.prepareStatement(deleteRequest.toString())) {
                statement.setObject(1, key);
                statement.execute();
            }
        }
    }

    private List<T> queryWithRequest(String query) throws InvalidParameterException, SQLException{
        try (Connection connection = DriverManager.getConnection(DATABASE_NAME)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    return convertResult(resultSet);
                }
            }
        }
    }

    private List<T> convertResult(ResultSet resultSet) throws InvalidParameterException, SQLException {
        List<T> result = new ArrayList<>();

        while(resultSet.next()) {
            T newElement;
            try {
                newElement = typeClass.newInstance();
            } catch(InstantiationException | IllegalAccessException e) {
                throw new InvalidParameterException("Can not create new element");
            }
            for(int i = 0; i < columns.size(); ++i) {
                try {
                    AnnotatedField currentField = columns.get(i);
                    switch(currentField.getSqlType()) {
                        case "INT":
                            Long number = resultSet.getLong(i + 1);
                            if(currentField.getField().getType().equals(Byte.class)
                                    || currentField.getField().getType().equals(Byte.TYPE)) {
                                currentField.getField().set(newElement, number.byteValue());
                            } else if(currentField.getField().getType().equals(Short.class)
                                    || currentField.getField().getType().equals(Short.TYPE)) {
                                currentField.getField().set(newElement, number.shortValue());
                            } else if(currentField.getField().getType().equals(Integer.class)
                                    || currentField.getField().getType().equals(Integer.TYPE)) {
                                currentField.getField().set(newElement, number.intValue());
                            } else {
                                currentField.getField().set(newElement, number);
                            }
                            break;
                        case "DOUBLE":
                            Double doubleNumber = resultSet.getDouble(i + 1);
                            if(currentField.getField().getType().equals(Float.class)
                                    || currentField.getField().getType().equals(Float.TYPE)) {
                                currentField.getField().set(newElement, doubleNumber.floatValue());
                            } else {
                                currentField.getField().set(newElement, doubleNumber);
                            }
                            break;
                        case "VARCHAR(10)":
                            if(columns.get(i).getField().getType().equals(String.class)) {
                                String string = resultSet.getString(i + 1);
                                columns.get(i).getField().set(newElement, string);
                            } else {
                                char c = resultSet.getString(i + 1).charAt(0);
                                columns.get(i).getField().set(newElement, c);
                            }
                            break;
                        default:
                            throw new InvalidParameterException("Type of field in class is bad.");
                    }
                } catch(IllegalAccessException e) {
                    throw new InvalidParameterException("Can not initialize new element");
                }
            }
            result.add(newElement);
        }

        return result;
    }
}
