package com.example;

import com.example.handlers.PublicationErrorHandler;
import com.example.information.AppInfo;
import net.engio.mbassy.bus.MBassador;
import net.minecraft.util.Identifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Global {
    private static final MBassador<Object> EVENT_BUS = new MBassador<>(PublicationErrorHandler.getInstance());
    private static final Presents PRESENTS = new Presents();
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();

    // private final ModernUIFabricClient MODERN_UI_CLIENT = new ModernUIFabricClient(ModernUI.LOGGER, ModernUI.MARKER);

    public static Identifier identifier(String path) {
        return Identifier.of(AppInfo.ID, path);
    }

    public static MBassador<Object> getEventBus() {
        return EVENT_BUS;
    }

    public static Presents getPresents() {
        return PRESENTS;
    }
}
