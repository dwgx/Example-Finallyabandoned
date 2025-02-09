package com.example.mod.file.impl;

import com.diaoling.schema.ConfigUtils;
import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.datatype.Datatypes;
import com.diaoling.schema.file.FileSchema;
import com.example.mod.features.ClientSettings;
import com.example.mod.file.AbstractFile;
import com.example.utils.interfaces.Configurable;
import com.example.value.BasicValue;

import java.nio.file.Path;
import java.util.Map;

public class FileClientConfig extends AbstractFile<ConfigSchema.ClientConfig, ClientSettings> {
    public FileClientConfig(Path path, Configurable<?> configurable, FileSchema.FileType fileType, FileSchema.BaseFile baseFile, ConfigSchema.ClientConfig data) {
        super(path, Path.of("client", "default"), configurable, fileType, baseFile, data);
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
    }

    @Override
    public void save() {

    }
}
