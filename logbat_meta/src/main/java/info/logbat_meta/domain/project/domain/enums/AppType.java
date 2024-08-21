package info.logbat_meta.domain.project.domain.enums;

public enum AppType {
    JS, JAVA;

    public static AppType from(String appTypeName) {
        for (AppType appType : AppType.values()) {
            if (appType.name().equals(appTypeName)) {
                return appType;
            }
        }
        throw new IllegalArgumentException("잘못된 앱 타입 요청입니다.");
    }
}
