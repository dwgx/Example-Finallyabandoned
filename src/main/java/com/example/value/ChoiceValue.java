package com.example.value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 单选下拉：ChoiceValue<T>
 * 多选：内部类 Multi
 */
public class ChoiceValue<T> extends BasicValue<T> {
    private final Collection<T> options;

    public ChoiceValue(String name, String description, Collection<T> options, T defaultValue) {
        super(name, description, defaultValue);
        this.options = new HashSet<>(options);
        validate(defaultValue);
    }

    public ChoiceValue(String name, Collection<T> options, T defaultValue) {
        this(name, "", options, defaultValue);
    }

    private void validate(T defaultValue) {
        if (!options.contains(defaultValue)) {
            throw new IllegalArgumentException("The default value must be one of the options.");
        }
    }

    @Override
    public void setValue(T value) {
        if (!options.contains(value)) {
            throw new IllegalArgumentException("Value must be one of the available options.");
        }
        super.setValue(value);
    }

    public Collection<T> getOptions() {
        return Set.copyOf(options);
    }

    // ========== 多选内部类 ==========
    public class Multi extends BasicValue<Collection<T>> {
        private final Collection<T> options;

        public Multi(String name, String description,
                     Collection<T> options, Collection<T> defaultValue) {
            super(name, description, defaultValue);
            this.options = options;
            validate(defaultValue);
        }

        public Multi(String name,
                     Collection<T> options, Collection<T> defaultValue) {
            this(name, "", options, defaultValue);
        }

        private void validate(Collection<T> defaultValue) {
            for (T v : defaultValue) {
                if (!options.contains(v)) {
                    throw new IllegalArgumentException("Default " + v + " must be in options.");
                }
            }
        }

        @Override
        public void setValue(Collection<T> value) {
            for (T v : value) {
                if (!options.contains(v)) {
                    throw new IllegalArgumentException("Value must be one of the available options: " + v);
                }
            }
            super.setValue(value);
        }

        public Collection<T> getOptions() {
            return Set.copyOf(options);
        }
    }
}
