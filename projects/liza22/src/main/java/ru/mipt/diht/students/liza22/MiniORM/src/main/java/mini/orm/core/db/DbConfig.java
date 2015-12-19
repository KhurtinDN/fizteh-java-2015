package mini.orm.core.db;

import mini.orm.core.utils.Constants;

import java.io.*;

/*
 * config file with parameters DB connection's.
 * will be use by DriverManager to getting DB Connection.
 */
public class DbConfig {
    /*
     * keys names from property file.
     */
    private static final String JDBC_DRIVER_PROP_NAME   = "db.jdbc.driver";
    private static final String DB_URL_PROP_NAME        = "db.url";
    private static final String DB_USER_PROP_NAME       = "db.user";
    private static final String DB_PASSWORD_PROP_NAME   = "db.password";

    private String jdbcDriver;
    private String url;
    private String username;
    private String password;

    public DbConfig() {
        init();
        // load Jdbc Driver class
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver Class = '" + jdbcDriver + "' not found", e);
        }
    }

    private void init() {
        File cfgFile = new File(Constants.DB_CONFIG_FILE);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile)))) {
            while (in.ready()) {
                String line = in.readLine();
                int indexOfDelimiter = line.indexOf('=');
                if (indexOfDelimiter == -1) {
                    System.err.println("Incorrect line in DB configuration = '" + line + "'");
                    continue;
                }
                String propName = line.substring(0, indexOfDelimiter);
                String propValue = line.substring(indexOfDelimiter + 1, line.length());
                switch (propName) {
                    case JDBC_DRIVER_PROP_NAME:
                        jdbcDriver = propValue;
                        break;
                    case DB_URL_PROP_NAME:
                        url = propValue;
                        break;
                    case DB_USER_PROP_NAME:
                        username = propValue;
                        break;
                    case DB_PASSWORD_PROP_NAME:
                        password = propValue;
                        break;
                    default:
                        System.err.println("Property '" + propName + "' not recognized");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("DB config file by path = \"" + cfgFile.getAbsolutePath() + "\" not found");
        } catch (IOException e) {
            System.err.println("Problem with reading DB config file: " + e.getMessage());
        }

        validate();
    }

    private void validate() {
        if (jdbcDriver == null
                || url == null
                || username == null
                || password == null) {
            throw new IllegalStateException("DB configuration file is incorrect");
        }
    }

    public final String getJdbcDriver() {
        return jdbcDriver;
    }

    public final String getUrl() {
        return url;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    @Override
    public final String toString() {
        return "DbConfig{"
                + "jdbcDriver='" + jdbcDriver + '\''
                + ", url='" + url + '\''
                + ", username='" + username + '\''
                + ", password='" + password + '\''
                + '}';
    }
}
