package ru.mipt.diht.students.miniorm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mikhail on 05.02.16.
 */
public interface DatabaseManager {
    void executeQuery(String query) throws SQLException;

    ResultSet executeQueryWithResults(String query) throws SQLException;

    void close() throws SQLException;
}
