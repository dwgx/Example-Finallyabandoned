package com.example.utils.interfaces;

@FunctionalInterface
public interface Callback<T, E extends Throwable> {
    void onExecute(T data);

    default void onComplete(T data) {}

    default void onFailure(T data, E e) {}
}