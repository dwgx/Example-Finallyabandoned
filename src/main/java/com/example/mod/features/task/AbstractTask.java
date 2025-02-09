package com.example.mod.features.task;

import com.example.mod.client.GameAccessor;
import com.example.mod.managers.TaskManager;

public abstract class AbstractTask implements Task, GameAccessor {
    public abstract void onComplete();

    public abstract void onFailure(Exception e);

    public void submit() {
        TaskManager.getInstance().submit(this);
    }
}