package com.example.mod.events.client;

import com.example.event.Event;

public class GameTickEvent extends Event.Cancellable {
    public GameTickEvent(State state) {
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
