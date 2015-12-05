package ru.mipt.diht.students.feezboom.Lessons.DataBase;

import org.h2.jdbcx.JdbcDataSource;
import javax.sql.DataSource;
import java.sql.*;

/**
 * * Created by avk on 05.12.15.
 **/
public class Lesson5_12_0 {


    public static void main(String[] args) throws SQLException {
        DataSource dataSource = createDataSource();

//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lesson?a=4&b=4");
        try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson")) {
            Statement createTableStatement = connection.createStatement();
            int updates = createTableStatement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name VARCHAR(255) not null, age int not null)");
            System.out.println("Updates : " + updates);

//            if (updates > 0) {
                Statement insertStatement = connection.createStatement();
                int i1 = insertStatement.executeUpdate("INSERT INTO users VALUES('John', 18);");
                int i2 = insertStatement.executeUpdate("INSERT INTO users VALUES('Mike', 20);");
                System.out.println("i1" + i1);
                System.out.println("i2" + i2);
//            }

            Statement selectStatement = connection.createStatement();
            String table = "users; drop table users;";
            selectStatement.execute("SELECT * FROM " + table);
            //boolean success = selectStatement.execute("SELECT * FROM users");
            ResultSet resultSet = selectStatement.getResultSet();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " : " + resultSet.getInt("age"));
            }
        }
    }

    private static javax.sql.DataSource createDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:/tmp/lesson");
        return jdbcDataSource;
    }
}
