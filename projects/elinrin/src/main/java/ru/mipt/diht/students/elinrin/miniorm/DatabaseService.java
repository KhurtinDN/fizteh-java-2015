package ru.mipt.diht.students.elinrin.miniorm;
import com.google.common.base.CaseFormat;
import ru.mipt.diht.students.elinrin.miniorm.annotations.Column;
import ru.mipt.diht.students.elinrin.miniorm.annotations.PrimaryKey;
import ru.mipt.diht.students.elinrin.miniorm.annotations.Table;
import ru.mipt.diht.students.elinrin.miniorm.exception.HandlerOfException;

import javax.management.OperationsException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*

T queryById(K) - возвращает запись по первичному ключу
T queryForAll() - возвращает все записи из таблицы
void insert(T) - добавляет запись
void update(T) - редактирует запись по первичному ключу
void delete(T) - удаляет запись по первичному ключу
void createTable() - создаёт таблицу по метаданным класса T. См. аннотации ниже.
void dropTable() - удаляет таблицу, соответствующую T. Класс нужно покрыть модульными тестами.
*/
public class DatabaseService<T> {
    //Классы JDSC
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private Field primaryKey = null;
    private List<Field> columns = null;
    private Field[] fields;
    private String[] namesOfColumns;
    private Class<T> tableClass = null;
    private String tableName = "";
    private int primaryKeyFieldNumber = -1;

    private boolean hasTableYet = false;


    /*protected void finalize ( ) throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }*/

   // DatabaseService(Class) - коструктор, принимает тип объекта, с которым хотим работать
    DatabaseService(final Class<T> elementClass) throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException {

        Class.forName("org.h2.Driver").newInstance();
        //путь к файлу с БД, соединение с драйверами БД
        connection = DriverManager.getConnection("jdbc:h2:~/test", "test", "test");
        statement = connection.createStatement();

        columns = new ArrayList<>();
        tableClass = elementClass;
        Table table = tableClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Class should be annotated with Table");
        }
        tableName = table.name();
        if (tableName.equals("")) {
            tableName = "MY_TABLE";
        }
        int i = 0;
        for (Field elem : tableClass.getDeclaredFields()) {
            if (elem.getAnnotation(Column.class) != null) {
                columns.add(elem);
            }
            if (elem.getAnnotation(PrimaryKey.class) != null) {
                if (elem.getAnnotation(Column.class) == null) {
                    throw new IllegalArgumentException("Not all fields are columns");
                }
                if (primaryKey != null) {
                    throw new IllegalArgumentException("Not one primary Key");
                }
                primaryKey = elem;
                primaryKeyFieldNumber = i;
            }
            ++i;
        }

        resultSet = connection.getMetaData().getTables(null, null,
                        CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, tableName), null);
        resultSet.next();
    }
    public final <K> T queryById(final K id) throws OperationsException,
            SQLException, IllegalAccessException, InstantiationException {
        if (!hasTableYet) {
            throw new OperationsException("Для данного запроа необходимо создать таблицу");
        }
        if (primaryKey == null) {
            throw new OperationsException("Должен суущестовать первичный ключ");
        }
        /*if (!id.getClass().isInstance(primaryKey.getType())){
            throw new IllegalArgumentException("Ключ должен иметь тот же тип, что и первичные ключи таблицы");
        }*/
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(columns.get(primaryKeyFieldNumber).getAnnotation(Column.class)
                        .name()).append(" = ").append(id.toString());
        resultSet = statement.executeQuery(newRequest.toString());
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T item = tableClass.newInstance();
            for (Field field: columns) {
                if (field.getType().equals(Integer.class)) {
                    field.set(item, resultSet.getInt(field.getAnnotation(Column.class).name()));
                }
                if (field.getType().equals(String.class)) {
                    field.set(item, resultSet.getString(field.getAnnotation(Column.class).name()));
                }
            }
            list.add(item);
        }
        if (list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new OperationsException("Ошибка получения результата");
        }
        return list.get(0);
    }

    final List<T> queryForAll() throws OperationsException, SQLException, IllegalAccessException,
            InstantiationException {

        if (!hasTableYet) {
            throw new OperationsException("Для данного запроа необходимо создать таблицу");
        }
        if (primaryKey == null) {
            throw new OperationsException("Должен суущестовать первичный ключ");
        }
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("SELECT * FROM ").append(tableName);
        resultSet = statement.executeQuery(newRequest.toString());
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T item = tableClass.newInstance();
            for (Field field: columns) {
                if (field.getType().equals(Integer.class)) {
                    field.set(item, resultSet.getInt(field.getAnnotation(Column.class).name()));
                }
                if (field.getType().equals(String.class)) {
                    field.set(item, resultSet.getString(field.getAnnotation(Column.class).name()));
                }
            }
            list.add(item);
        }
        return list;
    }

    final void insert(final T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("INSERT INTO ").append(tableName).append(" VALUES(");
        boolean first = true;
        for (Field field:columns) {
            if (!first) {
                newRequest.append(", ");
            } else {
                first = false;
            }
            newRequest.append("?");
        }
        newRequest.append(")");
        //System.out.println(newRequest);
        PreparedStatement preparedStatement = connection.prepareStatement(newRequest.toString());

        for (int i = 0; i < columns.size(); i++) {
            try {
                preparedStatement.setObject(i + 1, columns.get(i).get(key));
            } catch (IllegalAccessException e) {
                HandlerOfException.handler(e);
            }
        }
        //System.out.println(preparedStatement.toString());
        preparedStatement.execute();

    }

    final void update(final T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder().append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < columns.size(); ++i) {
            if (i != 0) {
                newRequest.append(", ");
            }
            newRequest.append(columns.get(i).getAnnotation(Column.class).name()).append(" = ?");
        }

        newRequest.append(" WHERE ").append(columns.get(primaryKeyFieldNumber).getAnnotation(Column.class).name())
                .append(" = ?");
        PreparedStatement prepareStatement = connection.prepareStatement(newRequest.toString());
        for (int i = 0; i < columns.size(); i++) {
            try {
                prepareStatement.setObject(1, columns.get(i).get(key));
                prepareStatement.setObject(2, columns.get(primaryKeyFieldNumber).get(key));
            } catch (IllegalAccessException e) {
                HandlerOfException.handler(e);
            }
            prepareStatement.execute();
        }



    }


    final void delete(final T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("DELETE FROM ").append(tableName).append(columns.get(primaryKeyFieldNumber)
                .getAnnotation(Column.class).name()).append(" = ").append("?");
        PreparedStatement preparedStatement = connection.prepareStatement(newRequest.toString());
        try {
            preparedStatement.setObject(1, columns.get(primaryKeyFieldNumber).get(key));
            preparedStatement.execute();
        } catch (IllegalAccessException e) {
            HandlerOfException.handler(e);
        }
    }

    final void createTable() throws OperationsException, SQLException {
        if (hasTableYet) {
            throw new OperationsException("Невозможно повторно создать таблицу");
        }
        hasTableYet = true;
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        int i = 0;
        for (Field field : columns) {
            //System.out.println(field.getType().toString());
            if (i != 0) {
                newRequest.append(", ");
            }
            newRequest.append(field.getAnnotation(Column.class).name()).append(" ");
            newRequest.append(ClassConverter.convert(columns.get(i).getType())).append(" ");
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                newRequest.append("PRIMARY KEY");
                primaryKeyFieldNumber = i;
            }
            i++;
        }
        newRequest.append(") ");
        //System.out.println(newRequest);
        statement.execute(newRequest.toString());
    }

    final void dropTable() {
        try {
            statement.execute("DROP TABLE IF EXISTS " + tableName);
            hasTableYet = false;
        } catch (SQLException e) {
            HandlerOfException.handler(e);
        }
    }
}
