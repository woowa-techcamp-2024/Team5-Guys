package info.logbat.domain.log.values;


public class LogData {

    private final String value;

    public static LogData from(String data) {
        return new LogData(data);
    }

    public String getValue() {
        return value;
    }

    private LogData(String data) {
        validateData(data);
        this.value = data;
    }

    private void validateData(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("log data는 null이거나 빈 문자열일 수 없습니다.");
        }
    }
}
