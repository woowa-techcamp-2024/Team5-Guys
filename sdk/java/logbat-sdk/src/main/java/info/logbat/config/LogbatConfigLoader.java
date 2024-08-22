package info.logbat.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LogbatConfigLoader {

    public static Map<String, String> loadConfig() {
        Map<String, String> config = new HashMap<>();

        if (!loadYaml(config, "application.yml") && !loadYaml(config, "application.yaml")) {
            loadProperties(config, "application.properties");
        }

        if (config.isEmpty()) {
            System.err.println("Warning: No Logbat configuration loaded.");
        }

        return config;
    }

    private static boolean loadYaml(Map<String, String> config, String filename) {
        try (InputStream is = LogbatConfigLoader.class.getClassLoader()
            .getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean inLogbatSection = false;
            int logbatIndentation = -1;

            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\\s+$", "");

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("logbat:")) {
                    inLogbatSection = true;
                    logbatIndentation = line.indexOf("logbat:");
                    continue;
                }

                if (inLogbatSection) {
                    int currentIndentation = line.indexOf(line.trim());

                    if (currentIndentation <= logbatIndentation) {
                        break;
                    }

                    String[] parts = line.trim().split(":\\s*", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        config.put("logbat." + key, value);
                    }
                }
            }

            if (!config.isEmpty()) {
                System.out.println("Loaded YAML config: " + config);
            }
            return !config.isEmpty();
        } catch (Exception e) {
            System.err.println("Could not load " + filename + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean loadProperties(Map<String, String> config, String filename) {
        try (InputStream is = LogbatConfigLoader.class.getClassLoader()
            .getResourceAsStream(filename)) {
            if (is == null) {
                System.err.println("Resource not found: " + filename);
                return false;
            }

            Properties props = new Properties();
            props.load(is);

            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("logbat.")) {
                    config.put(key, props.getProperty(key));
                }
            }
            return !config.isEmpty();
        } catch (IOException e) {
            System.err.println("Could not load " + filename + ": " + e.getMessage());
            return false;
        }
    }
}