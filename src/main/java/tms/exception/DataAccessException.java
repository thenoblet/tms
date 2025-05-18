package tms.exception;

/**
 * Custom runtime exception for data access layer operations.
 * Indicates an error occurred while interacting with the database or data source.
 * Extends RuntimeException to avoid mandatory exception handling in calling code.
 */
public class DataAccessException extends RuntimeException {

    /**
     * Constructs a new DataAccessException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying exception that caused this error
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}