package com.example.mod.file;

import com.diaoling.schema.file.FileSchema;
import com.example.utils.interfaces.Configurable;

import java.nio.file.Path;

public abstract class AbstractFile<T, C> {
    private Path path;
    private final Path defaultPath;

    private final Configurable<?> configurable;

    private FileSchema.FileType fileType = FileSchema.FileType.UNKNOWN_FILE;

    private final FileSchema.BaseFile baseFile;
    private final T data;

    public AbstractFile(Path path, Path defaultPath, Configurable<?> configurable, FileSchema.FileType fileType, FileSchema.BaseFile baseFile, T data) {
        this.path = path;
        this.defaultPath = defaultPath;
        this.configurable = configurable;
        this.fileType = fileType;
        this.baseFile = baseFile;
        this.data = data;
    }

    public abstract void read();
    public abstract void save();

    public Path getPath() {
        return path == null ? defaultPath : path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getDefaultPath() {
        return defaultPath;
    }

    public Configurable<?> getConfigurable() {
        return configurable;
    }

    public FileSchema.FileType getFileType() {
        return fileType;
    }

    public FileSchema.BaseFile getBaseFile() {
        return baseFile;
    }

    public T getData() {
        return data;
    }

    public void setFileType(FileSchema.FileType fileType) {
        this.fileType = fileType;
    }
}
