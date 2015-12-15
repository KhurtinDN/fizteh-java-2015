package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.mipt.diht.students.maxdankow.miniorm.Utils.camelCaseToLowerCase;

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

    DatabaseService(Class newItemsClass) {
        itemsClass = newItemsClass;
        tableName = getTableName();
        columnList = getColumnList();
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

    private <R> R databaseRequest(CheckedFunction<Statement, R> action) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement statement = connection.createStatement();
            return action.apply(statement);
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public void createTable() throws IllegalStateException {
//        databaseRequest((Statement s) -> {
//            s.execute(statementBuilder.buildCreate());
//            return true;
//        });
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement createStatement = connection.createStatement();
            createStatement.execute(statementBuilder.buildCreate());
            if (primaryKey != null) {
                addPrimaryKey(primaryKey);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public void addPrimaryKey(ItemColumn key) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement addPrimaryKeyStatement =
                    connection.createStatement();
            addPrimaryKeyStatement.execute("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + key.name + ")");
            System.err.println("PK успешно добавлен.");
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }

    }

    public void dropTable() throws IllegalStateException {
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

    public String getTableName() {
        // Проверяем, проаннотирован ли класс @Table
        Table tableAnnotation;
        if (itemsClass.isAnnotationPresent(Table.class)) {
            tableAnnotation = (Table) itemsClass.getAnnotation(Table.class);
        } else {
            throw new IllegalArgumentException("Class has no @Table annotation");
        }

        // Если имя таблицы не указано, то сгерерируем его.
        String tableName = tableAnnotation.name();
        if (Objects.equals(tableName, UNNAMED)) {
            tableName = camelCaseToLowerCase(itemsClass.getSimpleName());
        }
        return tableName;
    }

    public List<ItemColumn> getColumnList() {
        List<ItemColumn> columnList = new ArrayList<>();
        primaryKey = null;

        // Пройдемся по полям класса и найдем аннотированные @Column
        Field[] fields = itemsClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {

                Column column = field.getAnnotation(Column.class);
                String name = column.name();
                String type = column.type();

                // Если имя не задано, то сгернерируем.
                if (name.equals(UNNAMED)) {
                    name = camelCaseToLowerCase(field.getName());
                }
                ItemColumn itemColumn = new ItemColumn(name, type, field);
                columnList.add(itemColumn);

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    // Объявление более одного @PrimaryKey недопустимо.
                    if (primaryKey != null) {
                        throw new IllegalStateException("More than one primary key presents");
                    }
                    primaryKey = itemColumn;
                }
            }
        }
        return columnList;
    }

}

