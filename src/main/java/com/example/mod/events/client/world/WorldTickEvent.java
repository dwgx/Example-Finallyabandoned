package com.example.mod.events.client.world;

import com.example.event.Event;

public class WorldTickEvent extends Event.Cancellable {
    public WorldTickEvent(State state) {
        super(state);
    }

    @Override
    public void cancel(String reason) {
        if (getState() == State.POST) {
            throw new UnsupportedOperationException("Cannot cancel POST state.");
        }
        super.cancel(reason);
    }
}
