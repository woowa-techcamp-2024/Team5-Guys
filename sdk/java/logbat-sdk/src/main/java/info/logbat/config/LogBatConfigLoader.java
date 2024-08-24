package info.logbat.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility class for loading LogBat configuration from various file formats. Supports YAML (.yml,
 * .yaml) and Properties (.properties) file formats. Configuration files are expected to be in the
 * classpath.
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>
 * @version 0.1.2
 * @since 0.1.2
 */
@SuppressWarnings("unchecked")
public class LogBatConfigLoader {

    /**
     * Map to store the configuration options.
     * <p>
     * This map does not allow duplicate keys with different values. If a duplicate key is found with
     */
    private static final Map<String, String> CONFIG_MAP = new HashMap<>(){
        @Override
        public String put(String key, String value) {
            if (this.containsKey(key)) {
                if (this.get(key).equals(value)) {
                    return value;
                } else {
                    throw new IllegalArgumentException("Duplicate key: " + key + " with different values " + value + " and " + this.get(key));
                }
            }
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    };
    private static final List<ConfigLoader> LOADERS = List.of(
        new YamlConfigLoader("application.yml"),
        new YamlConfigLoader("application.yaml"),
        new PropertiesConfigLoader("application.properties")
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if an attempt is made to instantiate this class
     */
    private LogBatConfigLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Loads the LogBat configuration from the first available configuration file. Tries to load
     * from YAML files first, then falls back to Properties file.
     *
     * @return {@link LogBatConfig} object containing the loaded configuration
     */
    public static LogBatConfig loadConfig() {
        for (ConfigLoader loader : LOADERS) {
            if (loader.load(loader.filename)) {
                break;
            }
        }
        return new LogBatConfig(CONFIG_MAP);
    }

    /**
     * Abstract base class for configuration loaders.
     */
    private abstract static class ConfigLoader {

        protected String filename;

        /**
         * Loads the configuration from the specified file.
         *
         * @param filename the name of the configuration file to load
         * @return true if the configuration was successfully loaded, false otherwise
         */
        abstract boolean load(String filename);
    }

    /**
     * Configuration loader for YAML files.
     */
    private static class YamlConfigLoader extends ConfigLoader {

        /**
         * Constructs a YamlConfigLoader with the specified filename.
         *
         * @param filename the name of the YAML configuration file
         */
        YamlConfigLoader(String filename) {
            this.filename = filename;
        }

        /**
         * Loads the configuration from a YAML file.
         *
         * @param filename the name of the YAML configuration file to load
         * @return true if the configuration was successfully loaded, false otherwise
         */
        @Override
        boolean load(String filename) {
            try (InputStream inputStream = LogBatConfigLoader.class.getClassLoader()
                .getResourceAsStream(filename)) {
                if (inputStream == null) {
                    return false;
                }
                Yaml yaml = new Yaml();
                Map<String, Object> map = yaml.load(inputStream);
                if (map != null && map.containsKey("logbat")) {
                    Object logBatConfig = map.get("logbat");
                    if (logBatConfig instanceof Map<?, ?>) {
                        Map<String, String> logBatConfigMap = (Map<String, String>) logBatConfig;
                        CONFIG_MAP.putAll(logBatConfigMap);
                    }
                }
                return !CONFIG_MAP.isEmpty();
            } catch (IOException e) {
                System.err.println("Could not load " + filename + ": " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Configuration loader for Properties files.
     */
    private static class PropertiesConfigLoader extends ConfigLoader {

        /**
         * Constructs a PropertiesConfigLoader with the specified filename.
         *
         * @param filename the name of the Properties configuration file
         */
        PropertiesConfigLoader(String filename) {
            this.filename = filename;
        }

        /**
         * Loads the configuration from a Properties file.
         *
         * @param filename the name of the Properties configuration file to load
         * @return true if the configuration was successfully loaded, false otherwise
         */
        @Override
        boolean load(String filename) {
            try (InputStream is = LogBatConfigLoader.class.getClassLoader()
                .getResourceAsStream(filename)) {
                if (is == null) {
                    return false;
                }

                Properties props = new Properties();
                props.load(is);

                for (String key : props.stringPropertyNames()) {
                    if (key.startsWith("logbat.")) {
                        CONFIG_MAP.put(key, props.getProperty(key));
                    }
                }
                return !CONFIG_MAP.isEmpty();
            } catch (IOException e) {
                System.err.println("Could not load " + filename + ": " + e.getMessage());
                return false;
            }
        }
    }

}
