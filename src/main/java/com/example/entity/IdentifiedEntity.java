package com.example.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class IdentifiedEntity {
    protected String id = UUID.randomUUID().toString();
    protected final Set<String> aliases = new HashSet<>();

    public String getId() {
        return id;
    }

    public Set<String> getAliases() {
        return new HashSet<>(aliases);
    }

    public Set<String> getIdentifiers() {
        Set<String> identifiers = new HashSet<>();
        if (id != null) {
            identifiers.add(id);
        }
        identifiers.addAll(aliases);
        return identifiers;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public boolean removeAlias(String alias) {
        return aliases.remove(alias);
    }
}