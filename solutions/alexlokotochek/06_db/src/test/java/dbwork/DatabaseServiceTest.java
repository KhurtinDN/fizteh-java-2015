//package dbwork;
import dbwork.DatabaseService;
import org.h2.store.Data;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lokotochek on 14.12.15.
 */

public class DatabaseServiceTest {

    @Test
    public void testInsert() throws Exception {

        final int testedUsers = 150;
        DatabaseService dbs = new DatabaseService(DatabaseService.User.class);

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), age int, PRIMARY KEY(name))");
            for (int i = 0; i <= testedUsers; ++i) {
                int age = i * i + 5;
                String name = "User" + i;
                dbs.insert(new DatabaseService.User(name, age));
            }

            String modAge = "4";

            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM users WHERE age % 10 != ? ORDER BY age");
            preparedStatement.setString(1, modAge);
            ResultSet rs = preparedStatement.executeQuery();

            List<DatabaseService.User> users = new ArrayList<>();
            while (rs.next()) {
                DatabaseService.User user = new DatabaseService.User();
                user.setName(rs.getString("name"));
                user.setAge(rs.getInt("age"));
                users.add(user);
            }

//            for (Launcher.User user: users){
//                System.out.println(user.toString());
//            }

            List<DatabaseService.User> correctUsers = new ArrayList<>();
            for (int i = 0; i <= testedUsers; ++i) {
                int age = i * i + 5;
                if (age % 10 != 4) {
                    String name = "User" + i;
                    correctUsers.add(new DatabaseService.User(name, age));
                }
            }

            //assertEquals(correctUsers.size(), users.size());

//            for (int i = 0; i < correctUsers.size(); ++i) {
//                assertEquals(correctUsers.get(i).toString(), users.get(i).toString());
//            }
        }
    }

    @Test
    public void testQueryById() throws Exception {
        DatabaseService dbs = new DatabaseService(DatabaseService.User.class);

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {
            Statement statement = connection.createStatement();
            //statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), age int, PRIMARY KEY(name))");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Gena', 200)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Lena', 300)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Pena', 400)");

            String key = "Lena";
            DatabaseService.User answer = dbs.queryById(key);
//            if (answer != null) {
//                System.out.println(answer.toString());
//            } else {
//                System.out.println("No =(");
//            }
            assertNotNull(answer);
            assertEquals("User{name='Lena', age=300}", answer.toString());
        }
    }

    @Test
    public void testQueryForAll() throws Exception {
        final int testedUsers = 150;
        DatabaseService dbs = new DatabaseService(DatabaseService.User.class);

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {

            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), age int, PRIMARY KEY(name))");
            List<DatabaseService.User> correctUsers = new ArrayList<>();

            for (int i = 0; i <= testedUsers; ++i) {
                int age = i * i + 5;
                String name = "User" + i;
                dbs.insert(new DatabaseService.User(name, age));
                correctUsers.add(new DatabaseService.User(name, age));
            }


            List<DatabaseService.User> users = dbs.queryForAll();
//            for (DatabaseService.User user: users){
//                System.out.println(user.toString());
//            }
//
            assertEquals(correctUsers.size(), users.size());
            for (int i = 0; i < users.size(); ++i) {
                assertEquals(correctUsers.get(i).toString(), users.get(i).toString());
            }
        }
    }

    @Test
    public void testUpdate() throws Exception {
        DatabaseService dbs = new DatabaseService(DatabaseService.User.class);

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), age int, PRIMARY KEY(name))");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Gena', 200)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Lena', 300)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Pena', 400)");

            DatabaseService.User toChange = new DatabaseService.User("Lena", 305);

            dbs.update(toChange);

            String key = "Lena";
            DatabaseService.User answer = dbs.queryById(key);

            assertNotNull(answer);
            assertEquals("User{name='Lena', age=305}", answer.toString());

        }
    }

    @Test
    public void testDelete() throws Exception {
        DatabaseService dbs = new DatabaseService(DatabaseService.User.class);

        try (Connection connection = DriverManager.getConnection("jdbc:h2:./miniORM2")) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), age int, PRIMARY KEY(name))");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Gena', 200)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Lena', 300)");
            statement.executeUpdate("INSERT INTO users (name, age) VALUES ('Pena', 400)");

            DatabaseService.User toChange = new DatabaseService.User("Lena", 300);

            dbs.delete(toChange);

            String key = "Lena";
            DatabaseService.User answer = dbs.queryById(key);

            // Должен вернуть null, то есть не найти ничего
            assertNull(answer);

        }
    }

}
