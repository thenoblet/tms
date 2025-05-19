package tms.util;

import tms.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Singleton class for managing database connections.
 * Provides thread-safe access to a single database connection instance
 * with lazy initialisation and automatic reconnection when closed.
 */
public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static DatabaseConnection instance;
    private final Connection connection;

    /**
     * Private constructor to enforce singleton pattern.
     * Initialises database connection using configuration from DatabaseConfig.
     *
     * @throws SQLException if database connection fails or driver not found
     */
    private DatabaseConnection() throws SQLException {
        try {
            LOGGER.fine("Initializing new database connection");

            Class.forName(DatabaseConfig.getDbDriver());
            this.connection = DriverManager.getConnection(
                    DatabaseConfig.getDbUrl(),
                    DatabaseConfig.getDbUsername(),
                    DatabaseConfig.getDbPassword()
            );
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found: " + DatabaseConfig.getDbDriver(), ex);
            throw new SQLException("Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", ex);
            throw new SQLException("Failed to establish database connection", ex);
        }
    }

    /**
     * Gets the singleton instance of DatabaseConnection.
     * Creates new connection if none exists or previous connection was closed.
     *
     * @return DatabaseConnection instance
     * @throws SQLException if database connection fails
     */
    public static DatabaseConnection getInstance() throws SQLException {
        try {
            if (instance == null) {
                LOGGER.fine("Creating new DatabaseConnection instance");
                instance = new DatabaseConnection();
            } else if (instance.getConnection().isClosed()) {
                LOGGER.fine("Existing connection closed - creating new instance");
                instance = new DatabaseConnection();
            }
            return instance;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", ex);
            throw new SQLException("Failed to establish database connection", ex);
        }
    }

    /**
     * Gets the active database connection.
     *
     * @return current Connection object
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            LOGGER.warning("Attempted to get closed/invalid connection");
        }

        LOGGER.finest("Returning active database connection");
        return connection;
    }

    /**
     * Closes the database connection if it's open.
     * Silently handles any errors that occur during closing.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

