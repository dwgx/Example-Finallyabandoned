package com.example.mod.managers;

import com.example.mod.features.task.Task;
import com.example.utils.pattern.Singleton;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {
    private final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    public void submit(Task task) {
        try {
            task.execute();
            task.onComplete();
        } catch (Exception e) {
            task.onFailure(e);
        }
    }

    public void shutdown() {

    }

    public int getActiveTaskCount() {
        return taskCounter.get();
    }

    public int getTotalTaskCount() {
        return taskCounter.get();
    }

    public static TaskManager getInstance() {
        return Singleton.getInstance(TaskManager.class);
    }
}
