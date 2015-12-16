package dbwork;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import static java.lang.System.exit;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

public class DatabaseService {

    @Target(TYPE)
    @Retention(RUNTIME)
    @interface Table {
        String name();
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    @interface Column {
        String name();
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    @interface PrimaryKey {
    }

    @Table(name = "users")
    public static class User {

        @Column(name = "name")
        @PrimaryKey
        String name;

        @Column(name = "age")
        int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public User() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public Class<?> aClass;
    public Class<?> primaryKeyClass;

    public DatabaseService(Class<?> ac) {
        aClass = ac;
    }

    public <T> void insert(T entity) throws IllegalAccessException {

        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
                values.add(field.get(entity));
            }
        }

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            String columnsLine = columns.stream().collect(joining(", "));
            String valuesLine = values.stream()
                    .map(o -> (o instanceof String) ? "\'" + o + "'" : o.toString())
                    .collect(joining(", "));

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + table + " (" + columnsLine + ") " +
                            "VALUES (" + valuesLine + ")");
            int updatedLines = statement.executeUpdate();
            System.out.println("Updated: " + updatedLines);

        } catch (SQLException sqle) {
            System.out.println("Exception caught in insert: " + sqle.getMessage());
        }
    }

    public <T> List<T> queryForAll() {
        List<T> answers = new ArrayList<>();
        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();
        List<String> columns = new ArrayList<>();

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                T answer = (T) aClass.newInstance();
                for (Field field : fields) {
                    if (field.getType() == int.class) {
                        field.set(answer, rs.getInt(field.getName()));
                    } else {
                        field.set(answer, rs.getString(field.getName()));
                    }
                }
                answers.add(answer);
            }
        } catch (SQLException sqle) {
            System.out.println("SQLException caught in queryForAll: " + sqle.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (answers.size() == 0) {
            return null;
        }
        return answers;
    }

    public <T, K> T queryById(K key) {

        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();
        Field pk = null;
        primaryKeyClass = key.getClass();
        List<String> columns = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pk = field;
                break;
            }

            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }

        String pkFieldName = pk.getName();
        List<T> answers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            String keyToFind = key.toString();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + table + " WHERE " + pkFieldName + "= ?");
            preparedStatement.setString(1, keyToFind);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                T answer = (T) aClass.newInstance();
                for (Field field : fields) {
                    if (field.getType() == int.class) {
                        field.set(answer, rs.getInt(field.getName()));
                    } else {
                        field.set(answer, rs.getString(field.getName()));
                    }
                }
                answers.add(answer);
            }
        } catch (SQLException sqle) {
            System.out.println("SQLException caught in queryById: " + sqle.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assert (answers.size() <= 1);
        if (answers.size() == 0) {
            return null;
        }
        return answers.get(0);
    }

    public <T> void update(T entity) {
        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();
        Field pk = null;
        List<String> columns = new ArrayList<>();
        String keyStr = new String();

        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pk = field;
                field.setAccessible(true);
                try {
                    field.setAccessible(true);
                    keyStr = (String)field.get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }
        String pkFieldName = pk.getName();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM " + table + " WHERE " + pkFieldName + " = ?");
            preparedStatement.setString(1, keyStr);
            preparedStatement.executeUpdate();
            insert(entity);
        } catch (SQLException sqle) {
            System.out.println("SQLException caught in update: " + sqle.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public <T> void delete(T entity) {
        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();
        Field pk = null;
        List<String> columns = new ArrayList<>();
        Object key = null;
        String keyStr = new String();

        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pk = field;
                field.setAccessible(true);
                try {
                    field.setAccessible(true);
                    keyStr = (String)field.get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }
        String pkFieldName = pk.getName();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            String keyToFind = keyStr;
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM " + table + " WHERE " + pkFieldName + " = ?");
            preparedStatement.setString(1, keyToFind);
            preparedStatement.executeUpdate();
        } catch (SQLException sqle) {
            System.out.println("SQLException caught in delete: " + sqle.getMessage());
        }
    }

    public void createTable() {
        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();
        Field[] fields = aClass.getDeclaredFields();
        List<String> columnsWithTypes = new ArrayList<>();
        String typename;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                Class<?> type = field.getType();
                typename = new String();
                if (type == int.class) {
                    typename += "int";
                }
                if (type == long.class) {
                    typename += "bigint";
                }
                if (type == double.class) {
                    typename += "real";
                }
                if (type == String.class){
                    typename += "varchar(255)";
                }
                if (typename.length() == 0) {
                    System.out.println("Unsupported type!");
                    exit(1);
                }
                columnsWithTypes.add(column.name() + " " + typename);
            }
        }

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            String columnsLine = columnsWithTypes.stream().collect(joining(", "));

            PreparedStatement statement = connection.prepareStatement("DROP TABLE IF EXISTS " + table);

            statement.executeUpdate();

            statement = connection.prepareStatement(
                    "CREATE TABLE " + table + " (" + columnsLine + "); ");
            statement.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("Exception caught in create: " + sqle.getMessage());
        }
    }

    public void dropTable() {
        Table annotation = aClass.getAnnotation(Table.class);
        String table = annotation.name();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            PreparedStatement statement = connection.prepareStatement("DROP TABLE IF EXISTS" + table + ";");
            statement.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("Exception caught in drop: " + sqle.getMessage());
        }
    }

}
