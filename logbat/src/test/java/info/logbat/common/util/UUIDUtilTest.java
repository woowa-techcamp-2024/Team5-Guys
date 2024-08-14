package info.logbat.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("UUIDUtil에서 ")
public class UUIDUtilTest {

  @ParameterizedTest
  @DisplayName("UUID 문자열은 byte 배열로 변환할 수 있다.")
  @MethodSource("validUUIDProvider")
  void uuidToBytes_ShouldConvertUUIDStringToByteArray(String uuidStr, byte[] expectedBytes) {
    // when
    byte[] bytes = UUIDUtil.uuidStringToBytes(uuidStr);

    assertAll(
        () -> assertThat(bytes).isNotNull(),
        () -> assertThat(bytes.length).isEqualTo(16),
        () -> assertThat(bytes).isEqualTo(expectedBytes)
    );
  }

  @ParameterizedTest
  @DisplayName("bytesToUUIDString은 byte 배열을 UUID 문자열로 정확하게 변환해야 한다.")
  @MethodSource("validUUIDProvider")
  void bytesToUUIDString_ShouldConvertByteArrayToUUIDString(String expectedUUIDStr, byte[] bytes) {
    // when
    String resultUUIDStr = UUIDUtil.bytesToUuidString(bytes);

    // then
    assertThat(resultUUIDStr).isEqualTo(expectedUUIDStr);
  }


  private static Stream<Arguments> validUUIDProvider() {
    return Stream.of(
        Arguments.of("550e8400-e29b-41d4-a716-446655440000",
            new byte[]{85, 14, -124, 0, -30, -101, 65, -44, -89, 22, 68, 102, 85, 68, 0, 0}),
        Arguments.of("123e4567-e89b-12d3-a456-426614174000",
            new byte[]{18, 62, 69, 103, -24, -101, 18, -45, -92, 86, 66, 102, 20, 23, 64, 0}),
        Arguments.of("00000000-0000-0000-0000-000000000000",
            new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0})
    );
  }
}