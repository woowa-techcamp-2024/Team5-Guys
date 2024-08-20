package info.logbat.config;

import java.io.InputStream;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

public class LogbatConfigLoader {

    public static CompositeConfiguration loadConfig() {
        CompositeConfiguration config = new CompositeConfiguration();

        boolean yamlLoaded = addYamlConfigurations(config);
        boolean propertiesLoaded = addPropertiesConfigurations(config);

        if (!propertiesLoaded && !yamlLoaded) {
            throw new RuntimeException("Could not load any configuration files.");
        }

        return config;
    }

    private static boolean addYamlConfigurations(CompositeConfiguration config) {
        YAMLConfiguration yamlConfig = new YAMLConfiguration();
        try (InputStream yamlStream = LogbatConfigLoader.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (yamlStream != null) {
                yamlConfig.read(yamlStream);
                config.addConfiguration(yamlConfig);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Could not load application.yml: " + e.getMessage());
        }

        try (InputStream yamlStream = LogbatConfigLoader.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (yamlStream != null) {
                yamlConfig.read(yamlStream);
                config.addConfiguration(yamlConfig);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Could not load application.yaml: " + e.getMessage());
        }
        return false;
    }

    private static boolean addPropertiesConfigurations(CompositeConfiguration config) {
        Configurations configs = new Configurations();
        try {
            Configuration propertiesConfig = configs.properties("application.properties");
            config.addConfiguration(propertiesConfig);

            return true;
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            System.err.println("Could not load application.properties: " + e.getMessage());
            return false;
        }
    }

}
