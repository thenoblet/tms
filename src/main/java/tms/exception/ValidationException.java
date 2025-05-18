package tms.exception;

/**
 * Custom runtime exception for validation failures in the application.
 * Thrown when business rules or data validation constraints are violated.
 * Extends {@code RuntimeException} to avoid mandatory exception handling
 * while clearly indicating validation-specific errors.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new validation exception with the specified error message.
     *
     * @param message the detail message describing the validation failure.
     *        Should clearly indicate which validation rule was violated.
     */
    public ValidationException(String message) {
        super(message);
    }
}