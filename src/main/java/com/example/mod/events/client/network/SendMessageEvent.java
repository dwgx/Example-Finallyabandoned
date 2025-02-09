package com.example.mod.events.client.network;

import com.example.event.Event;

public class SendMessageEvent extends Event.Cancellable {
    private String context;

    public SendMessageEvent(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
