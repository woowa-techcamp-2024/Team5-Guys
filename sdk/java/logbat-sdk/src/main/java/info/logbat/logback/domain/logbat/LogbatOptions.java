package info.logbat.logback.domain.logbat;

import info.logbat.logback.config.LogbatConfig;
import info.logbat.logback.config.LogbatConfigLoader;
import org.apache.commons.configuration2.CompositeConfiguration;

public class LogbatOptions {

    private final AppKey appKey;

    public LogbatOptions() {
        this(getLogbatConfig());
    }

    protected LogbatOptions(LogbatConfig logbatConfig) {
        this.appKey = new AppKey(logbatConfig.getAppKey());
    }

    private static LogbatConfig getLogbatConfig() {
        return parseLogbatConfig();
    }

    private static LogbatConfig parseLogbatConfig() {
        final CompositeConfiguration configuration = LogbatConfigLoader.loadConfig();

        return new LogbatConfig(configuration.getString("logbat.appKey"));
    }
}
