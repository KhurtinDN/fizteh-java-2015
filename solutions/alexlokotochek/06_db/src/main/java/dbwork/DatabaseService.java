package dbwork;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;
import static junit.framework.TestCase.assertEquals;

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
            System.out.println("Exception caught in insert");
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

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                //System.out.println(rs.getString("name") + rs.getString("age"));
                T answer = (T)aClass.newInstance();
                for (Field field : fields) {
                    //@TODO: Сделать полями не только числа/строки
                    if (field.getType() == int.class) {
                        field.set(answer, rs.getInt(field.getName()));
                    } else {
                        field.set(answer, rs.getString(field.getName()));
                    }
                }
                answers.add(answer);

                //@TODO: понять различия между полями и именами колонок
                // camelCase и тд
                // ой, всё. не хочу((

//                for (int i = 0; i < fields.length; ++i){
//                    T answer = (T)aClass.newInstance();
//                    fields[i].set(answer, rs.getString(columns[i].name()))
//                }
            }
        } catch (SQLException sqle) {
            System.out.println("Exception caught in queryById");
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
        List<String> columns = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pk = field;
                break;
                // TODO: exception if >1 PK
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
            //System.out.println("pkFieldName: " + pkFieldName);

            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM users WHERE " + pkFieldName + "= ?");
            preparedStatement.setString(1, keyToFind);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                //System.out.println(rs.getString("name") + rs.getString("age"));
                T answer = (T)aClass.newInstance();
                for (Field field : fields) {
                    //@TODO: Сделать полями не только числа/строки
                    if (field.getType() == int.class) {
                        field.set(answer, rs.getInt(field.getName()));
                    } else {
                        field.set(answer, rs.getString(field.getName()));
                    }
                }
                answers.add(answer);

                //@TODO: понять различия между полями и именами колонок
                // camelCase и тд
                // ой, всё. не хочу((

//                for (int i = 0; i < fields.length; ++i){
//                    T answer = (T)aClass.newInstance();
//                    fields[i].set(answer, rs.getString(columns[i].name()))
//                }
            }
        } catch (SQLException sqle) {
            System.out.println("Exception caught in queryById");
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assert(answers.size() <= 1);
        if (answers.size() == 0) {
            return null;
        }
        return answers.get(0);
    }

}