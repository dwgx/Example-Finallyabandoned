package com.example.mod.utils.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityUtils {
    public static Set<Entity> filter(Collection<Entity> entityList, Set<EntityType<?>> types) {
        return entityList.stream()
                .filter(entity -> types.contains(entity.getType()))
                .collect(Collectors.toSet());
    }
}
