package ru.mipt.diht.students.nkarpachev.databaseservice;

import java.lang.reflect.Type;
import java.sql.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.LinkedList;
import java.util.List;

import ru.mipt.diht.students.nkarpachev.databaseservice.Table;

public class DatabaseService<T> {
    private String tableName;
    private Field[] fields;
    private String[] columns;
    private Class<T> dataType;
    private int primaryKeyField;

    private static String databaseUrl = "jdbc:h2:~/myownminoorm";

    private String getUnderscore(String source) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            answer.append(source.charAt(i));
            if ((i < source.length() - 1) && Character.isUpperCase(source.charAt(i+1)));
        }
        return source.toLowerCase();
    }

    public DatabaseService(Class<T> objectType) throws Exception {
        constructTable(objectType);
    }

    private void constructTable(Class<T> dataType) {
        if (!dataType.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException();
        }
        if ((dataType.getAnnotation(Table.class).name()).equals("")) {
            tableName = dataType.getSimpleName();
        }
        else {
            tableName = dataType.getAnnotation(Table.class).name();
        }

        List<String> columnNames = new ArrayList<>();
        List<Field> fieldNames = new ArrayList<>();
        int currentFieldNum = 0;
        for (Field currentField : dataType.getFields()) {
            String columnName = new String();
            if (!currentField.isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException();
            } else {

                columnName = currentField.getAnnotation(Column.class).name() ;
                if (currentField.isAnnotationPresent(PrimaryKey.class)) {
                    if (primaryKeyField != -1) {
                        throw new IllegalArgumentException("Must be only 1 primary key");
                    }
                    primaryKeyField = currentFieldNum;
                }
                if (columnName.equals("")) {
                    columnName = (getUnderscore(columnName)).toLowerCase();
                }
            }
        columnNames.add(columnName);
        fieldNames.add(currentField);
        currentFieldNum++;
        }

        fields = new Field[fieldNames.size()];
        fields = fieldNames.toArray(fields);

        columns = new String[columnNames.size()];
        columns = columnNames.toArray(columns);

    }

    public void createTable() {
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE IF NOT EXISTS ");
        createQuery.append(tableName);
        createQuery.append("(");
        for (int i = 0; i < fields.length; i++) {
            if (i != 0) {
                createQuery.append(", ");
            }
            createQuery.append(columns[i] + " " + TypeConverter.convertType(fields[i].getType()));
            if (i == primaryKeyField) {
                createQuery.append(" PRIMARY KEY");
            }
        }
        createQuery.append(");");

        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            connection.createStatement().execute(createQuery.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTable() {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            connection.createStatement().execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(T element) {
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO ");
        insertQuery.append(tableName);
        insertQuery.append(" VALUES(");

        List<Object> values = new ArrayList<>();
        try {
            for (Field field : fields) {
                values.add(field.get(element));
            }
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }

        for (int i = 0; i < fields.length; i++) {
            if (i != 0) {
                insertQuery.append(", ");
            }
            insertQuery.append("?");
        }
        insertQuery.append(")");
        Object[] valuesArray = new Object[values.size()];
        valuesArray = values.toArray();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (PreparedStatement statement = connection.prepareStatement(insertQuery.toString())) {
                for (int i = 0; i < valuesArray.length; i++) {
                    statement.setObject(i + 1, valuesArray[i]);
                }
                statement.execute();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(T element) {
        try {
            if (delete(element)) {
                insert(element);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public boolean delete(T element) {
        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("DELETE FROM ");
        deleteQuery.append(tableName);
        deleteQuery.append(" WHERE");
        deleteQuery.append(columns[primaryKeyField]);
        deleteQuery.append("=?");

        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery.toString())) {
                statement.setObject(1, fields[primaryKeyField].get(element));
                statement.execute();
                return (statement.executeUpdate() > 0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public<K> List<T> queryById(K key) {
        StringBuilder primaryKeyQuery = new StringBuilder();
        primaryKeyQuery.append("SELECT * FROM ");
        primaryKeyQuery.append(tableName);
        primaryKeyQuery.append(" WHERE ");
        String primaryName = columns[primaryKeyField];
        primaryKeyQuery.append(primaryName);
        primaryKeyQuery.append("=?");

        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            PreparedStatement statement = connection.prepareStatement(primaryKeyQuery.toString());
            statement.setObject(1, key);
            ResultSet resultSet = statement.executeQuery();
            return makeList(resultSet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<T> queryAll() {
        StringBuilder selectAllQuery = new StringBuilder();
        selectAllQuery.append("SELECT * FROM ");
        selectAllQuery.append(tableName);

        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            PreparedStatement statement = connection.prepareStatement(selectAllQuery.toString());
            ResultSet queryResult = statement.executeQuery();
            return makeList(queryResult);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<T> makeList(ResultSet set) {
        try {
            List<T> answer = new LinkedList<>();
            while (set.next()) {
                T entity = dataType.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    String fieldName = columns[i];
                    if (fieldName == null) {
                        continue;
                    }
                    fields[i].set(entity, set.getObject(fieldName));
                }
                answer.add(entity);
            }
            return answer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
