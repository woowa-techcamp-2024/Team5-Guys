package info.logbat.logback;

public class Logbat {

    public AppKey appKey;

    public Logbat(String appKey) {
        this.appKey = new AppKey(appKey);
    }

    public AppKey getAppKey() {
        return appKey;
    }
}
