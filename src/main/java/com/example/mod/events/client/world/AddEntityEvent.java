package com.example.mod.events.client.world;


import com.example.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;


public class AddEntityEvent extends Event {
    private Entity entity;
    private World world;

    public AddEntityEvent(Entity entity, World world) {
        this.entity = entity;
        this.world = world;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}