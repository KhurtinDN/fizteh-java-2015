/**
 * Created by Владимир on 19.12.2015.
 */

import com.google.common.base.CaseFormat;
import org.h2.jdbcx.JdbcConnectionPool;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import static RightNameResolver.isGood;

public class DatabaseService<T> implements Closeable
{

    private int pkIndex = -1;
    private final String connectionName;
    private final String username;
    private final String password;
    private Class<T> classs;
    private JdbcConnectionPool pool;
    private String tableName;
    private Field[] tableFields;

    void init() throws IllegalArgumentException, IOException {
        if (!classs.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("no @Table annotation");
        }
        tableName = classs.getAnnotation(Table.class).name();
        if (tableName.equals("")) {
            tableName = convert(classs.getSimpleName());
        }
        if (!isGood(tableName)) {
            throw new IllegalArgumentException("Bad table name");
        }
        Set<String> names = new HashSet<>();
        List<Field> tableFieldsList = new ArrayList<>();
        for (Field f : classs.getDeclaredFields()) {
            if (f.isAnnotationPresent(Column.class)) {
                String name = getColumnName(f);
                names.add(name);
                if (!isGood(name)) {
                    throw new IllegalArgumentException("Bad column name");
                }
                f.setAccessible(true);
                tableFieldsList.add(f);
                if (f.isAnnotationPresent(PrimaryKey.class)) {
                    if (pkIndex == -1) {
                        pkIndex = tableFieldsList.size() - 1;
                    } else {
                        throw new
                                IllegalArgumentException("Several @PrimaryKey");
                    }
                }
            } else if (f.isAnnotationPresent(PrimaryKey.class)) {
                throw new
                        IllegalArgumentException("@PrimaryKey without @Column");
            }
        }
        if (names.size() != tableFieldsList.size()) {
            throw new IllegalArgumentException("Duplicate columns");
        }
        tableFields = new Field[tableFieldsList.size()];
        tableFields = tableFieldsList.toArray(tableFields);
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No H2 driver found");
        }
        pool = JdbcConnectionPool.create(connectionName, username, password);
    }

    static String convert(String name) {
        if ('a' <= name.charAt(0) && name.charAt(0) <= 'z') {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    String getColumnName(Field f) {
        String name = f.getAnnotation(Column.class).name();
        if (name.equals("")) {
            return convert(f.getName());
        }
        return name;
    }

    DatabaseService(Class<T> newClazz, String properties) throws IOException {
        Properties credits = new Properties();
        try (InputStream inputStream
                     = this.getClass().getResourceAsStream(properties)) {
            credits.load(inputStream);
        }
        connectionName = credits.getProperty("connection_name");
        username = credits.getProperty("username");
        password = credits.getProperty("password");
        classs = newClazz;
        init();
    }

    DatabaseService(Class<T> newClazz) throws IOException {
        this(newClazz, "/h2.properties");
    }

    void createTable() throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("(");
        for (int i = 0; i < tableFields.length; ++i) {
            if (i != 0) {
                queryBuilder.append(", ");
            }
            queryBuilder.append(getColumnName(tableFields[i])).append(" ")
                    .append(H2StringsResolver.resolve(tableFields[i].getType()));
            if (i == pkIndex)
                queryBuilder.append(" PRIMARY KEY");
        }
        queryBuilder.append(")");
        try (Connection conn = pool.getConnection()) {
            conn.createStatement().execute(queryBuilder.toString());
        }
    }

    void dropTable() throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DROP TABLE IF EXISTS ")
                .append(tableName);
        try (Connection conn = pool.getConnection()) {
            conn.createStatement().execute(queryBuilder.toString());
        }
    }

    public void insert(T record) throws SQLException, IllegalAccessException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < tableFields.length; ++i) {
            if (i != 0) {
                queryBuilder.append(", ");
            }
            queryBuilder.append(getColumnName(tableFields[i])).append(" ");
        }
        queryBuilder.append(") VALUES (");
        for (int i = 0; i < tableFields.length; ++i) {
            if (i != 0) {
                queryBuilder.append(", ");
            }
            queryBuilder.append("?");
        }
        queryBuilder.append(")");

        try (Connection conn = pool.getConnection()) {
            PreparedStatement statement
                    = conn.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < tableFields.length; ++i) {
                statement.setObject(i + 1, tableFields[i].get(record));
            }
            statement.execute();
        }
    }

    public void delete(T record) throws IllegalArgumentException,
            IllegalAccessException, SQLException {
        if (pkIndex == -1)
            throw new IllegalArgumentException("NO @PrimaryKey");
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(tableName)
                .append(" WHERE ").append(tableFields[pkIndex].getName())
                .append(" = ?");
        try (Connection conn = pool.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());
            statement.setObject(1, tableFields[pkIndex].get(record));
            statement.execute();
        }
    }

    public void update(T record) throws IllegalArgumentException,
            SQLException, IllegalAccessException {
        if (pkIndex == -1) {
            throw new IllegalArgumentException("NO @PrimaryKey");
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < tableFields.length; ++i) {
            if (i != 0)
                queryBuilder.append(", ");
            queryBuilder.append(getColumnName(tableFields[i])).append(" = ?");
        }
        queryBuilder.append(" WHERE ").append(getColumnName(tableFields[pkIndex]))
                .append(" = ?");

        try (Connection conn = pool.getConnection()) {
            PreparedStatement statement
                    = conn.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < tableFields.length; ++i)
                statement.setObject(i + 1, tableFields[i].get(record));
            statement.setObject(tableFields.length + 1, tableFields[pkIndex].get(record));
            statement.execute();
        }
    }

    public <K> T queryById(K key) throws IllegalArgumentException,
            SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ").append(tableName)
                .append(" WHERE ").append(tableFields[pkIndex].getName())
                .append(" = ?");
        try (Connection conn = pool.getConnection()) {
            PreparedStatement statement
                    = conn.prepareStatement(queryBuilder.toString());
            statement.setString(1, key.toString());

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                T record = classs.newInstance();
                for (int i = 0; i < tableFields.length; ++i) {
                    if (tableFields[i].getClass()
                            .isAssignableFrom(Number.class)) {
                        Long val = rs.getLong(i + 1);
                        tableFields[i].set(record, val);
                    } else if (tableFields[i].getType() != String.class) {
                        tableFields[i].set(record, rs.getObject(i + 1));
                    } else {
                        Clob data = rs.getClob(i + 1);
                        tableFields[i].set(record,
                                data.getSubString(1, (int) data.length()));
                    }
                }
                return record;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("wrong class");
            }
        }

    }

    public List<T> queryForAll() throws SQLException {
        List<T> result = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ").append(tableName);
        try (Connection conn = pool.getConnection()) {
            try (ResultSet rs = conn.createStatement()
                    .executeQuery(queryBuilder.toString())) {
                while (rs.next()) {
                    T record = classs.newInstance();
                    for (int i = 0; i < tableFields.length; ++i) {
                        if (tableFields[i].getClass()
                                .isAssignableFrom(Number.class)) {
                            Long val = rs.getLong(i + 1);
                            tableFields[i].set(record, val);
                        } else if (tableFields[i].getType() != String.class) {
                            tableFields[i].set(record, rs.getObject(i + 1));
                        } else {
                            Clob data = rs.getClob(i + 1);
                            tableFields[i].set(record,
                                    data.getSubString(1, (int) data.length()));
                        }
                    }
                    result.add(record);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("wrong class");
            }
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (pool != null) {
            pool.dispose();
        }
    }
}
