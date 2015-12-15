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

    public void createTable() throws IllegalStateException {
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
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement dropStatement = connection.createStatement();
            dropStatement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    void insert(T newItem) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement InsertStatement = connection.createStatement();
            int added = InsertStatement.executeUpdate(statementBuilder.buildInsert(newItem));

            if (added != 0) {
                System.err.println("Элемент успешно добавлен.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public List<T> queryForAll() {
        List<T> selectAddList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement selectAllStatement = connection.createStatement();
            ResultSet resultSet = selectAllStatement.executeQuery("SELECT * FROM " + tableName);
            // Обрабатываем полученные результаты.
            while (resultSet.next()) {
                T item = Utils.createItemFromSqlResult(resultSet, columnList, itemsClass);
                selectAddList.add(item);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
        return selectAddList;
    }

    public <K> T queryById(K key) {
        T item = null;
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement selectAllStatement = connection.createStatement();
            ResultSet resultSet = selectAllStatement.executeQuery("SELECT * FROM " + tableName
                    + " WHERE " + primaryKey.name + "=" + Utils.getSqlValue(key));

            // Обрабатываем полученный результат.
            if (resultSet.next()) {
                item = Utils.createItemFromSqlResult(resultSet, columnList, itemsClass);
            }
            if (resultSet.next()) {
                throw new IllegalStateException("Primary key search sesult is not single");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
        return item;
    }



    public void update(T item) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement updateStatement = connection.createStatement();
            updateStatement.execute(statementBuilder.buildUpdate(item));
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public <K> void delete(K key) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement deleteStatement = connection.createStatement();
            deleteStatement.execute("DELETE FROM " + tableName
                    + " WHERE " + primaryKey.name + "=" + Utils.getSqlValue(key));
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
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

    private List<Field> getValueList(T item) {
        List<Field> values = new ArrayList<>();
        // Пройдемся по полям класса и найдем аннотированные @Column
        Field[] fields = itemsClass.getDeclaredFields();
        for (Field field : fields) {
            // Будем осходить из того, что внутри одного запуска программы
            // порядок методов, возвращаемых getFields, не меняется.
            if (field.isAnnotationPresent(Column.class)) {
                values.add(field);
            }
        }
        return values;
    }
}

