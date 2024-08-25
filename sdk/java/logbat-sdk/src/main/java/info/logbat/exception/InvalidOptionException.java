package info.logbat.exception;

/**
 * Exception thrown when an invalid option is provided.
 */
public class InvalidOptionException extends Exception {

    public InvalidOptionException(String message) {
        super(message);
    }
}
