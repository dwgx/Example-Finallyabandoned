package com.example.value;

import com.example.utils.text.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BasicValue<T> {
    private final String name;
    private final String description;
    private final T defaultValue;
    private T value;
    private final List<BasicValue<T>> children;
    private final ReentrantReadWriteLock lock;

    public BasicValue(String name, String description, T defaultValue) {
        this.name = Validate.notBlank(name, "Name cannot be null or empty");
        this.description = StringUtils.isBlank(description) ? "No description" : description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.children = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public BasicValue(String name, T defaultValue) {
        this(name, null, defaultValue);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        lock.readLock().lock();
        try {
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setValue(T value) {
        lock.writeLock().lock();
        try {
            if (value != null) {
                this.value = value;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void reset() {
        setValue(defaultValue);
    }

    public BasicValue<T> child(BasicValue<T> child) {
        lock.writeLock().lock();
        try {
            children.add(Objects.requireNonNull(child, "Child cannot be null"));
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    public void removeChild(BasicValue<T> child) {
        lock.writeLock().lock();
        try {
            children.remove(child);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<BasicValue<T>> getChildren() {
        lock.readLock().lock();
        try {
            return List.copyOf(children);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<BasicValue<T>> getChildByName(String name) {
        lock.readLock().lock();
        try {
            return children.stream()
                    .filter(child -> child.getName().equals(name))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
}
