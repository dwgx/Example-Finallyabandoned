package com.example.event;

import java.util.Objects;

public abstract class Event {
    public enum State { ANY, PRE, POST }

    private final State state;

    public Event(State state) {
        this.state = state;
    }

    public Event() {
        this(State.ANY);
    }

    public State getState() {
        return state;
    }

    public static class Cancellable extends Event {
        protected boolean canceled = false;
        protected String cancelReason = "";

        public Cancellable() {
            super();
        }

        public Cancellable(State state) {
            super(state);
        }

        public void cancel() {
            cancel(null);
        }

        public void cancel(String reason) {
            if (!this.canceled) {
                this.canceled = true;
                this.cancelReason = Objects.requireNonNullElse(reason, "");
            }
        }

        public boolean isCanceled() {
            return canceled;
        }

        public String getCancelReason() {
            return cancelReason;
        }
    }
}