package com.example.mod.file.impl;

import com.diaoling.schema.ConfigUtils;
import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.datatype.Datatypes;
import com.diaoling.schema.file.FileSchema;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.file.AbstractFile;
import com.example.utils.input.ShortcutKey;
import com.example.utils.interfaces.Configurable;
import com.example.value.BasicValue;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.Map;

public class FileModuleConfig extends AbstractFile<ConfigSchema.ModuleConfig, AbstractModule> {
    public FileModuleConfig(Path path, Configurable<?> configurable, FileSchema.FileType fileType, FileSchema.BaseFile baseFile, ConfigSchema.ModuleConfig data) {
        super(path, Path.of("modules", "default"), configurable, fileType, baseFile, data);
    }

    @Override
    public void read() {
        Map<String, Datatypes.Type> settingsMap = this.getData().getConfig().getSettingsMap();

        if (!settingsMap.isEmpty()) {
            for (BasicValue<?> basicValue : this.getConfigurable().getValues()) {
                String settingName = basicValue.getName();

                Datatypes.Type type = settingsMap.getOrDefault(settingName, null);

                if (type != null) {
                    ConfigUtils.setValue(type, basicValue);
                }
            }
        }

        if (this.getConfigurable() instanceof AbstractModule module) {
            switch (this.getData().getStatus()) {
                case ACTIVE_STATUS -> {
                    module.setState(true);
                }

                case INACTIVE_STATUS -> {
                    module.setState(false);
                }
            }

            int primaryKey = this.getData().getModuleKey();
            
            if (primaryKey != GLFW.GLFW_KEY_UNKNOWN) {
                module.setShortcutKey(new ShortcutKey(primaryKey).getPrimaryKey());
            }
        }
    }

    @Override
    public void save() {

    }
}
