package info.logbat.logback.domain.logbat;

public class Logbat {

    public AppKey appKey;

    public Logbat(String appKey) {
        this.appKey = new AppKey(appKey);
    }

    public AppKey getAppKey() {
        return appKey;
    }
}
