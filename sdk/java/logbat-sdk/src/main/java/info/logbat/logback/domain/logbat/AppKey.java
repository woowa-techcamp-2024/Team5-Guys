package info.logbat.logback.domain.logbat;

import java.util.UUID;

public class AppKey {

    private final String value;

    public AppKey(String value) {
        validateAppKey(value);
        validateUuidString(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private void validateAppKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AppKey must not be null or empty.");
        }
    }

    private void validateUuidString(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("AppKey must be a valid UUID string.");
        }
    }
}
