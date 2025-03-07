package com.devkbil.mtssbj.common.util;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {

    /**
     * Generates the least significant 64 bits for a Version 1 UUID.
     * This method creates a random 63-bit value and sets the 3 most significant bits
     * to ensure it conforms to the UUID variant specification.
     *
     * @return a long representing the least significant 64 bits for a Version 1 UUID
     */
    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    /**
     * Generates the most significant 64 bits for a Version 1 UUID based on the current timestamp.
     * This method uses the current time in milliseconds to construct the timestamp fields
     * required by the UUID version 1 specification. It combines time_low, time_mid,
     * time_hi, and version fields into a single 64-bit value.
     *
     * @return a long representing the most significant 64 bits for a Version 1 UUID
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
     * Generates a Version 1 UUID.
     * This method utilizes the current timestamp for the most significant bits
     * and a randomly generated value for the least significant bits according
     * to the UUID version 1 specification.
     *
     * @return a UUID object representing a Version 1 UUID
     */
    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits); // 62dd98f0-bd8e-11ed-93ab-325096b39f47
    }

    /**
     * Generates a Version 3 UUID (name-based) using a predefined name.
     * This method uses the UUID.nameUUIDFromBytes method to create a UUID
     * from the byte representation of the specified name.
     *
     * @return a UUID object representing a Version 3 UUID
     */
    private static UUID generateType3UUID() {
        String name = "uuid name key";
        UUID uuid3 = UUID.nameUUIDFromBytes(name.getBytes());
        return uuid3;
    }

    /**
     * Generates a random UUID of type 4 (randomly generated UUID).
     * This method utilizes the built-in UUID.randomUUID() method
     * to generate a universally unique identifier.
     *
     * @return a UUID object representing a randomly generated type 4 UUID
     */
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
