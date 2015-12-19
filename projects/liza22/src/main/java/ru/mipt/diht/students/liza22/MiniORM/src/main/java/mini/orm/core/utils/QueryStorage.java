package mini.orm.core.utils;

import java.text.MessageFormat;

/**
 * this class contains SQL queries teplates.
 */
public class QueryStorage {

    private static final String SELECT_BY_PK_QUERY =
            "SELECT * FROM {0} WHERE {1} = ?";
    public static String getSelectByPkQuery(String tableName, String pkColumnName) {
        return MessageFormat.format(SELECT_BY_PK_QUERY, tableName, pkColumnName);
    }

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM {0}";
    public static String getSelectAllQuery(String tableName) {
        return MessageFormat.format(SELECT_ALL_QUERY, tableName);
    }

    private static final String INSERT_QUERY =
            "INSERT INTO {0} VALUES({1})";
    public static String getInsertQuery(String tableName, int countOfColumns) {
        StringBuilder valueBinds = new StringBuilder();
        for (int i = 0; i < countOfColumns; i++) {
            valueBinds.append("?");
            if (i != countOfColumns - 1) {
                valueBinds.append(", ");
            }
        }
        return MessageFormat.format(INSERT_QUERY, tableName, valueBinds);
    }

    private static final String UPDATE_BY_PK_QUERY =
            "UPDATE {0} SET {1} WHERE {2} = ?";
    public static String getUpdateByPkQuery(String tableName, String[] columns, String pkColumnName) {
        StringBuilder valueBinds = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            valueBinds.append(columns[i]).append("=?");
            if (i != columns.length - 1) {
                valueBinds.append(", ");
            }
        }
        return MessageFormat.format(UPDATE_BY_PK_QUERY, tableName, valueBinds, pkColumnName);
    }

    private static final String DELETE_BY_PK_QUERY =
            "DELETE FROM {0} WHERE {1} = ?";
    public static String getDeleteByPkQuery(String tableName, String pkColumnName) {
        return MessageFormat.format(DELETE_BY_PK_QUERY, tableName, pkColumnName);
    }

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE {0} ({1} PRIMARY KEY ( {2} ))";
    public static String getCreateTableQuery(String tableName, String[] columnNames, 
		String[] columnTypes, String pkColumnName) {
        StringBuilder columnsInfo = new StringBuilder();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            String columnType = columnTypes[i];
            columnsInfo.append(columnName).append(" ").append(columnType).append(", ");
        }
        return MessageFormat.format(CREATE_TABLE_QUERY, tableName, columnsInfo, pkColumnName);
    }

    private static final String DROP_TABLE_QUERY =
            "DROP TABLE {0}";
    public static String getDropTableQuery(String tableName) {
        return MessageFormat.format(DROP_TABLE_QUERY, tableName);
    }
}