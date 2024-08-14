package info.logbat.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class UUIDUtil {

  private static final int UUID_BYTE_LENGTH = 16;

  public static byte[] uuidStringToBytes(String uuidStr) {
    UUID uuid = UUID.fromString(uuidStr);
    ByteBuffer bb = ByteBuffer.wrap(new byte[UUID_BYTE_LENGTH]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  public static String bytesToUuidString(byte[] bytes) {
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    long high = bb.getLong();
    long low = bb.getLong();
    UUID uuid = new UUID(high, low);
    return uuid.toString();
  }
}
