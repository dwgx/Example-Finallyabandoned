package com.example.mod.events.client;

import com.example.event.Event;

public class GameActionEvent extends Event.Cancellable {
    public enum Action {
        INIT,
        SHUTDOWN
    }

    private final Action action;

    public GameActionEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public void cancel(String reason) {
        if (action == Action.INIT) {
            throw new UnsupportedOperationException("Cannot cancel INIT action.");
        }
        super.cancel(reason);
    }
}

