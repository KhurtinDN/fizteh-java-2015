package ru.mipt.diht.students.egdeliya.MiniORM;

//import java.io.Closeable;

//import java.util.ArrayList;
import org.h2.jdbcx.JdbcConnectionPool;

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
 * Created by Эгделия on 19.12.2015.
 */
public class DatabaseService<T> implements Closeable {

    private Class<T> tableClass;
    private String tableName;
    private Field[] fields;
    private Table tableAnnotation;
    private List<String> columnsNames = new ArrayList<>();

    //столбец, который является primary key
    private int primaryKeyPosition;
    private String primaryKeyColumnsName = "";

    //соединенеи с сервером
    private JdbcConnectionPool connection;

    private void setTableAnnotations() {

        //нам нужны аннотации, чтобы распознать,
        //где какие поля у класса
        tableAnnotation = tableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            System.err.println("There is no @Table annotation");
        }

    }

    private void setTableName() {
        tableName = tableAnnotation.name();
        if (tableName == "") {
            tableName = "DefaultTableName";
        }
    }

    private void setFields() {

        //массив полей
        fields = tableClass.getDeclaredFields();
        boolean thereIsPrimaryKye = false;
        int fieldsCounter = 0;
        for (Field column: fields) {
            if (!column.isAnnotationPresent(Column.class)) {
                System.err.println("There is incorrect @Column annotation");
            }

            //узнаем имя поля через аннотацию
            String currentColumnName = column.getAnnotation(Column.class).name();
            if (currentColumnName == "") {
                currentColumnName = "DefaultColumnName";
            }

            //поле с аннотацией primary key
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                if (!thereIsPrimaryKye) {
                    thereIsPrimaryKye = true;
                    primaryKeyPosition = fieldsCounter;
                    primaryKeyColumnsName = currentColumnName;
                } else {
                    System.err.println("There is too many primary keys");
                }
            }

            ++fieldsCounter;
            columnsNames.add(currentColumnName);
        }
    }

    public final void createTable() {

        //конструируем запрос
        String query = "CREATE TABLE IF NOT EXISTS " + tableName;
        query += "(";
        for (int i = 0; i < fields.length; i++) {
            query += columnsNames.get(i) + " ";
            query += new TypeConverter(fields[i].getType()).toSqlType();
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                query += " PRIMARY KEY";
            }
            if (i != fields.length - 1) {
                query += ", ";
            }
        }
        query += ")";

        //отправляем запрос на сервер
        try {
            Connection connect = connection.getConnection();
            connect.createStatement().execute(query);
        } catch (SQLException sql) {
            System.err.println(sql.getMessage());
            System.out.println("SQLException in createTable");
        }

    }

    public final void insert(T element) {
        //конструируем запрос
        String insertQuery = "INSERT INTO " + tableName + " VALUES";
        insertQuery += "(";

        for (int i = 0; i < fields.length; ++i) {
            insertQuery += "?";
            if (i != fields.length - 1) {
                insertQuery += ", ";
            }
        }
        insertQuery += ")";

        //System.out.println(insertQuery);

        //подстановка нужных значений для вопросов

        try {
            Connection connect = connection.getConnection();
            PreparedStatement statement = connect.prepareStatement(insertQuery);

            //нужно так делать, потому что мы не знаем тип SQL
            //Prepare приводит наш запрос к нужному виду
            for (int i = 0; i < fields.length; i++) {
                Field currentField = fields[i];

                //значение этого поля при подстановке
                Object elementObject = currentField.get(element);

                statement.setObject(i + 1, elementObject);
            }
            //посылаем запрос на сервер
            statement.execute();
        } catch (SQLException sql) {
            System.err.println(sql.getMessage());
            System.out.println("SQLException in insert");
        } catch (IllegalAccessException access) {
            System.err.println(access.getMessage());
            System.out.println("IllegalAccessException in insert");
        }
    }

    public final void dropTable() {

        //составляем запрос и отправляем
        try {
            Connection connect = connection.getConnection();
        connect.createStatement().execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
            System.out.println("SQLException in dropTable");
        }

    }

    public final void delete(T element) {
        String deleteQuery = "DELETE FROM " + tableName + " WHERE "
                + primaryKeyColumnsName + " = ?";

        System.out.println(deleteQuery);
        //подставляем под вопросики всё что нужно
        try {
            Connection connect = connection.getConnection();
            PreparedStatement statement = connect.prepareStatement(deleteQuery);

            //подставляем на место первого вопросика
            statement.setObject(1, fields[primaryKeyPosition].get(element));

            //отправляем запрос
            statement.execute();

        } catch (SQLException s) {
            System.err.println(s.getMessage());
            System.out.println("SQLException in delete");
        } catch (IllegalAccessException i) {
            System.err.println(i.getMessage());
            System.out.println("IllegalAccessException in delete");
        }
    }

    public final void updated(T element) {

        //должны обновить каждое поле нашей строки
        for (int i = 0; i < columnsNames.size(); i++) {
            if (i == primaryKeyPosition) {
                continue;
            }

            String currentColumn = columnsNames.get(i);
            //sql запрос
            String updateQuery = "UPDATE " + tableName + "SET " + currentColumn
                    + " = ? WHERE " + primaryKeyColumnsName + " = ?";

            try {
                Connection connect = connection.getConnection();
                PreparedStatement statement = connect.prepareStatement(updateQuery);

                //подготовка запросов на отправление (замена вопросиков)
                statement.setObject(1, fields[i].get(element));
                statement.setObject(2, fields[primaryKeyPosition].get(element));

                // Отправление на исполнение.
                statement.execute();
            } catch (SQLException s) {
                System.err.println(s.getMessage());
                System.out.println("SQLException in updated");
            } catch (IllegalAccessException illegal) {
                System.err.println(illegal.getMessage());
                System.out.println("IllegalAccessException in update");
            }

        }
    }

    //запрос SELECT *
    //должен вернуть список всех полей
    public final List<T> queryForAll() {
        String query = "SELECT * FROM " + tableName;
        List<T> tableStrings = new ArrayList<>();
        try {
            Connection connect = connection.getConnection();

            ResultSet result = connect.createStatement().executeQuery(query);


            while (result.next()) {
                T newTableString = tableClass.newInstance();

                for (int i = 0; i < fields.length; i++) {
                    String currentColumn = columnsNames.get(i);

                    Object currentObject = result.getObject(currentColumn);
                    fields[i].set(newTableString, currentObject);
                }
                tableStrings.add(newTableString);
            }
            return tableStrings;
        } catch (SQLException sql) {
            System.err.println(sql.getMessage());
            System.out.println("SQLException in SELECT FROM *");
        } catch (InstantiationException ins) {
            System.err.println(ins.getMessage());
            System.out.println("InstantiationException in SELECT FROM *");
        } catch (IllegalAccessException illeg) {
            System.err.println(illeg.getMessage());
            System.out.println("IllegalAccessException in SELECT FROM *");
        }
        return tableStrings;
    }

    //два шабона,
    //потому что уникальные ключи могут быть разными типами
    public final <K> T queryById(K primaryKey) throws SQLException, IllegalAccessException, InstantiationException {
        String query = "SELECT * FROM " + tableName + " WHERE " + primaryKeyColumnsName
                + " = "  + primaryKey;

        //должен выдать единственную строку
        T  tableString = tableClass.newInstance();

        Connection connect = connection.getConnection();

        //ответ базы данных возвращается в виде ResultSet
        ResultSet result = connect.createStatement().executeQuery(query);

        if (result.next()) {
            for (int i = 0; i < fields.length; i++) {
                String currentColumn = columnsNames.get(i);

                //по иени колонки возвращает значение поля
                //в текущей строке
                Object currentObject = result.getObject(currentColumn);
                fields[i].set(tableString, currentObject);
            }
        }

        return tableString;
    }

    public DatabaseService(Class<T> newClass) {
        tableClass = newClass;

        setTableAnnotations();
        setTableName();
        setFields();

        //подключение
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        connection = JdbcConnectionPool.create("jdbc:h2:~/test",  "test", "test");
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.dispose();
        }
    }

}
