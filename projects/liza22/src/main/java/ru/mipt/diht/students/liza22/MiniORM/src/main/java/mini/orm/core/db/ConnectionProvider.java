package mini.orm.core.db;

import java.sql.Connection;
import java.sql.SQLException;

/*
 * provide Connection to DB.
 */
public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
}
