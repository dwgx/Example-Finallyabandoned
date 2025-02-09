package com.example.utils;

import java.util.Optional;

public class Result<T> {
    private final T value;
    private final boolean success;

    private Result(T value, boolean success) {
        this.value = value;
        this.success = success;
    }

    public static <T> Result<T> empty() {
        return new Result<>(null, true);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, true);
    }

    public static <T> Result<T> failure() {
        return new Result<>(null, false);
    }

    public static <T> Result<T> failure(T value) {
        return new Result<>(value, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }
}