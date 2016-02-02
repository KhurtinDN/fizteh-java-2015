package ru.mipt.diht.students.miniorm;

import java.sql.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class DatabaseManager {
    private static final String DB_NAME = "jdbc:h2:./test";
    private final Connection connection;
    private final Statement statement;

    DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_NAME);
        statement = connection.createStatement();
    }

    void executeQuery(String query) throws SQLException {
        statement.execute(query);
    }

    ResultSet executeQueryWithResults(String query) throws SQLException {
        return statement.executeQuery(query);
    }
}
