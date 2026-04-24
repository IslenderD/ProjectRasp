package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Configures the configuration of the database on the raspberry.
 */
public class DatabaseHandler {

    /// Name of the database driver.
    public static final String driverName = "org.mariadb.jdbc.Driver";
    /// HikariCP connection manages a pool of databases connections.
    static HikariDataSource ds;
    /// Schema being used.
    private static String currentSchema = envOrDefault("BIGSLICK_DB_SCHEMA", "my_db");
    /// Name of database being connected to.
    private static String db = envOrDefault("BIGSLICK_DB_NAME", "my_db");
    /// Username (for authentification of database).
    private static String dbuser = envOrDefault("BIGSLICK_DB_USER", "BIGSLICK");
    /// Password (for authentification of database).
    private static String passwd = envOrDefault("BIGSLICK_DB_PASSWORD", "change-me");
    /// Base JDBC connection URL.
    private static String url = envOrDefault("BIGSLICK_DB_URL", "jdbc:mariadb://localhost:3306/" + db);
    /// Transaction isolation level for database connections.
    private static String isolationLevel = "TRANSACTION_REPEATABLE_READ";
    /// Maximum number of connections in the HikariCP pool.
    private static final int hikari_pool_size = 10; //placeholder quantity
    /// Determines if database auto-commit mode is enabled.
    private static final boolean autocommit = false;

    // Sets the configuration of the Hikari connection.
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driverName);
            config.setJdbcUrl(url);
            config.setUsername(dbuser);
            config.setPassword(passwd);
            config.setAutoCommit(autocommit);
            config.setTransactionIsolation(isolationLevel);
            config.setMaximumPoolSize(hikari_pool_size);
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String envOrDefault(String key, String fallback) {
        return Optional.ofNullable(System.getenv(key)).filter(value -> !value.isBlank()).orElse(fallback);
    }

    /**
     * Gets current connection.
     *
     * @return connection
     * @throws SQLException Exception if the connection does not exist
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = ds.getConnection();
        conn.setSchema(currentSchema); //in future currentSchema
        return conn;
    }


}