package info.logbat.log.domain;

import lombok.Getter;

@Getter
public class LogData {

  private final String value;

  public LogData(String data) {
    validateData(data);
    this.value = data;
  }

  private void validateData(String data) {
    if (data == null || data.isBlank()) {
      throw new IllegalArgumentException("log data는 null이거나 빈 문자열일 수 없습니다.");
    }
  }
}
