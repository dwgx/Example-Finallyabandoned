package com.example.mod.file.impl;

import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.file.FileSchema;
import com.example.mod.file.AbstractFile;
import com.example.utils.interfaces.Configurable;

import java.nio.file.Path;

public class FileSettingsConfig extends AbstractFile<ConfigSchema.SettingsConfig, Configurable> {
    public FileSettingsConfig(Path path, Configurable<?> configurable, FileSchema.FileType fileType, FileSchema.BaseFile baseFile, ConfigSchema.SettingsConfig data) {
        super(path, Path.of("settings", "default"), configurable, fileType, baseFile, data);
    }

    @Override
    public void read() {

    }

    @Override
    public void save() {

    }
}
