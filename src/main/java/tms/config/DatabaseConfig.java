package tms.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides static access to database configuration properties loaded from
 * the application.properties file. This class initializes the properties
 * during class loading and makes them available through static getter methods.
 * <p>
 * The configuration file must contain the following properties:
 * <ul>
 *   <li>db.url - The JDBC connection URL</li>
 *   <li>db.username - The database username</li>
 *   <li>db.password - The database password</li>
 *   <li>db.driver - The JDBC driver class name</li>
 * </ul>
 *
 * @throws RuntimeException if:
 *                         <ul>
 *                           <li>The application.properties file cannot be found</li>
 *                           <li>There is an error reading the properties file</li>
 *                         </ul>
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading configuration", ex);
        }
    }

    /**
     * Retrieves the database connection URL from the configuration.
     *
     * @return The JDBC connection URL as specified in the properties file
     */
    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Retrieves the database username from the configuration.
     *
     * @return The database username as specified in the properties file
     */
    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Retrieves the database password from the configuration.
     *
     * @return The database password as specified in the properties file
     */
    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Retrieves the JDBC driver class name from the configuration.
     *
     * @return The fully qualified JDBC driver class name as specified in the properties file
     */
    public static String getDbDriver() {
        return properties.getProperty("db.driver");
    }
}