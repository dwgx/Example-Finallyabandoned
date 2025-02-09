package com.example.mod.handlers;

import com.example.utils.pattern.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void handle(Thread thread, Throwable throwable) {
        StringBuilder errorMessage = new StringBuilder()
                .append("\n========================================\n")
                .append("Exception Report\n")
                .append("========================================\n")
                .append("Time                   : ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n")
                .append("Thread Name            : ").append(thread.getName()).append("\n")
                .append("Exception Type         : ").append(throwable.getClass().getName()).append("\n")
                .append("Exception Message      : ").append(throwable.getMessage() == null ? "N/A" : throwable.getMessage()).append("\n")
                .append("Stack Trace:\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            errorMessage.append("\t- ").append(element).append("\n");
        }

        errorMessage.append("\n");

        LOGGER.error(errorMessage.toString());
    }

    public static Thread.UncaughtExceptionHandler asHandler() {
        return (thread, throwable) -> getInstance().handle(thread, throwable);
    }

    public static ExceptionHandler getInstance() {
        return Singleton.getInstance(ExceptionHandler.class);
    }
}
