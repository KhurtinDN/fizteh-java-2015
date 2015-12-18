package ru.mipt.diht.students.lenazherdeva.miniORM;

import org.h2.jdbcx.JdbcConnectionPool;
import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.Column;
import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.PrimaryKey;
import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.Table;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 18.12.2015.
 */
public class DataBaseService<T> implements Closeable {

    private JdbcConnectionPool connectionTool; //поле для соединения с сервером базы данных
    private String tableName;
    private Class<T> ourTableClass; //хранит информацию о строках таблицы //в базе данных
    private String[] namesOfColumns;
    private Field[] fields;

    // Уникальный идентификатор элемента в базе данных
    private String primaryKeyFieldName;
    private int primaryKeyFieldNumber;

    DataBaseService(Class<T> inputClass) throws Exception {
        tableInit(inputClass);
    }

    private void tableInit(Class<T> inputClass) throws Exception {
        ourTableClass = inputClass;
        //аннотируем таблицу
        Table tableAnnotation = ourTableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new Exception("Your table should be annotated!");
        }
        tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = "QueenTable";
        }

        fields = ourTableClass.getDeclaredFields(); //информация о полях таблицы
        List<String> columnsNamesList = new ArrayList<>();

        //корректность входных данных?

        int counterOfFields = 0;
        boolean presentKey = false; // присутствие primaryKey
        for (Field ourColumn : fields) {
            if (!ourColumn.isAnnotationPresent(Column.class)) {
                throw new Exception("Not all elements of your table are columns!");
            }
            String currentColumnName = ourColumn.getAnnotation(Column.class).name();
            if (currentColumnName.equals("")) {
                currentColumnName = "EmptyName";
            }

            //единственнен ли primarykey??
            if (ourColumn.isAnnotationPresent(PrimaryKey.class)) {
                if (!presentKey) {
                    presentKey = true;
                    primaryKeyFieldNumber = counterOfFields;
                    primaryKeyFieldName = currentColumnName;
                } else {
                    throw new Exception("More then one primary key!");
                }
            }
            columnsNamesList.add(currentColumnName);
            counterOfFields++;
        }
        namesOfColumns = new String[columnsNamesList.size()];
        namesOfColumns = columnsNamesList.toArray(namesOfColumns);

        //соединение с сервером
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        connectionTool = JdbcConnectionPool.create("jdbc:h2:~/test", "test", "test");
    }

    public final void createTable() {
        //  CREATE TABLE IF NOT EXISTS Parking(ID INTEGER PRIMARY KEY, TYPE VARCHAR(255), OWNER VARCHAR(255))
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

        // отправляем этот запрос на сервер
        sendQuery(ourSQLQuery);
    }

    public final void dropTable() {
        sendQuery("DROP TABLE IF EXISTS " + tableName);
    }

    public final void insert(T elementToInsert) {
        String insertSQLQuery = "INSERT INTO " + tableName + " VALUES";
        insertSQLQuery += "(";
        for (int i = 0; i < fields.length; i++) {
            //?- пока неизвестные аргументы в формируемом запросе
            insertSQLQuery += "?";
            if (i != fields.length - 1) {
                insertSQLQuery += ", ";
            }
        }
        insertSQLQuery += ")";

        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement = connect.prepareStatement(insertSQLQuery);
            for (int i = 0; i < fields.length; i++) {
                Field currentField = fields[i];
                Object elementToSubstitute = currentField.get(elementToInsert);
                // Подставляем вместо вопросика
                statement.setObject(i + 1, elementToSubstitute);
            }
            // на сервер
            statement.execute();
        } catch (SQLException ex) {
            System.err.println("Error in inserting.\n" + ex);
        } catch (IllegalAccessException ex) {
            System.err.println("\n" + "An error has occurred unauthorized access.(inserting)");
        }
    }

    public final void update(T elementToUpdate) {
        for (int i = 0; i < namesOfColumns.length; i++) {
            if (i == primaryKeyFieldNumber) {
                //не обновляем primary key
                continue;
            }
            String currentColumnName = namesOfColumns[i];

            String updateSQLQuery = "UPDATE " + tableName + " SET "
                    + currentColumnName + " = ? WHERE " + primaryKeyFieldName + " = ?";
            System.out.println(updateSQLQuery);

            try {
                Connection connect = connectionTool.getConnection();
                PreparedStatement statement = connect.prepareStatement(updateSQLQuery);

                statement.setObject(1, fields[i].get(elementToUpdate));

                statement.setObject(2, fields[primaryKeyFieldNumber].get(elementToUpdate));

                statement.execute();
            } catch (SQLException ex) {
                System.err.println("Error in updating.");
            } catch (IllegalAccessException ex) {
                System.err.println("An error has occurred unauthorized access.(updating)");
            }
        }
    }

    public final void delete(T elementToDelete) {

        String deleteSQLQuery = "DELETE FROM " + tableName + " WHERE "
                + primaryKeyFieldName + " = ?";

        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement = connect.prepareStatement(deleteSQLQuery);

            statement.setObject(1, fields[primaryKeyFieldNumber].get(elementToDelete));

            statement.execute();
        } catch (SQLException ex) {
            System.err.println("Error in deleting.");
        } catch (IllegalAccessException ex) {
            System.err.println("An error has occurred unauthorized access.(deleting)");
        }
    }

    private void sendQuery(String querySQL) {
        try {
            // установим соединение для отправки запроса
            Connection connection = connectionTool.getConnection();

            connection.createStatement().execute(querySQL);
        } catch (SQLException exception) {
            System.err.println("Error in sending query");
        }
    }


    public final List<T> queryForAll() { //SELECT * FROM TABLE

        String ourSQLQuery = "SELECT * FROM " + tableName;
        List<T> answerList = new ArrayList<>();
        try {
            Connection connection = connectionTool.getConnection();
            ResultSet baseResult = connection.createStatement().executeQuery(ourSQLQuery);
            while (baseResult.next()) {
                T ourTakenElement = ourTableClass.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    // Из baseResult данные мы можем вытаскивать только по имени столбца
                    String currentColumnName = namesOfColumns[i];

                    Object currentObject = baseResult.getObject(currentColumnName);
                    // Устанавливаем новое значение в наш инстанциированный объект (ourTakenElement),
                    fields[i].set(ourTakenElement, currentObject);
                }
                answerList.add(ourTakenElement);
            }
        } catch (SQLException ex) {
            System.err.println("Exeption in queryForAll :" + ex);
        } catch (IllegalAccessException ex) {
            System.err.println("An error has occurred unauthorized access.(queryForAll): " + ex);
        } catch (InstantiationException ex) {
                System.err.println("\n" + "Failed to create an instance of the class(queryForAll) : " + ex);
        }
        return answerList;
    }

    public final <K> T queryById(K primaryKey) throws SQLException, InstantiationException, IllegalAccessException {
        // SELECT * FROM tableName WHERE ID = primaryKey

        String ourSQLQuery = "SELECT * FROM " + tableName + " WHERE " + primaryKeyFieldName + " = " + primaryKey;
        System.out.println(ourSQLQuery);
        T ourTakenElement = ourTableClass.newInstance();

        Connection connection = connectionTool.getConnection();
        ResultSet baseResult = connection.createStatement().executeQuery(ourSQLQuery);

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

    class FromJavaToSQLType {
        private String sqlClass;
        FromJavaToSQLType(Class javaClass) {
            if (javaClass == Integer.class) {
                sqlClass = "INTEGER";
            } else if (javaClass == String.class) {
                sqlClass = "VARCHAR(255)";
            }
        }
        @Override
        public String toString() {
            return sqlClass;
        }
    }











}
