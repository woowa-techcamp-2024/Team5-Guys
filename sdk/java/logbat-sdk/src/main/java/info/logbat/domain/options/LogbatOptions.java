package info.logbat.domain.options;

import info.logbat.config.LogbatConfig;
import info.logbat.config.LogbatConfigLoader;
import info.logbat.domain.logbat.AppKey;
import info.logbat.exception.InvalidAppKeyException;
import java.util.Map;

public class LogbatOptions {

    private final AppKey appKey;

    public AppKey getAppKey() {
        return appKey;
    }

    public LogbatOptions() throws InvalidAppKeyException {
        this(getLogbatConfig());
    }

    protected LogbatOptions(LogbatConfig logbatConfig) throws InvalidAppKeyException {
        this.appKey = new AppKey(logbatConfig.getAppKey());
    }

    private static LogbatConfig getLogbatConfig() {
        return parseLogbatConfig();
    }

    private static LogbatConfig parseLogbatConfig() {
        final Map<String, String> configMap = LogbatConfigLoader.loadConfig();

        return new LogbatConfig(configMap.get("logbat.appKey"));
    }
}
