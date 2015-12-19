package ru.fizteh.fivt.students.chipak.miniorm;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import org.h2.jdbcx.JdbcConnectionPool;

public class DatabaseService<T> {
    private Class<T> tableClass;
    private String tableName;
    private Connection dbConnection;
    private List<TypedColumn> columns;
    private TypedColumn primaryKeyColumn;

    private static final String STANDARD_DATABASE_PATH = "./mamaevadb";

    private enum DataType {
        INTEGER("INT", Boolean.class, Byte.class, Short.class, Integer.class, Long.class),
        DOUBLE("DOUBLE", Float.class, Double.class),
        TEXT("VARCHAR(255)", String.class);

        private final String nameSQL;
        private final Class<?>[] examples;

        DataType(String sqllikeName, Class<?>... variety) {
            nameSQL = sqllikeName;
            examples = variety;
        }

        private static DataType valueOf(Class<?> typeClass) throws DatabaseServiceException {
            for (DataType type : values()) {
                for (Class<?> testClass : type.examples) {
                    if (typeClass.equals(testClass)) {
                        return type;
                    }
                }
            }
            throw new DatabaseServiceException("This type is not supported"
                    + typeClass.getSimpleName());
        }
    }

    private final class TypedColumn {
        private DataType type;
        private String name;
        private Field field;

        private TypedColumn(Field realField) throws DatabaseServiceException {
            field = realField;
            field.setAccessible(true);
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                throw new DatabaseServiceException("annotate the field");
            }
            name = columnAnnotation.name();
            if (name.equals("")) {
                name = toSnakeCase(field.getName());
            }
            type = DataType.valueOf(field.getType());
        }
    }

    private static String toSnakeCase(String camelCase) {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < camelCase.length(); ++i) {
            if (camelCase.charAt(i) >= 'A' && camelCase.charAt(i) <= 'Z') {
                if (i > 0 && camelCase.charAt(i - 1) >= 'a' && camelCase.charAt(i - 1) <= 'z') {
                    nameBuilder.append('_');
                }
                nameBuilder.append((char) (camelCase.charAt(i) - 'A' + 'a'));
            } else {
                nameBuilder.append(camelCase.charAt(i));
            }
        }
        return nameBuilder.toString();
    }

    private String createColumnsPattern(Function<TypedColumn, String> convert) {
        StringBuilder values = new StringBuilder();
        for (TypedColumn column : columns) {
            values.append(convert.apply(column))
                    .append(',');
        }
        values.deleteCharAt(values.length() - 1);
        return values.toString();
    }

    private void initDatabaseTable(Class<T> annotatedTableClass) throws DatabaseServiceException {
        tableClass = annotatedTableClass;
        Table tableAnnotation = tableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new DatabaseServiceException("annotate the table class");
        }
        tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = toSnakeCase(tableClass.getSimpleName());
        }
        Field[] allFields = tableClass.getDeclaredFields();
        columns = new ArrayList<>();
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(new TypedColumn(field));
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (primaryKeyColumn != null) {
                        throw new DatabaseServiceException("please enter one prim key");
                    }
                    primaryKeyColumn = columns.get(columns.size() - 1);
                }
            }
        }
    }

    private void initConnection(String databasePath) throws DatabaseServiceException {
        try {
            dbConnection = DriverManager.getConnection("jdbc:h2:" + databasePath, "sa", "");
        } catch (SQLException e) {
            throw new DatabaseServiceException("Connection not found: " + e.getMessage(), e);
        }
    }

    public DatabaseService(Class<T> annotatedTableClass) throws DatabaseServiceException {
        initDatabaseTable(annotatedTableClass);
        initConnection(STANDARD_DATABASE_PATH);
    }

    public final void createTable() throws DatabaseServiceException {
        StringBuilder columnsString = new StringBuilder();
        for (TypedColumn column : columns) {
            columnsString.append(column.name)
                    .append(' ')
                    .append(column.type.nameSQL);
            if (column.field.isAnnotationPresent(PrimaryKey.class)) {
                columnsString.append(" PRIMARY KEY CAN'T BE NULL");
            }
            columnsString.append(',');
        }
        columnsString.deleteCharAt(columnsString.length() - 1);
        try {
            dbConnection.createStatement().execute("CREATE TABLE " + tableName
                    + "(" + columnsString.toString() + ")");
        } catch (SQLException e) {
            throw new DatabaseServiceException("can't create: " + e.getMessage(), e);
        }
    }


    public final void dropTable() throws DatabaseServiceException {
        try {
            dbConnection.createStatement().execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new DatabaseServiceException("can't drop table: " + e.getMessage(), e);
        }
    }


    public final void insert(T row) throws DatabaseServiceException {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO " + tableName
                    + " VALUES (" + createColumnsPattern(c -> "?") + ")");
            for (int i = 0; i < columns.size(); ++i) {
                statement.setString(i + 1, String.valueOf(columns.get(i).field.get(row)));
            }
            statement.execute();
        } catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
            throw new DatabaseServiceException("can't insert: " + e.getMessage());
        }
    }


    public final void update(T row) throws DatabaseServiceException {
        if (primaryKeyColumn == null) {
            throw new DatabaseServiceException("no prim key");
        }
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE " + tableName
                    + " SET " + createColumnsPattern(c -> c.name + "=?") + " WHERE " + primaryKeyColumn.name + "=?");
            for (int i = 0; i < columns.size(); ++i) {
                statement.setString(i + 1, String.valueOf(columns.get(i).field.get(row)));
            }
            statement.setString(columns.size() + 1, primaryKeyColumn.field.get(row).toString());
            statement.execute();
        } catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
            throw new DatabaseServiceException("Can't update: " + e.getMessage());
        }
    }


    public final <K> void deleteByKey(K key) throws DatabaseServiceException {
        if (primaryKeyColumn == null) {
            throw new DatabaseServiceException("no prim key");
        }
        try {
            PreparedStatement statement = dbConnection.prepareStatement("DELETE FROM " + tableName
                    + " WHERE " + primaryKeyColumn.name + "=?");
            statement.setString(1, key.toString());
            statement.execute();
        } catch (SQLException | IllegalArgumentException e) {
            throw new DatabaseServiceException("Can't delete" + e.getMessage());
        }
    }


    public final void delete(T row) throws DatabaseServiceException {
        try {
            deleteByKey(primaryKeyColumn.field.get(row).toString());
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException("Can't delete: " + e.getMessage());
        }
    }

    private List<T> getRealClasses(ResultSet result) throws IllegalArgumentException, IllegalAccessException,
            SQLException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException {
        List<T> output = new ArrayList<>();
        while (result.next()) {
            T row = tableClass.newInstance();
            for (int i = 0; i < columns.size(); ++i) {
                String value = result.getString(i + 1);
                if (columns.get(i).field.getType().equals(String.class)) {
                    columns.get(i).field.set(row, value);
                } else {
                    columns.get(i).field.set(row, columns.get(i).field.getType().getMethod("valueOf", String.class)
                            .invoke(null, value));
                }
            }
            output.add(row);
        }
        result.close();
        return output;
    }

    public final <K> T queryById(K key) throws DatabaseServiceException {
        if (primaryKeyColumn == null) {
            throw new DatabaseServiceException("need prim key");
        }
        if (!primaryKeyColumn.field.getType().isInstance(key)) {
            throw new DatabaseServiceException("key type is not type of primary key");
        }

        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT " + createColumnsPattern(c -> c.name)
                    + " FROM " + tableName + " WHERE " + primaryKeyColumn.name + "=?");
            statement.setString(1, key.toString());
            List<T> result = getRealClasses(statement.executeQuery());
            if (result.isEmpty()) {
                return null;
            }
            return result.get(0);
        } catch (SQLException | IllegalArgumentException | IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new DatabaseServiceException("Can't get the instantiate the row: " + e.getMessage(), e);
        }
    }


    public final List<T> queryForAll() throws DatabaseServiceException {
        try {
            return getRealClasses(dbConnection.prepareStatement("SELECT " + createColumnsPattern(c -> c.name)
                    + " FROM " + tableName).executeQuery());
        } catch (SQLException | IllegalArgumentException | IllegalAccessException
                | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new DatabaseServiceException("Can't get or instantiate the row: " + e.getMessage(), e);
        }
    }

    @Override
    protected final void finalize() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            System.err.println("can not close connection: " + e.getMessage());
        }
    }
}
