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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String name() default "";

        String type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PrimaryKey {
    }

    private String tableName = null;
    private List<ItemColumn> columnList = null;
    private ItemColumn primaryKey = null;

    private static final String DATABASE_PATH = "jdbc:h2:./simple_database";
    Class itemsClass;

    DatabaseService(Class newItemsClass) {
        itemsClass = newItemsClass;
        tableName = getTableName();
        columnList = getColumnList();
    }

    public String buildCreateStatement() {
        StringBuilder createQuery = new StringBuilder("");
        createQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        int count = 0;
        for (ItemColumn column : columnList) {
            createQuery.append(column.name)
                    .append(" ")
                    .append(column.type);
            if (column == primaryKey) {
                createQuery.append(" NOT NULL");
            }
            if (count + 1 < columnList.size()) {
                createQuery.append(", ");
            }
            ++count;
        }
        createQuery.append(")");
        return createQuery.toString();
    }

    public String buildInsertStatement(T newItem) {
        StringBuilder insertQuery = new StringBuilder("");
        insertQuery.append("INSERT INTO ")
                .append(tableName)
                .append(" VALUES (");
        List<Field> values = getValueList(newItem);
        assert values.size() == columnList.size();

        for (int i = 0; i < values.size(); ++i) {
            Field field = values.get(i);
            field.setAccessible(true);

            try {
                if (field.getType() == String.class || field.getType() == char.class) {
                    insertQuery.append('\'')
                            .append(field.get(newItem))
                            .append('\'');
                } else {
                    insertQuery.append(field.get(newItem));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (i + 1 < columnList.size()) {
                insertQuery.append(", ");
            }
        }
        insertQuery.append(")");
        return insertQuery.toString();
    }

    public void createTable() throws IllegalStateException {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement createStatement = connection.createStatement();
            createStatement.execute(buildCreateStatement());
            if (primaryKey != null) {
                addPrimaryKey(primaryKey);
            }
        } catch (SQLException e) {
//            System.err.println("An SQL error occurred: " + e.getMessage());
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public void addPrimaryKey(ItemColumn key) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement addPrimaryKeyStatement =
                    connection.createStatement();
            addPrimaryKeyStatement.execute("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + primaryKey.name + ")");
            System.err.println("PK успешно добавлен.");
        } catch (SQLException e) {
//            System.err.println("An SQL error occurred: " + e.getMessage());
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }

    }

    public void dropTable() throws IllegalStateException {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement dropStatement = connection.createStatement();
            dropStatement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
//            System.err.println("An SQL error occurred: " + e.getMessage());
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    void insert(T item) {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement InsertStatement = connection.createStatement();
            int added = InsertStatement.executeUpdate(buildInsertStatement(item));

            if (added != 0) {
                System.err.println("Элемент успешно добавлен.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        }
    }

    public List<T> queryForAll() throws IllegalAccessException, InstantiationException {
        List<T> selectAddList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement selectAllStatement = connection.createStatement();
            ResultSet resultSet = selectAllStatement.executeQuery("SELECT * FROM " + tableName);

            // Обрабатываем полученные результаты.
            while (resultSet.next()) {
                // Создаем новый объект пустым конструктором.
                T item = (T) itemsClass.newInstance();
                Field[] itemFields = item.getClass().getFields();

                // Перебираем все нужные столбцы-поля.
                for (ItemColumn column : columnList) {
                    Field field = column.field;
                    Field itemField = item.getClass().getField(field.getName());

                    // Определяем какой тип получать в соостветствии с типом поля.
                    // String
                    // TODO: Вынести в отдельный метод.
                    if (field.getType() == String.class) {
                        String value = resultSet.getString(column.name);
                        field.set(item, value);
                    }
                    // int
                    if (field.getType() == int.class) {
                        int value = resultSet.getInt(column.name);
                        field.set(item, value);
                    }
                    // boolean
                    if (field.getType() == boolean.class) {
                        boolean value = resultSet.getBoolean(column.name);
                        field.set(item, value);
                    }
                }
                selectAddList.add(item);
            }
        } catch (SQLException e) {
//            System.err.println("An SQL error occurred: " + e.getMessage());
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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
                // Создаем новый объект пустым конструктором.
                item = (T) itemsClass.newInstance();
                Field[] itemFields = item.getClass().getFields();

                // Перебираем все нужные столбцы-поля.
                for (ItemColumn column : columnList) {
                    Field field = column.field;
                    Field itemField = item.getClass().getField(field.getName());

                    // Определяем какой тип получать в соостветствии с типом поля.
                    // String
                    // TODO: Вынести в отдельный метод.
                    if (field.getType() == String.class) {
                        String value = resultSet.getString(column.name);
                        field.set(item, value);
                    }
                    // int
                    if (field.getType() == int.class) {
                        int value = resultSet.getInt(column.name);
                        field.set(item, value);
                    }
                    // boolean
                    if (field.getType() == boolean.class) {
                        boolean value = resultSet.getBoolean(column.name);
                        field.set(item, value);
                    }
                }
            }
            if (resultSet.next()) {
                throw new IllegalStateException("Primary key search sesult is not single");
            }
        } catch (SQLException e) {
//            System.err.println("An SQL error occurred: " + e.getMessage());
            throw new IllegalStateException("An SQL error occurred: " + e.getMessage());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return item;
    }
//
//    void update(T item){}
//    void delete(T item) {}
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
        if (Objects.equals(tableName, "")) {
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
                if (name.equals("")) {
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
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String name = column.name();
                String type = column.type();

                // Если имя не задано, то сгернерируем.
                if (name.equals("")) {
                    name = camelCaseToLowerCase(field.getName());
                }
                values.add(field);
            }
        }
        return values;
    }
}

class ItemColumn {
    public ItemColumn(String name, String type, Field field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        ItemColumn other = (ItemColumn) obj;
        return this.name.equals(other.name)
                && this.type.equals(other.type)
                /*&& this.field == other.field*/;
    }

    public String name;
    public String type;
    Field field;
}
