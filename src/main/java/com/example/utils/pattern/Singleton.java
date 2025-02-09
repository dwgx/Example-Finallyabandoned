package com.example.utils.pattern;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Singleton {
    private static final Map<Class<?>, Object> INSTANCE_MAP = new ConcurrentHashMap<>();
    private static final MethodType CONSTRUCTOR_METHOD_TYPE = MethodType.methodType(void.class);

    private Singleton() {}

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        return (T) INSTANCE_MAP.computeIfAbsent(clazz, Singleton::createInstance);
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            @SuppressWarnings("unchecked")
            T instance = (T) lookup.findConstructor(clazz, CONSTRUCTOR_METHOD_TYPE).invoke();
            return instance;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }
}
