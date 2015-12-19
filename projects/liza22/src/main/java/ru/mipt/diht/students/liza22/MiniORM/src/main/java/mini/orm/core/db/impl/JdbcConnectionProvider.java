package mini.orm.core.db.impl;

import mini.orm.core.db.ConnectionProvider;
import mini.orm.core.db.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionProvider on the JDBC base.
 * Using DB config with parameters to make Connection.
 */
public class JdbcConnectionProvider implements ConnectionProvider {
    private DbConfig dbConfig;

    public JdbcConnectionProvider() {
        this.dbConfig = new DbConfig();
    }

    public final Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }
}
