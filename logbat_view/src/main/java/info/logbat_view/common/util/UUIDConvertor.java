package info.logbat_view.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDConvertor {

    private static final int UUID_BYTE_LENGTH = 16;
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public static UUID convertStringToUUID(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("UUID는 필수 값 입니다.");
        }
        if (!UUID_PATTERN.matcher(uuid).matches()) {
            throw new IllegalArgumentException("UUID 형식이 올바르지 않습니다.");
        }
        return UUID.fromString(uuid);
    }

    public static UUID convertBytesToUUID(byte[] bytes) {
        if (bytes.length != UUID_BYTE_LENGTH) {
            throw new IllegalArgumentException("UUID 바이트 배열의 길이는 16이어야 합니다.");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long mostSigBits = byteBuffer.getLong();
        long leastSigBits = byteBuffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static byte[] convertUUIDToBytes(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID는 필수 값 입니다.");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[UUID_BYTE_LENGTH]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

}
