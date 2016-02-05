package ru.mipt.diht.students.miniorm;

import java.sql.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class DatabaseManagerImpl implements DatabaseManager {
    private static final String DB_NAME = "jdbc:h2:./test";
    private static final String DRIVER_NAME = "org.h2.Driver";
    private final Connection connection;
    private final Statement statement;

    public DatabaseManagerImpl() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_NAME);
        connection = DriverManager.getConnection(DB_NAME);
        statement = connection.createStatement();
    }

    @Override
    public void executeQuery(String query) throws SQLException {
        statement.execute(query);
    }

    @Override
    public ResultSet executeQueryWithResults(String query) throws SQLException {
        return statement.executeQuery(query);
    }

    public void close() throws SQLException {
        connection.close();
    }
}
