package com.example.clockntp.ntp;

public class NTPTimeStamp {

    public static final int REFERENCE_TIME_OFFSET = 16;
    public static final int ORIGINATE_TIME_OFFSET = 24;
    public static final int RECEIVE_TIME_OFFSET = 32;
    public static final int TRANSMIT_TIME_OFFSET = 40;

    /*
    *   Remove sign from byte
    * */
    private static long signedToUnsigned(byte b) {
        return ((b & 0x80) == 0x80 ? (b & 0x7F) + 0x80 : b);
    }

    /*
    *   Return unsigned long from buffer
    * */
    public static long readWord(byte[] buffer, int offset) {
        long result = 0; // Sum unsigned bytes to long
        for (int i = 0; i < 4; i++) result += signedToUnsigned(buffer[offset+i]) << (24 - (8 * i));
        return result;
    }

    // Seconds between 1 Jan 1900 and 1 Jan 1970, 70 years and 17 leap days
    private static final long EPOCH = 2208988800L;

    /*
    *   Convert NTP timestamp to unsigned long timestamp
    * */
    public static long readTimeStamp(byte[] buffer, int offset) {
        long seconds = readWord(buffer, offset);
        long fraction = readWord(buffer, offset + 4);
        return  ((seconds - EPOCH) * 1000) + ((fraction * 1000L) / 0x100000000L);
    }

    /*
    *   Write timestamp long to buffer
    * */
    public static void writeTimeStamp(byte[] buffer, int offset, long timestamp) {
        long seconds = timestamp / 1000;
        long fraction = (timestamp - seconds * 1000) * 0x100000000L / 1000;
        seconds += EPOCH;

        // Write seconds to buffer
        for (int i = 0; i < 4; i++) buffer[offset+i] = (byte)(seconds >> (24 - (8 * i)));
        // Write fraction to buffer
        for (int i = 0; i < 4; i++) buffer[offset+4+i] = (byte)(fraction >> (24 - (8 * i)));
    }
}
