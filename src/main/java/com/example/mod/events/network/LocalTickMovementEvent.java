package com.example.mod.events.network;

import com.example.event.Event;

public class LocalTickMovementEvent extends Event.Cancellable {
    public LocalTickMovementEvent(State state) {
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
