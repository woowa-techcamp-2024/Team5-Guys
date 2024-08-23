package info.logbat.domain.options;

import info.logbat.config.LogBatConfig;
import info.logbat.domain.logbat.AppKey;
import info.logbat.exception.InvalidAppKeyException;

public class LogBatOptions {

    private final AppKey appKey;

    public AppKey getAppKey() {
        return appKey;
    }

    public LogBatOptions(LogBatConfig logbatConfig) throws InvalidAppKeyException {
        this.appKey = new AppKey(logbatConfig.getValue("appKey"));
    }

}
