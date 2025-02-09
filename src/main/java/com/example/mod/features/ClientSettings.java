package com.example.mod.features;

import com.diaoling.schema.ConfigUtils;
import com.diaoling.schema.config.ConfigSchema;
import com.example.information.AppInfo;
import com.example.utils.interfaces.Configurable;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Set;

public class ClientSettings implements Configurable<ConfigSchema.ClientConfig> {
    public static final BasicValue<Text> CHAT_PREFIX = new BasicValue<>(
            "Chat Prefix",
            Text.empty().formatted(Formatting.GRAY).append("[").append(Text.literal(AppInfo.NAME).formatted(Formatting.AQUA)).append("] ")
    );

    public static final BasicValue<String> COMMAND_INPUT_PREFIX = new BasicValue<>(
            "Command Input Prefix",
            "."
    );

    public static final BasicValue<Boolean> ADD_COMMAND_HISTORY = new BasicValue<>(
            "Add command history",
            true
    );

    public static final BasicValue<Boolean> ALWAYS_HANDLE_KEY_INPUT = new BasicValue<>(
            "Always handle key input (Except chat)",
            false
    );

    @Override
    public ConfigSchema.ClientConfig getConfig() {
        return ConfigSchema.ClientConfig.newBuilder()
                .setConfig(
                        ConfigUtils.makeSettingConfig(
                                this.getConfigName(),
                                this.getConfigDescription(),
                                AppInfo.AUTHOR,
                                AppInfo.VERSION.toString(),
                                ConfigUtils.makeConfigValue(this.getValues())
                        )
                )
                .setClientVersion(AppInfo.VERSION.toString())
                .build();
    }

    @Override
    public String getConfigName() {
        return "Client Setting";
    }

    @Override
    public String getConfigDescription() {
        return "Client Configuration Options";
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                CHAT_PREFIX,
                COMMAND_INPUT_PREFIX,
                ADD_COMMAND_HISTORY,
                ALWAYS_HANDLE_KEY_INPUT
        );
    }

    public static ClientSettings getInstance() {
        return Singleton.getInstance(ClientSettings.class);
    }
}
