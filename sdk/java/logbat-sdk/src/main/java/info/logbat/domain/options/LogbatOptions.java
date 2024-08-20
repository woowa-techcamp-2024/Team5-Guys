package info.logbat.domain.options;

import info.logbat.config.LogbatConfig;
import info.logbat.config.LogbatConfigLoader;
import info.logbat.domain.logbat.AppKey;
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
