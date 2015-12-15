package ru.mipt.diht.students.dkhurtin.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

/**
 * @author Denis Khurtin
 */
@SuppressWarnings("Duplicates")
public class SimpleOrm {

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

    public static class MiniOrm {
        public <T> void save(T entity) throws IllegalAccessException {
            Class<?> aClass = entity.getClass();
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

            try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson1")) {
                String columnsLine = columns.stream().collect(joining(", "));
                String valuesLine = values.stream()
                        .map(o -> (o instanceof String) ? "\'" + o + "'" : o.toString())
                        .collect(joining(", "));

                PreparedStatement statement =
                        connection.prepareStatement(
                                "INSERT INTO "+ table + " (" + columnsLine + ") " +
                                        "VALUES (" + valuesLine + ")");
                int updatedLines = statement.executeUpdate();

                System.out.println("Updated: " + updatedLines);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Table(name = "users")
    static class User {

        @Column(name = "name")
        String name;

        @Column(name = "age")
        int age;

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

    public static void main(String[] args) throws SQLException, IllegalAccessException {
        User user = new User();
        user.setName("Alex");
        user.setAge(18);

        MiniOrm orm = new MiniOrm();
        orm.save(user);


        try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson1")) {

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS USERS (name VARCHAR(255), age int)");

//            int result1 = statement.executeUpdate("INSERT INTO users VALUES ('Alex', 17)");
//            int result2 = statement.executeUpdate("INSERT INTO users VALUES ('noname', 21)");
//            System.out.println(result1);
//            System.out.println(result2);

            String minAge = "1";// new Scanner(System.in).nextLine(); // 18; drop table users;

            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM users WHERE age > ?");
            preparedStatement.setString(1, minAge);
            ResultSet rs = preparedStatement.executeQuery();


            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user2 = new User();
                user2.setName(rs.getString("name"));
                user2.setAge(rs.getInt("age"));
                users.add(user2);
            }

            System.out.println(users);
        }
    }
}
