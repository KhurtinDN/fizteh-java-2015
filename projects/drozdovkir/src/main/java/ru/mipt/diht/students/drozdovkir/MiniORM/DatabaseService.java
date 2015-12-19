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
    private StatementConstructor<T> statementBuilder = null;
    private String tableName = null;
    private List<TColumn> columnList = null;
    private TColumn primaryKey = null;
    private Class itemsClass;

    public DatabaseService(Class newItemsClass) throws IllegalArgumentException {
        itemsClass = newItemsClass;
        tableName = DatabaseServiceUtils.getTableName(itemsClass);
        Pair<List<TColumn>, TColumn> pair = DatabaseServiceUtils.analyseColumns(itemsClass);
        columnList = pair.getKey();
        primaryKey = pair.getValue();
        statementBuilder = new StatementConstructor(tableName, columnList,
                primaryKey, itemsClass);
    }

    public final StatementConstructor<T> getStatementBuilder() {
        return statementBuilder;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws SQLException, IllegalStateException;
    }


    private <R> R databaseRequest(CheckedFunction<Statement, R> action) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement statement = connection.createStatement();
            return action.apply(statement);
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public final void createTable() {
        databaseRequest((Statement statement) -> {
            statement.execute(statementBuilder.buildCreate());
            if (primaryKey != null) {
                statement.execute("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + primaryKey.getName() + ")");
                System.err.println("PK успешно добавлен.");
            }
            return true;
        });
    }

    public final void dropTable() {
        databaseRequest((Statement statement) -> {
            statement.execute("DROP TABLE IF EXISTS " + tableName);
            return true;
        });
    }

    public final void insert(T newItem) {
        int added = databaseRequest(
                (Statement statement) -> statement.executeUpdate(statementBuilder.buildInsert(newItem))
        );

        if (added != 0) {
            System.err.println("Элемент успешно добавлен.");
        }
    }

    public final List<T> queryForAll() {
        List<T> allItems = databaseRequest((Statement statement) -> {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            // Обрабатываем полученные результаты.
            List<T> selectAllList = new ArrayList<>();
            while (resultSet.next()) {
                T item = DatabaseServiceUtils.createItemFromSqlResult(resultSet, columnList, itemsClass);
                selectAllList.add(item);
            }
            return selectAllList;
        });
        System.err.println("Get all items: " + allItems);
        return allItems;
    }

    public final <K> T queryById(K key) {
        T itemById = databaseRequest((Statement statement) -> {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName
                    + " WHERE " + primaryKey.getName() + "=" + DatabaseServiceUtils.getSqlValue(key));

            T item = null;
            if (resultSet.next()) {
                item = DatabaseServiceUtils.createItemFromSqlResult(resultSet, columnList, itemsClass);
            }
            if (resultSet.next()) {
                throw new IllegalStateException("Primary key search result is not single");
            }
            return item;
        });
        System.err.println("Get item by ID: " + itemById);
        return itemById;
    }

    public final void update(T item) {
        databaseRequest((Statement statement) -> {
            statement.execute(statementBuilder.buildUpdate(item));
            return true;
        });
    }

    public final <K> void delete(K key) {
        databaseRequest((Statement statement) -> {
            statement.execute("DELETE FROM " + tableName + " WHERE "
                    + primaryKey.getName() + "=" + DatabaseServiceUtils.getSqlValue(key));
            return true;
        });
    }
}