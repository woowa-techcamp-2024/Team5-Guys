package info.logbat.domain.logbat;

import info.logbat.exception.InvalidOptionException;
import java.util.UUID;

/**
 * AppKey represents a unique identifier for an application.
 * <p>
 * AppKey is a value object that holds a UUID string as the unique identifier for an application. It
 * used to identify an application in the LogBat system.
 * </p>
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class AppKey {

    private final String value;

    /**
     * Constructs an AppKey object with the given UUID string.
     *
     * @param value the UUID string to be used as the AppKey
     * @throws InvalidOptionException if the given UUID string is null, empty, or not a valid UUID
     *                                string
     */
    public AppKey(String value) throws InvalidOptionException {
        validateAppKey(value);
        validateUUIDString(value);
        this.value = value;
    }

    /**
     * Returns the UUID string value of the AppKey.
     *
     * @return the UUID string value of the AppKey
     */
    public String getValue() {
        return value;
    }

    private void validateAppKey(String value) throws InvalidOptionException {
        if (value == null || value.isBlank()) {
            throw new InvalidOptionException("AppKey must not be null or empty.");
        }
    }

    private void validateUUIDString(String value) throws InvalidOptionException {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException("AppKey must be a valid UUID string.");
        }
    }
}
