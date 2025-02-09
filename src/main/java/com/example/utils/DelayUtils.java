package com.example.utils;

public class DelayUtils {
    public static long getCpsDelay(double cps) {
        if (cps <= 0) {
            return 0;
        }
        return (long) (1000 / cps);
    }
}
