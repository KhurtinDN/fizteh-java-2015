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
public class DatabaseService <T> {
    public static final long MAX_NAME_LENGTH = 200;
    public static final long MAX_STRING_LENGTH = 1000000L;

    private Class<T> aClass;
    private Table annotation;
    private String table;
    private Field[] fields;
    private static String DATABASE_PROTOCOL_HEAD = "jdbc:h2:./database/miniorm";

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Table {
        String name();
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface Column {
        String name();
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface PrimaryKey{}

    public DatabaseService(Class<T> dataClass) {
        aClass = dataClass;
        annotation = aClass.getAnnotation(Table.class);
        table = annotation.name();
        fields = aClass.getDeclaredFields();
    }

    //    - возвращает запись по первичному ключу
    public <T,K> T queryById(K key) throws SQLException {
        T ans = null;
        return ans;
    }

    // - возвращает все записи из таблицы
    public <T> List<T> queryForAll() throws SQLException, IllegalAccessException, InstantiationException {
        List<T> result = new LinkedList<>();
        Connection connection = DriverManager.getConnection(DATABASE_PROTOCOL_HEAD);
        PreparedStatement getStatement= connection.prepareStatement("SELECT * FROM " + table);
        ResultSet rs = getStatement.executeQuery();
        while(rs.next()) {

            T record = (T) aClass.newInstance();
            for(Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if(column == null) continue;

                field.set(record,rs.getObject(column.name()));
            }
            result.add(record);
        }

        return result;
    }

    //- добавляет запись
    public <T> void insert(T entity) throws SQLException, IllegalAccessException {

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
                values.add(field.get(entity));
            }
        }

        Connection connection = DriverManager.getConnection(DATABASE_PROTOCOL_HEAD);
        String columnsLine = columns.stream().collect(joining(", "));
        String valuesLine = values.stream()
                .map(x -> (x instanceof String) ? "'" + x + "'" : x.toString())
                .collect(joining(", "));

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("INSERT INTO ")
                .append(table)
                .append(" (").append(columnsLine).append(") ")
                .append("VALUES (").append(valuesLine).append(")");
        PreparedStatement insertStatement
                = connection.prepareStatement(requestBuilder.toString());
        insertStatement.executeUpdate();
    }

    // - редактирует запись по первичному ключу
    public <T,K> void update(K key) throws SQLException {

    }

    //    удаляет запись по первичному ключу
    public <T,K> void delete(K key) throws SQLException {

    }

    // - создаёт таблицу по метаданным класса T. См. аннотации ниже.
    public void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_PROTOCOL_HEAD);

        StringBuilder sBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sBuilder.append(table).append(" (");
        int i = 0;
        for (Field field : fields) {
            if(i == 0) {
                ++i;
            } else {
                sBuilder.append(", ");
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                sBuilder.append(column.name()).append(" VARCHAR(").append(MAX_STRING_LENGTH).append(")");
            }
        }
        sBuilder.append(" );");

        PreparedStatement createStatement
                = connection.prepareStatement(sBuilder.toString());
        createStatement.execute();
    }

    // - удаляет таблицу, соответствующую T
    public void dropTable() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_PROTOCOL_HEAD);
        Statement createStatement = connection.createStatement();
        createStatement.execute("DROP TABLE IF EXISTS " + table);
    }
}
