package info.logbat.config;

import info.logbat.exception.InvalidAppKeyException;
import java.util.Map;
import java.util.Optional;

public class LogBatConfig {

    private final Map<String, String> options;

    public LogBatConfig(Map<String, String> options) {
        this.options = options;
    }

    public String getValue(String key) throws InvalidAppKeyException {
        return Optional.ofNullable(options.get(key))
            .orElseThrow(() -> new InvalidAppKeyException("Key not found: " + key));
    }

}
