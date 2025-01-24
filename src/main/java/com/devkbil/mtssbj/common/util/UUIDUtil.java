package com.devkbil.mtssbj.common.util;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {


    /**
     * [MAC Address] MAC 주소 대신에 임의의 48비트 숫자를 생성합니다.(보안 우려로 이를 대체합니다)
     *
     * @return
     */
    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    /*
     * [TimeStamp] 타임스템프를 이용하여 64개의 최상위 비트를 생성합니다.
     */
    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    /**
     * UUID v1을 생성하여 반환합니다.(MAC Address, TimeStamp 조합)
     *
     * @return
     */
    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits); // 62dd98f0-bd8e-11ed-93ab-325096b39f47
    }

    private static UUID generateType3UUID() {
        String name = "uuid name key";
        UUID uuid3 = UUID.nameUUIDFromBytes(name.getBytes());
        return uuid3;
    }

    private static UUID getnerateType4UUID() {
        UUID uuid4 = UUID.randomUUID();
        return uuid4;
    }
/*
    private static UUID generateType5UUID() {
        String name = "uuid name key";
        UUID namespace = UUID.fromString("00000000-0000-0000-0000-000000000000");
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] namespaceBytes = namespace.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bytesToHash = new byte[nameBytes.length + namespaceBytes.length];

        System.arraycopy(nameBytes, 0, bytesToHash, 0, nameBytes.length);
        System.arraycopy(namespaceBytes, 0, bytesToHash, nameBytes.length, namespaceBytes.length);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(bytesToHash);
            hash[6] &= 0x0f;
            hash[6] |= 0x50;
            hash[8] &= 0x3f;
            hash[8] |= 0x80;
            return UUID.nameUUIDFromBytes(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error creating UUID v5", e);
        }
    }
 */
}
