package com.example.mod.features.task;

public interface Task {
    void execute() throws Exception;
    void onComplete();
    void onFailure(Exception e);
}