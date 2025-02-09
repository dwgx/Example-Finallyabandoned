package com.example.mod.client;

import com.example.Global;
import com.example.mod.events.client.GameActionEvent;
import com.example.mod.handlers.ClientHandler;
import com.example.mod.server.ModuleServer;
import net.engio.mbassy.listener.Handler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import java.io.IOException;

public class ExampleClient implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            ModuleServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Global.getEventBus().subscribe(this);
    }

    @Handler
    public void onGameAction(GameActionEvent event) {
        if (event.getAction().equals(GameActionEvent.Action.INIT)) {
            ClientHandler.getInstance().init();
        } else {
            ClientHandler.getInstance().shutdown();
        }
    }
}
