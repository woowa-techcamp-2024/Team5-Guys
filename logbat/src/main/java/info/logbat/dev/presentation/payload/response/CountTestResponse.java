package info.logbat.dev.presentation.payload.response;

public record CountTestResponse(
    Long successCount,
    Long errorCount
) {

  public static CountTestResponse of(Long successCount, Long errorCount) {
    return new CountTestResponse(successCount, errorCount);
  }
}
