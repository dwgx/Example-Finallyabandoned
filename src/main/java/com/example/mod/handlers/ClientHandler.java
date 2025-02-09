package com.example.mod.handlers;

import com.diaoling.schema.ConfigUtils;
import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.file.FileSchema;
import com.example.Global;
import com.example.mod.features.ClientSettings;
import com.example.mod.file.impl.FileClientConfig;
import com.example.mod.managers.CommandManager;
import com.example.mod.managers.FileManager;
import com.example.mod.managers.ModuleManager;
import com.example.mod.storage.PacketStorage;
import com.example.mod.utils.render.skija.Skija;
import com.example.mod.utils.render.skija.text.Texts;
import com.example.utils.pattern.Singleton;
import com.example.utils.text.StringUtils;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

import java.nio.file.Path;
import java.util.Map;

import static com.example.mod.client.GameAccessor.mc;

public class ClientHandler {

    private final MicrosoftHandler microsoftHandler = new MicrosoftHandler();

    public void init() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()::handle);

            Profiler profiler = Profilers.get();

            profiler.swap(StringUtils.profilerTag("presents", "init"));
            Global.getPresents().init();

            profiler.swap(StringUtils.profilerTag("eventbus", "register"));
            Global.getEventBus().subscribe(EventHandler.getInstance());
            Global.getEventBus().subscribe(PacketStorage.getInstance());

            try {
                profiler.swap(StringUtils.profilerTag("skija", "init"));
                Skija.getInstance().initSkia(mc.getWindow());
            } catch (Throwable throwable) {
                ExceptionHandler.getInstance().handle(Thread.currentThread(), throwable);
            }

            try {
                profiler.swap(StringUtils.profilerTag("texts", "init"));
                Texts.init();
            } catch (Throwable throwable) {
                ExceptionHandler.getInstance().handle(Thread.currentThread(), throwable);
            }

            profiler.swap(StringUtils.profilerTag("file", "client", "settings"));
            ConfigSchema.ClientConfig clientConfig = ClientSettings.getInstance().getConfig();
            FileManager.getInstance().add(
                    new FileClientConfig(
                            Path.of("default"),
                            ClientSettings.getInstance(),
                            FileSchema.FileType.CLIENT_CONFIG_FILE,
                            ConfigUtils.makeBaseFile(
                                    Map.of(),
                                    FileSchema.FileType.CLIENT_CONFIG_FILE,
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    clientConfig.toByteArray()
                            ),
                            clientConfig
                    )
            );

            profiler.swap(StringUtils.profilerTag("managers", "init"));
            ModuleManager.getInstance().init();
            CommandManager.getInstance().init();
            FileManager.getInstance().init();
            profiler.pop();

            try {
                microsoftHandler.init();
            } catch (Exception e) {
                ExceptionHandler.getInstance().handle(Thread.currentThread(), e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            microsoftHandler.shutdown();
            Profiler profiler = Profilers.get();
            profiler.swap(StringUtils.profilerTag("managers", "destroy"));
            ModuleManager.getInstance().destroy();
            CommandManager.getInstance().destroy();
            FileManager.getInstance().destroy();
            profiler.pop();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static ClientHandler getInstance() {
        return Singleton.getInstance(ClientHandler.class);
    }
}
