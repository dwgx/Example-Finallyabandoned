package com.example.utils.unsafe;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UnsafeUtils {
    private static final Unsafe UNSAFE;

    private static final ConcurrentMap<Field, VarHandle> VAR_HANDLE_CACHE = new ConcurrentHashMap<>();

    static {
        UNSAFE = initUnsafe();
    }

    private static Unsafe initUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to obtain Unsafe instance", e);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    public static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    public static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    public static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    public static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    public static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    public static void putLong(long address, long value) {
        UNSAFE.putLong(address, value);
    }

    public static void putObject(Object obj, long offset, Object value) {
        UNSAFE.putObject(obj, offset, value);
    }

    public static Object getObject(Object obj, long offset) {
        return UNSAFE.getObject(obj, offset);
    }

    public static VarHandle getVarHandle(Field field) {
        return VAR_HANDLE_CACHE.computeIfAbsent(field, UnsafeUtils::createVarHandle);
    }

    private static VarHandle createVarHandle(Field field) {
        try {
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectVarHandle(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to obtain VarHandle for field: " + field.getName(), e);
        }
    }

    public static void setFieldValue(Object target, Field field, Object value) {
        VarHandle varHandle = getVarHandle(field);
        varHandle.set(target, value);
    }

    public static Object getFieldValue(Object target, Field field) {
        VarHandle varHandle = getVarHandle(field);
        return varHandle.get(target);
    }

    public static boolean compareAndSet(Object target, Field field, Object expected, Object newValue) {
        VarHandle varHandle = getVarHandle(field);
        return varHandle.compareAndSet(target, expected, newValue);
    }

    public static boolean compareAndSwapInt(Object obj, long offset, int expected, int value) {
        return UNSAFE.compareAndSwapInt(obj, offset, expected, value);
    }

    public static boolean compareAndSwapObject(Object obj, long offset, Object expected, Object value) {
        return UNSAFE.compareAndSwapObject(obj, offset, expected, value);
    }

    public static void park(boolean isAbsolute, long time) {
        UNSAFE.park(isAbsolute, time);
    }

    public static void unpark(Thread thread) {
        UNSAFE.unpark(thread);
    }

    public static int arrayBaseOffset(Class<?> arrayClass) {
        return UNSAFE.arrayBaseOffset(arrayClass);
    }

    public static int arrayIndexScale(Class<?> arrayClass) {
        return UNSAFE.arrayIndexScale(arrayClass);
    }
}
