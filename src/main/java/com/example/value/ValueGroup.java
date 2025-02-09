package com.example.value;

import com.example.entity.NamedEntity;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class ValueGroup extends NamedEntity {
    private final Set<BasicValue<?>> values = new LinkedHashSet<>();

    public ValueGroup(String name) {
        this.id = name;
    }

    public <T extends BasicValue<?>> T add(T value) {
        values.add(value);
        return value;
    }

    public void remove(BasicValue<?> value) {
        values.remove(value);
    }

    public Optional<BasicValue<?>> getValue(String name) {
        return values.stream().filter(v -> v.getName().equals(name)).findFirst();
    }

    public Set<BasicValue<?>> getValues() {
        return new LinkedHashSet<>(values);
    }
}
