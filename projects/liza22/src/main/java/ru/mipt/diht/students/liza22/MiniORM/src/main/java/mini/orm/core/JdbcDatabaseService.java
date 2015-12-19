package mini.orm.core;

import mini.orm.api.DatabaseService;
import mini.orm.core.db.ConnectionProvider;
import mini.orm.core.db.impl.JdbcConnectionProvider;
import mini.orm.core.model.ColumnDescriptor;
import mini.orm.core.model.TableDescriptor;
import mini.orm.core.utils.Constants;
import mini.orm.core.utils.QueryStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation ORM-service by JDBC base.
 * @param <T> - content type
 * @param <K> - primary key type
 */
public final class JdbcDatabaseService<T, K> implements DatabaseService<T, K> {
    private static final boolean LOGGING = true;

    private Class<T> entityType;
    private TableDescriptor tableDescriptor;

    private ConnectionProvider connectionProvider;

    public JdbcDatabaseService(Class<T> entityClass) {
        this.entityType = entityClass;
        this.tableDescriptor = TableDescriptor.fromEntityClass(entityClass);
        this.connectionProvider = new JdbcConnectionProvider();
    }

    public T queryById(K entityKey) {
        // get query with TABLE name and primary key name
        String query = QueryStorage.getSelectByPkQuery(tableDescriptor.getTableName(),
                tableDescriptor.getPrimaryKey().getColumnName());
        if (LOGGING) {
            System.out.println("Method 'queryById' query is: " + query);
        }
        // make DB connection, then PreparedStatement, run query and processing the ResultSet
        try (Connection conn = connectionProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, entityKey);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return buildEntityFromResult(result);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while perform queryById: " + e.getMessage());
            return null;
        }
    }

    public List<T> queryForAll() {
        String query = QueryStorage.getSelectAllQuery(tableDescriptor.getTableName());
        if (LOGGING) {
            System.out.println("Method 'queryForAll' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query)) {
            List<T> entities = new ArrayList<>();
            while (result.next()) {
                entities.add(buildEntityFromResult(result));
            }
            return entities;
        } catch (SQLException e) {
            System.err.println("Error while perform queryForAll: " + e.getMessage());
            return null;
        }
    }

    public void insert(T newEntity) {
        String query = QueryStorage.getInsertQuery(tableDescriptor.getTableName(), tableDescriptor.getColumns().size());
        if (LOGGING) {
            System.out.println("Method 'insert' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            for (ColumnDescriptor columnDescriptor : tableDescriptor.getColumns()) {
                int bindNum = tableDescriptor.getColumns().indexOf(columnDescriptor) + 1;
                Object columnValue = columnDescriptor.getColumnField().get(newEntity);
                stmt.setObject(bindNum, columnValue);
            }
            stmt.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error while perform insert: " + e.getMessage());
        }
    }

    public void update(T updatedEntity) {
        List<ColumnDescriptor> updateColumns = tableDescriptor.getColumnsExceptPK();
        String[] updateColumnNames = new String[updateColumns.size()];
        for (ColumnDescriptor cd : updateColumns) {
            updateColumnNames[updateColumns.indexOf(cd)] = cd.getColumnName();
        }
        String query = QueryStorage.getUpdateByPkQuery(tableDescriptor.getTableName(),
                updateColumnNames, tableDescriptor.getPrimaryKey().getColumnName());
        if (LOGGING) {
            System.out.println("Method 'update' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            int bindNum = 0;
            for (ColumnDescriptor columnDescriptor : updateColumns) {
                bindNum = updateColumns.indexOf(columnDescriptor) + 1;
                Object columnValue = columnDescriptor.getColumnField().get(updatedEntity);
                stmt.setObject(bindNum, columnValue);
            }
            stmt.setObject(bindNum + 1, tableDescriptor.getPrimaryKey().getColumnField().get(updatedEntity));
            stmt.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error while perform update: " + e.getMessage());
        }
    }

    public void delete(T deleteEntity) {
        ColumnDescriptor pkColumn = tableDescriptor.getPrimaryKey();
        String query = QueryStorage.getDeleteByPkQuery(tableDescriptor.getTableName(), pkColumn.getColumnName());
        if (LOGGING) {
            System.out.println("Method 'delete' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, pkColumn.getColumnField().get(deleteEntity));
            stmt.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error while perform delete: " + e.getMessage());
        }
    }

    public void createTable() {
        List<ColumnDescriptor> columns = tableDescriptor.getColumns();
        String[] columnNames = new String[columns.size()];
        String[] columnTypes = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            ColumnDescriptor columnDescriptor = columns.get(i);
            columnNames[i] = columnDescriptor.getColumnName();
            columnTypes[i] = Constants.JAVA_TO_SQL_TYPES.get(columnDescriptor.getColumnType());
        }
        String query = QueryStorage.getCreateTableQuery(tableDescriptor.getTableName(),
                columnNames, columnTypes, tableDescriptor.getPrimaryKey().getColumnName());
        if (LOGGING) {
            System.out.println("Method 'createTable' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error while perform createTable: " + e.getMessage());
        }
    }

    public void dropTable() {
        String query = QueryStorage.getDropTableQuery(tableDescriptor.getTableName());
        if (LOGGING) {
            System.out.println("Method 'dropTable' query is: " + query);
        }
        try (Connection conn = connectionProvider.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error while perform dropTable: " + e.getMessage());
        }
    }

    private T buildEntityFromResult(ResultSet resultSet) {
        try {
            T entity = entityType.newInstance();
            for (ColumnDescriptor column : tableDescriptor.getColumns()) {
                Object columnValue = resultSet.getObject(column.getColumnName());
                column.getColumnField().set(entity, columnValue);
            }
            return entity;
        } catch (Exception e) {
            System.err.println("Entity class = " + entityType + " is incorrect: " + e.getMessage());
            return null;
        }
    }
}
