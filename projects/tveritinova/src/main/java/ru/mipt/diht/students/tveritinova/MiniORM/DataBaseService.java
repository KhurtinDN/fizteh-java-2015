package ru.mipt.diht.students.tveritinova.MiniORM;

import org.h2.jdbcx.JdbcConnectionPool;
import ru.mipt.diht.students.tveritinova.MiniORM.Annotations.Column;
import ru.mipt.diht.students.tveritinova.MiniORM.Annotations.PrimaryKey;
import ru.mipt.diht.students.tveritinova.MiniORM.Annotations.Table;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseService<T> implements Closeable {

    private JdbcConnectionPool connectionTool;

    private String tableName;

    private Class<T> ourTableClass;

    private String[] namesOfColumns;

    private Field[] fields;

    private String primaryKeyFieldName;
    private int primaryKeyFieldNumber;

    DataBaseService(Class<T> inputClass) {
        try {
            tableInit(inputClass);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void tableInit(Class<T> inputClass) throws Exception {

        ourTableClass = inputClass;
        Table tableAnnotation = ourTableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new Exception("Ваша таблица должна быть аннотирована.");
        }

        tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = "EmptyName";
        }

        fields = ourTableClass.getDeclaredFields();

        List<String> columnsNamesList = new ArrayList<>();
        int i = 0;
        boolean present = false;
        for (Field ourColumn : fields) {

            if (!ourColumn.isAnnotationPresent(Column.class)) {
                throw new Exception("Не все поля вашего элемента таблицы "
                        + "являются столбцами.");
            }

            String currentColumnName = ourColumn
                    .getAnnotation(Column.class).name();
            if (currentColumnName.equals("")) {
                currentColumnName = "EmptyName";
            }

            if (ourColumn.isAnnotationPresent(PrimaryKey.class)) {
                if (!present) {
                    present = true;
                    primaryKeyFieldNumber = i;
                    primaryKeyFieldName = currentColumnName;
                } else {
                    throw new Exception("В таблице не должно присутствовать "
                            + "строк, имеющих более одного первичного ключа");
                }
            }

            columnsNamesList.add(currentColumnName);
            i++;
        }

        namesOfColumns = new String[columnsNamesList.size()];
        namesOfColumns = columnsNamesList.toArray(namesOfColumns);

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        connectionTool = JdbcConnectionPool
                .create("jdbc:h2:~/test",  "test", "test");
    }

    public final void createTable() {
        String ourSQLQuery = "CREATE TABLE IF NOT EXISTS " + tableName;
        ourSQLQuery += "(";
        for (int i = 0; i < fields.length; i++) {
            ourSQLQuery += namesOfColumns[i] + " ";
            ourSQLQuery += new FromJavaToSQLType(fields[i].getType());
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                ourSQLQuery += " PRIMARY KEY";
            }
            if (i != fields.length - 1) {
                ourSQLQuery += ",";
            }
        }
        ourSQLQuery += ")";

        sendQuery(ourSQLQuery);
    }

    public final void dropTable() {
        sendQuery("DROP TABLE IF EXISTS " + tableName);
    }

    public final void insert(T elementToInsert) {
        String insertSQLQuery = "INSERT INTO " + tableName + " VALUES";
        insertSQLQuery += "(";
        for (int i = 0; i < fields.length; i++) {
            insertSQLQuery += "?";
            if (i != fields.length - 1) {
                insertSQLQuery += ", ";
            }
        }
        insertSQLQuery += ")";

        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement =
                    connect.prepareStatement(insertSQLQuery);
            for (int i = 0; i < fields.length; i++) {
                Field currentField = fields[i];
                Object elementToSubstitute = currentField.get(elementToInsert);
                statement.setObject(i + 1, elementToSubstitute);
            }
            statement.execute();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

    }

    public final void update(T elementToUpdate) {
        for (int i = 0; i < namesOfColumns.length; i++) {
            if (i == primaryKeyFieldNumber) {
                continue;
            }
            String currentColumnName = namesOfColumns[i];
            String updateSQLQuery = "UPDATE " + tableName + " SET "
                    + currentColumnName
                    + " = ? WHERE " + primaryKeyFieldName + " = ?";
            System.out.println(updateSQLQuery);
            try {
                Connection connect = connectionTool.getConnection();
                PreparedStatement statement = connect
                        .prepareStatement(updateSQLQuery);
                statement.setObject(1,
                        fields[i].get(elementToUpdate));
                statement.setObject(2,
                        fields[primaryKeyFieldNumber].get(elementToUpdate));
                statement.execute();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

    }

    public final void delete(T elementToDelete) {
        String deleteSQLQuery = "DELETE FROM " + tableName + " WHERE "
                + primaryKeyFieldName + " = ?";
        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement = connect
                    .prepareStatement(deleteSQLQuery);
            statement.setObject(1,
                    fields[primaryKeyFieldNumber].get(elementToDelete));
            statement.execute();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void sendQuery(String querySQL) {
        try {
            Connection connection = connectionTool.getConnection();
            connection.createStatement().execute(querySQL);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public final List<T> queryForAll() {
        String ourSQLQuery = "SELECT * FROM " + tableName;
        List<T> answerList = new ArrayList<>();
        try {
            Connection connection = connectionTool.getConnection();
            ResultSet baseResult = connection.createStatement()
                    .executeQuery(ourSQLQuery);

            while (baseResult.next()) {
                T ourTakenElement = ourTableClass.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    String currentColumnName = namesOfColumns[i];
                    Object currentObject = baseResult
                            .getObject(currentColumnName);
                    fields[i].set(ourTakenElement, currentObject);

                }
                answerList.add(ourTakenElement);
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        return answerList;
    }

    public final <K> T queryById(K primaryKey) throws SQLException,
            InstantiationException, IllegalAccessException {

        String ourSQLQuery = "SELECT * FROM " + tableName
                + " WHERE " + primaryKeyFieldName + " = " + primaryKey;
        System.out.println(ourSQLQuery);
        T ourTakenElement = ourTableClass.newInstance();

        Connection connection = connectionTool.getConnection();
        ResultSet baseResult = connection.createStatement()
                .executeQuery(ourSQLQuery);

        if (baseResult.next()) {
            for (int i = 0; i < fields.length; i++) {
                String currentColumnName = namesOfColumns[i];
                Object currentObject = baseResult.getObject(currentColumnName);
                fields[i].set(ourTakenElement, currentObject);
            }
        }
        System.out.println(ourTakenElement);
        return ourTakenElement;
    }

    @Override
    public final void close() throws IOException {
        if (connectionTool != null) {
            connectionTool.dispose();
        }
    }
}
