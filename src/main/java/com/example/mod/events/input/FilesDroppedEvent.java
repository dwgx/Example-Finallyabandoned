package com.example.mod.events.input;

import com.example.event.Event;

import java.nio.file.Path;
import java.util.List;

public class FilesDroppedEvent extends Event.Cancellable {
    private final long window;
    private List<Path> paths;
    private int invalidFilesCount;

    public FilesDroppedEvent(long window, List<Path> paths, int invalidFilesCount) {
        this.window = window;
        this.paths = paths;
        this.invalidFilesCount = invalidFilesCount;
    }

    public long getWindow() {
        return window;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public int getInvalidFilesCount() {
        return invalidFilesCount;
    }

    public void setInvalidFilesCount(int invalidFilesCount) {
        this.invalidFilesCount = invalidFilesCount;
    }
}
