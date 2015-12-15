package ru.mipt.diht.students.maxdankow.miniorm;

import javafx.util.Pair;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService<T> {
    static final String UNNAMED = "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String name() default UNNAMED;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String name() default UNNAMED;

        String type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PrimaryKey {
    }

    private static final String DATABASE_PATH = "jdbc:h2:./simple_database";
    private SqlStatementBuilder<T> statementBuilder = null;
    private String tableName = null;
    private List<ItemColumn> columnList = null;
    private ItemColumn primaryKey = null;
    private Class itemsClass;

    DatabaseService(Class newItemsClass) throws IllegalArgumentException {
        itemsClass = newItemsClass;
        tableName = Utils.getTableName(itemsClass);
        Pair<List<ItemColumn>, ItemColumn> pair = Utils.analyseColumns(itemsClass);
        columnList = pair.getKey();
        primaryKey = pair.getValue();
        statementBuilder = new SqlStatementBuilder<>(tableName, columnList,
                primaryKey, itemsClass);
    }

    public SqlStatementBuilder<T> getStatementBuilder() {
        return statementBuilder;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws SQLException, IllegalStateException;
    }

    // Осуществляет указанное действие с базой данных.
    private <R> R databaseRequest(CheckedFunction<Statement, R> action) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement statement = connection.createStatement();
            return action.apply(statement);
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public void createTable() {
        databaseRequest((Statement statement) -> {
            statement.execute(statementBuilder.buildCreate());
            if (primaryKey != null) {
                statement.execute("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + primaryKey.name + ")");
                System.err.println("PK успешно добавлен.");
            }
            return true;
        });
    }

    public void dropTable() {
        databaseRequest((Statement statement) -> {
            statement.execute("DROP TABLE IF EXISTS " + tableName);
            return true;
        });
    }

    void insert(T newItem) {
        int added = databaseRequest((Statement statement) -> statement.executeUpdate(statementBuilder.buildInsert(newItem)));

        if (added != 0) {
            System.err.println("Элемент успешно добавлен.");
        }
    }

    public List<T> queryForAll() {
        List<T> allItems = databaseRequest((Statement statement) -> {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            // Обрабатываем полученные результаты.
            List<T> selectAllList = new ArrayList<>();
            while (resultSet.next()) {
                T item = Utils.createItemFromSqlResult(resultSet, columnList, itemsClass);
                selectAllList.add(item);
            }
            return selectAllList;
        });
        System.err.println("Get all items: " + allItems);
        return allItems;
    }

    public <K> T queryById(K key) {
        T itemById = databaseRequest((Statement statement) -> {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName
                    + " WHERE " + primaryKey.name + "=" + Utils.getSqlValue(key));
            // Обрабатываем полученный результат.
            T item = null;
            if (resultSet.next()) {
                item = Utils.createItemFromSqlResult(resultSet, columnList, itemsClass);
            }
            if (resultSet.next()) {
                throw new IllegalStateException("Primary key search result is not single");
            }
            return item;
        });
        System.err.println("Get item by ID: " + itemById);
        return itemById;
    }

    public void update(T item) {
        databaseRequest((Statement statement) -> {
            statement.execute(statementBuilder.buildUpdate(item));
            return true;
        });
    }

    public <K> void delete(K key) {
        databaseRequest((Statement statement) -> {
            statement.execute("DELETE FROM " + tableName + " WHERE " + primaryKey.name + "=" + Utils.getSqlValue(key));
            return true;
        });
    }
}

