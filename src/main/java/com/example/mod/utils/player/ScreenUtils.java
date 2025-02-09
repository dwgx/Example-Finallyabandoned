package com.example.mod.utils.player;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import static com.example.mod.client.GameAccessor.mc;

public class ScreenUtils {
    public static GenericContainerScreen getContainerScreen() {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            return screen;
        }

        return null;
    }

    public static boolean inContainerScreen() {
        return mc.currentScreen instanceof GenericContainerScreen;
    }

    public static ScreenHandler getHandler(HandledScreen<?> screen) {
        return screen.getScreenHandler();
    }

    public static ScreenHandler getHandler() {
        if (mc.currentScreen instanceof HandledScreen<?> screen) {
            return getHandler(screen);
        }
        return null;
    }
}
