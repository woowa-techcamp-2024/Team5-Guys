package info.logbat.domain.logbat;

import info.logbat.exception.InvalidAppKeyException;
import java.util.UUID;

public class AppKey {

    private final String value;

    public AppKey(String value) throws InvalidAppKeyException {
        validateAppKey(value);
        validateUuidString(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private void validateAppKey(String value) throws InvalidAppKeyException {
        if (value == null || value.isBlank()) {
            throw new InvalidAppKeyException("AppKey must not be null or empty.");
        }
    }

    private void validateUuidString(String value) throws InvalidAppKeyException {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidAppKeyException("AppKey must be a valid UUID string.");
        }
    }
}
