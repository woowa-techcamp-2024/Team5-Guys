package info.logbat.logback;

public class AppKey {

    private final String value;

    public AppKey(String value) {
        validateAppKey(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private void validateAppKey(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("AppKey must not be null or empty.");
        }
    }
}
