package info.logbat.infrastructure.payload;

public record LogSendRequest(
    String level,
    String data,
    String timestamp
) {

}
