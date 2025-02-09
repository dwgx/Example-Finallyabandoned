package com.example.mod.storage.event;

public class EventStorage<T> {
    private T event;

    public void setEvent(T event) {
        this.event = event;
    }

    public T getEvent() {
        return event;
    }
}
