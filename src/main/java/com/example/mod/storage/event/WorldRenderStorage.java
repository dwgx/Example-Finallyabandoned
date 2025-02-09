package com.example.mod.storage.event;

import com.example.mod.events.client.render.WorldRenderEvent;

public class WorldRenderStorage {
    private static WorldRenderEvent EVENT;
    private static Stage STAGE = Stage.IDLE;

    public static void setEvent(WorldRenderEvent Event) {
        EVENT = Event;
    }

    public static WorldRenderEvent getEvent() {
        return EVENT;
    }

    public static void setStage(Stage stage) {
        STAGE = stage;
    }

    public static Stage getStage() {
        return STAGE;
    }

    public enum Stage { IDLE, START, BEFORE_ENTITIES, AFTER_ENTITIES, END }
}
