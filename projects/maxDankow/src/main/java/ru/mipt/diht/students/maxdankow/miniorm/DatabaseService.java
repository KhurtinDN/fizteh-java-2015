package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

    private String tableName;
    private List<ItemColumn> columnList;

    private static final String DATABASE_PATH = "jdbc:h2:./simple_database";
    Class itemsClass;

    DatabaseService(Class newItemsClass) {
        itemsClass = newItemsClass;
        tableName = getTableName();
        columnList = getColumnList();
    }

    public String createStatementBuilder() {
        StringBuilder createQuery = new StringBuilder("");
        createQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        int count = 0;
        for (ItemColumn column : columnList) {
            createQuery.append(column.name)
                    .append(" ")
                    .append(column.type);
            if (count + 1 < columnList.size()) {
                createQuery.append(", ");
            }
            ++count;
        }
        createQuery.append(")");
        return createQuery.toString();
    }

    public void createTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement createStatement = connection.createStatement();
            createStatement.execute(createStatementBuilder());
        } catch (SQLException e) {
            System.err.println("An SQL error occurred: " + e.getMessage());
        }
    }

    void dropTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_PATH)) {
            Statement dropStatement = connection.createStatement();
            dropStatement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            System.err.println("An SQL error occurred: " + e.getMessage());
        }
    }

    //    void insert(T item) {}
    //    T queryById(K){}
//    T queryForAll(){};
//    void update(T item){}
//    void delete(T item) {}
    private String getTableName() {
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
            tableName = camelCaseToLowerCase(itemsClass.getName());
        }
        return tableName;
    }

    private List<ItemColumn> getColumnList() {
        List<ItemColumn> columnList = new ArrayList<>();

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
                columnList.add(new ItemColumn(name, type));
            }
        }
        return columnList;
    }

    class ItemColumn {
        public ItemColumn(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String name;
        public String type;
    }
}
