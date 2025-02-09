package com.example.utils.unsafe;

@SuppressWarnings("all")
public class CrashUtils {
    private CrashUtils() {}

    public static void StackOverflow() {
        StackOverflow();
    }

    public static void SegmentationFault() {
        UnsafeUtils.putByte(0, (byte) 0);
    }
}
