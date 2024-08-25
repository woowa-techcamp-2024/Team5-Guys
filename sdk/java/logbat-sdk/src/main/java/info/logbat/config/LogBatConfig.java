package info.logbat.config;

import info.logbat.exception.InvalidOptionException;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration class for LogBat.
 * <p>
 * This class is used to store the configuration options for LogBat. The configuration options are
 * stored in a map, where the key is the option name and the value is the option value. The
 * configuration options can be accessed using the {@link #getValue(String)} method. If the option
 * is not found, an {@link InvalidOptionException} is thrown.
 * </p>
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>
 * @version 0.1.1
 * @see InvalidOptionException
 * @see info.logbat.config.LogBatConfig#getValue(String)
 * @see info.logbat.config.LogBatConfig#LogBatConfig(Map)
 * @since 0.1.1
 */
public class LogBatConfig {

    // The configuration options
    private final Map<String, String> options;

    /**
     * Constructs a new LogBatConfig object with the given options.
     *
     * @param options the configuration options
     */
    public LogBatConfig(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Returns the value of the option with the given key.
     *
     * @param key the key of the option
     * @return the value of the option
     * @throws InvalidOptionException if the option is not found
     */
    public String getValue(String key) throws InvalidOptionException {
        return Optional.ofNullable(options.get(key))
            .orElseThrow(() -> new InvalidOptionException("Key not found: " + key));
    }

}
