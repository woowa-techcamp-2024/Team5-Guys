package info.logbat_view.common.util;

import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDConvertor {

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

}
