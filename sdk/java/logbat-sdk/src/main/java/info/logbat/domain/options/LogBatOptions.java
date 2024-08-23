package info.logbat.domain.options;

import info.logbat.config.LogbatConfig;
import info.logbat.config.LogbatConfigLoader;
import info.logbat.domain.logbat.AppKey;
import info.logbat.exception.InvalidAppKeyException;
import java.util.Map;

public class LogBatOptions {

    private final AppKey appKey;

    public AppKey getAppKey() {
        return appKey;
    }

    public LogBatOptions() throws InvalidAppKeyException {
        this(getLogbatConfig());
    }

    protected LogBatOptions(LogbatConfig logbatConfig) throws InvalidAppKeyException {
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
