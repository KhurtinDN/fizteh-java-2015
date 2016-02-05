package ru.mipt.diht.students.miniorm;

import javafx.util.Pair;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mikhail on 28.01.16.
 */
public class DatabaseService<T> {
    private final Class<T> type;
    private final Column primaryKey;
    private final List<Column> columns;
    private final String tableName;
    private final DatabaseManager databaseManager;


    public DatabaseService(Class<T> type, DatabaseManager databaseManager) throws DatabaseServiceException,
            SQLException {
        this.type = type;
        DatabaseServiceAnnotations databaseServiceAnnotations = new DatabaseServiceAnnotations(type);
        Pair<List<Column>, Column> initData = databaseServiceAnnotations.parseType();
        primaryKey = initData.getValue();
        columns = initData.getKey();
        tableName = databaseServiceAnnotations.getTableName();
        this.databaseManager = databaseManager;
    }
    public T queryById(Object primaryKey) throws SQLException, InstantiationException, IllegalAccessException, DatabaseServiceException {
        String query = "SELECT * FROM " + tableName + whereForPrimaryKey(primaryKey);
        ResultSet resultSet = databaseManager.executeQueryWithResults(query);
        resultSet.next();

        return constructObject(resultSet);
    }

    public List<T> queryForAll() throws SQLException, InstantiationException, IllegalAccessException {
        String query = "SELECT * FROM " + tableName;
        LinkedList<T> result = new LinkedList<>();
        ResultSet resultSet = databaseManager.executeQueryWithResults(query);

        while (resultSet.next()) {
            result.add(constructObject(resultSet));
        }

        return result;
    }

    public void insert(T item) throws IllegalAccessException, SQLException, DatabaseServiceException {
        if(primaryKey.getField().get(item) == null)
            throw new DatabaseServiceException("Primary key can't be null.");

        String query = "INSERT INTO " + tableName + " (";

        for (Column column : columns) {
            query += column.getName() + ", ";
        }

        query = StringProcessor.erase2LastLetters(query);
        query += ") VALUES (";

        for (Column column : columns) {
            query += column.toSQL(column.getField().get(item)) + ", ";
        }

        query = StringProcessor.erase2LastLetters(query);
        query += ")";

        databaseManager.executeQuery(query);
    }

    public void update(T item) throws DatabaseServiceException, IllegalAccessException, SQLException {
        if(primaryKey.getField().get(item) == null)
            throw new DatabaseServiceException("Primary key can't be null.");

        String query = "UPDATE " + tableName + " SET ";

        for (Column column : columns) {
            query += column.getName() + " = " + column.toSQL(column.getField().get(item)) + ", ";
        }
        query = StringProcessor.erase2LastLetters(query);

        query += whereForPrimaryKey(primaryKey.getField().get(item));

        databaseManager.executeQuery(query);
    }

    public void delete(Object primaryKey) throws DatabaseServiceException, SQLException {
        String query = "DELETE FROM " + tableName + whereForPrimaryKey(primaryKey);

        databaseManager.executeQuery(query);
    }

    public void createTable() throws SQLException, DatabaseServiceException {
        if (columns.isEmpty()) {
            throw new DatabaseServiceException("Nothing to store.");
        }

        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

        for (Column column : columns) {
            query += column.getName() + " " + column.getType();

            if (column == primaryKey) {
                query += " PRIMARY KEY";
            }

            query += ", ";
        }

        query = StringProcessor.erase2LastLetters(query);
        query += ")";

        databaseManager.executeQuery(query);
    }

    public void dropTable() throws SQLException {
        databaseManager.executeQuery("DROP TABLE IF EXISTS " + tableName);
    }

    private T constructObject(ResultSet current) throws IllegalAccessException, InstantiationException, SQLException {
        T result = type.newInstance();
        for (Column column : columns) {
            Field field = column.getField();
            String columnName = column.getName();

            switch (column.getType()) {
                case INT:
                    field.set(result, current.getInt(columnName));
                    break;
                case BOOLEAN:
                    field.set(result, current.getBoolean(columnName));
                    break;
                case DOUBLE:
                    field.set(result, current.getDouble(columnName));
                    break;
                case TIME:
                    field.set(result, current.getTime(columnName));
                    break;
                case DATE:
                    field.set(result, current.getDate(columnName));
                    break;
                case VARCHAR:
                    field.set(result, current.getString(columnName));
                    break;
            }

            if (current.wasNull()) {
                field.set(result, null);
            }
        }

        return result;
    }

    private String whereForPrimaryKey(Object primaryKey) throws DatabaseServiceException {
        if (primaryKey == null || !this.primaryKey.checkIfSuits(primaryKey)) {
            throw new DatabaseServiceException("delete/queryById accept only nonnull primary keys.");
        }

        return " WHERE " + this.primaryKey.getName() + " = " + this.primaryKey.toSQL(primaryKey);
    }
}
