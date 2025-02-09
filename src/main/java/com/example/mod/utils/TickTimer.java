package com.example.mod.utils;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.enums.TickType;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.utils.interfaces.Initializable;
import net.engio.mbassy.listener.Handler;

public class TickTimer implements Initializable {
    private int tick;
    private boolean running;

    private TickType tickType;
    private Event.State eventState;

    public TickTimer(TickType tickType) {
        this.init();

        this.running = false;
        this.tickType = tickType;
        this.eventState = Event.State.PRE;
    }

    public TickTimer() {
        this(TickType.GAME);
    }

    public TickTimer start() {
        if (!this.running) {
            this.tick = 0;
            this.running = true;
        }

        return this;
    }

    public TickTimer stop() {
        if (this.running) {
            this.running = false;
        }

        return this;
    }

    public void reset() {
        this.tick = 0;
        this.running = false;
    }

    @Handler
    public void onGameTick(GameTickEvent event) {
        if (this.isRunning() && this.tickType.equals(TickType.GAME) && this.getEventState().equals(event.getState()))  {
            this.tick++;
        }
    }

    @Handler
    public void onGameTick(LivingEntityTickEvent event) {
        if (event.isLocalPlayer() && this.isRunning() && this.tickType.equals(TickType.LIVING_ENTITY) && this.getEventState().equals(event.getState()))  {
            this.tick++;
        }
    }

    @Override
    public boolean init() {
        Global.getEventBus().subscribe(this);
        return true;
    }

    @Override
    public boolean destroy() {
        Global.getEventBus().unsubscribe(this);
        return true;
    }

    public boolean hasElapsed(int tick) {
        return this.tick >= tick;
    }

    public int getTick() {
        return tick;
    }

    public boolean isRunning() {
        return running;
    }

    public TickType getTickType() {
        return tickType;
    }

    public Event.State getEventState() {
        return eventState;
    }
}
