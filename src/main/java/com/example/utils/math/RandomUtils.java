package com.example.utils.math;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static long randomLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    public static float randomFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }
    private static final Random RANDOM = new Random();

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return (endExclusive - startInclusive <= 0) ? startInclusive : startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        return (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0) ? startInclusive : startInclusive + (endInclusive - startInclusive) * Math.random();
    }

    public static float nextFloat(double startInclusive, double endInclusive) {
        return (float) ((startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) ? startInclusive : (startInclusive + (endInclusive - startInclusive) * Math.random()));
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static String randomNumber(int length) {
        return random(length, "123456789");
    }

    public static String randomString(int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String randomStringA(int length) {
        return random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String randomStringHex(int length) {
        return random(length, "1234567890ABCDEF");
    }

    public static String randomStringLower(int length) {
        return random(length, "0123456789abcdefghijklmnopqrstuvwxyz");
    }

    public static String randomStringHexLower(int length) {
        return random(length, "0123456789abcdef");
    }

    public static String random(int length, String chars) {
        return random(length, chars.toCharArray());
    }


    public static String random(int length, char[] chars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(chars[(RANDOM.nextInt(chars.length))]);
        }
        return stringBuilder.toString();
    }

    public static void nextBytes(byte[] array) {
        RANDOM.nextBytes(array);
    }

    public static <T> T nextArray(T[] array) {
        if (array.length == 0) return null;
        return array[RANDOM.nextInt(array.length)];
    }

    public static <T> T nextList(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(RANDOM.nextInt(list.size()));
    }
}
