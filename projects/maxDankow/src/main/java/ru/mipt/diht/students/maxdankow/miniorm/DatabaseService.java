package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.mipt.diht.students.maxdankow.miniorm.Utils.camelCaseToLowerCase;

public class DatabaseService<T> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String name();

        String type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PrimaryKey {
    }

    private String tableName;

    private static final String DATABASE_PATH = "jdbc:h2:/tmp/simple_database";
    Class itemsClass;

    DatabaseService(Class newItemsClass) {
        itemsClass = newItemsClass;
        getTableName();
    }

    private String getTableName() {
        Table tableAnnotation = (Table) itemsClass.getAnnotation(Table.class);
        return tableAnnotation.name();
    }

    private List<Column> getColumnsList() {
        List<Column> columnList = new ArrayList<>();

        // Пройдемся по полям класса и найдем аннотированные @Column
        Field[] fields = itemsClass.getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columnList.add(column);
                if (column.name() == null) {
//                    column.
                }
            }
        }
        return columnList;
    }

    private String buildCreateQuery() {
        StringBuilder createQuery = new StringBuilder("");
        createQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" ( ");

        List<Column> columns = getColumnsList();
        for (Column column : columns) {
            String name = column.name();
            if (name == null) {
                name = camelCaseToLowerCase();

            }
        }
        return createQuery.toString();
    }

    public void createTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement crateStatement =
                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS USERS ");
//            insertStatement.setString(1, tableName);
        } catch (SQLException e) {
            System.err.println("An SQL error occurred: " + e.getMessage());
        }
    }

    void dropTable() {
    }

    void insert(T item) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            PreparedStatement insertStatement =
                    connection.prepareStatement("INSERT INTO ? VALUES ('Peter', 99, true)");
            insertStatement.setString(1, tableName);
        } catch (SQLException e) {
            System.err.println("An SQL error occurred: " + e.getMessage());
        }
    }
//    T queryById(K){}
//    T queryForAll(){};
//    void update(T item){}
//    void delete(T item) {}
}
