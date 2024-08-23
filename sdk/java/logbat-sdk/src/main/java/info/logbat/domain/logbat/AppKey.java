package info.logbat.domain.logbat;

import info.logbat.exception.InvalidOptionException;
import java.util.UUID;

public class AppKey {

    private final String value;

    public AppKey(String value) throws InvalidOptionException {
        validateAppKey(value);
        validateUUIDString(value);
        this.value = value;
    }

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
