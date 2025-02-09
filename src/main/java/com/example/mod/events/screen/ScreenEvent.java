package com.example.mod.events.screen;

import com.example.event.Event;
import net.minecraft.client.gui.screen.Screen;

public class ScreenEvent extends Event.Cancellable {
    private Screen screen;

    public ScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
