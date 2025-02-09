package com.example.entity;

public class NamedEntity extends IdentifiedEntity {
    public String getName() {
        return super.getId();
    }
}
