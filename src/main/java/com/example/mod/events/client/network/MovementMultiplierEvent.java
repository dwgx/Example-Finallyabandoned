package com.example.mod.events.client.network;

import com.example.event.Event;

public class MovementMultiplierEvent extends Event.Cancellable {
    private float forwardMultiplier, sidewaysMultiplier;

    public MovementMultiplierEvent(float forwardMultiplier, float sidewaysMultiplier) {
        this.forwardMultiplier = forwardMultiplier;
        this.sidewaysMultiplier = sidewaysMultiplier;
    }

    public float getForwardMultiplier() {
        return forwardMultiplier;
    }

    public void setForwardMultiplier(float forwardMultiplier) {
        this.forwardMultiplier = forwardMultiplier;
    }

    public float getSidewaysMultiplier() {
        return sidewaysMultiplier;
    }

    public void setSidewaysMultiplier(float sidewaysMultiplier) {
        this.sidewaysMultiplier = sidewaysMultiplier;
    }
}
