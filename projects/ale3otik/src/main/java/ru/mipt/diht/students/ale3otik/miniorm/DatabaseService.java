package ru.mipt.diht.students.ale3otik.miniorm;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static java.lang.Enum.valueOf;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

/**
 * Created by alex on 15.12.15.
 */
@SuppressWarnings("Duplicates")
public class DatabaseService <T> {
    private Class<T> aClass;
    private Table annotation;
    private String table;
    private Field[] fields;
    private Field primaryKeyField;
    private Connection connection;
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

    private void validateClassType() throws DatabaseServiceException {

        int pkCount = 0;
        for(Field f : fields) {
            if(f.getAnnotatedType() == null) continue;
            if(f.isAnnotationPresent(PrimaryKey.class) == true) {
                ++pkCount;
                primaryKeyField = f;
            }
            if(pkCount > 1) {
                throw new DatabaseServiceException("not unique @PrimaryKey field");
            }
            if(H2DBTypeResolver.resolve(f.getType()) == null) {
                throw new DatabaseServiceException(f.getType().toString() + "is not allowed");
            }
        }
        if(pkCount == 0) {
            throw new DatabaseServiceException("@PrimaryKey wasn't found");
        }
    }

    public DatabaseService(Class<T> dataClass) throws SQLException,DatabaseServiceException {
        aClass = dataClass;
        annotation = aClass.getAnnotation(Table.class);
        table = annotation.name();
        fields = aClass.getDeclaredFields();
        connection = DriverManager.getConnection(DATABASE_PROTOCOL_HEAD);
        validateClassType();
    }

    //    - возвращает запись по первичному ключу
    private <T> List<T> buildObjects(ResultSet rs) throws SQLException,
            IllegalAccessException, InstantiationException {
        List<T> result = new LinkedList<>();

        while(rs.next()) {
            T instance = (T) aClass.newInstance();
            for(Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if(column == null) continue;

                field.set(instance,rs.getObject(column.name()));
            }
            result.add(instance);
        }
        return result;
    }

    public <T,K> T queryById(K key) throws SQLException,
            DatabaseServiceException, IllegalAccessException, InstantiationException {

        Column column = primaryKeyField.getAnnotation(Column.class);
        PreparedStatement getStatement =
                connection.prepareStatement("SELECT * FROM " + table + " WHERE " + column.name() + " = ?");
        getStatement.setObject(1, key);
        ResultSet rs = getStatement.executeQuery();
        List<T> result = buildObjects(rs);
        if (result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    // - возвращает все записи из таблицы
    public <T> List<T> queryForAll() throws SQLException, IllegalAccessException, InstantiationException {
        PreparedStatement getStatement= connection.prepareStatement("SELECT * FROM " + table);
        ResultSet rs = getStatement.executeQuery();
        return buildObjects(rs);
    }

    //- добавляет запись
    public <T> void insert(T entity) throws SQLException, IllegalAccessException {

        List<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
                values.add(field.get(entity));
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
    }

    // - редактирует запись по первичному ключу
    public <T,K> void update(K key) throws SQLException {

    }

    //    удаляет запись по первичному ключу
    public <T,K> void delete(K key) throws SQLException {

    }

    // - создаёт таблицу по метаданным класса T. См. аннотации ниже.
    public void createTable() throws SQLException {
        StringBuilder headBuilder = new StringBuilder();
//        sBuilder.append(table).append(" (");
        int i = 0;
        for (Field field : fields) {
            if(i == 0) {
                ++i;
            } else {
                headBuilder.append(", ");
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                headBuilder.append(column.name()).append(" ")
                        .append(H2DBTypeResolver.resolve(field.getType()));
                if(field.isAnnotationPresent(PrimaryKey.class)) {
                    headBuilder.append(" NOT NULL PRIMARY KEY");
                }
            }
        }

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table
                + "(" + headBuilder.toString() + ")");
    }

    // - удаляет таблицу, соответствующую T
    public void dropTable() throws SQLException {
        Statement createStatement = connection.createStatement();
        createStatement.execute("DROP TABLE IF EXISTS " + table);
    }
}
