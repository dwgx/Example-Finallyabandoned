package com.example.handlers;

import com.example.utils.pattern.Singleton;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 为什么error.getCause().getCause()。因为这个是我的事件公交车拉的屎
public class PublicationErrorHandler implements IPublicationErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationErrorHandler.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Handle the given publication error.
     *
     * @param error The PublicationError to handle.
     */
    @Override
    public void handleError(PublicationError error) {
        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append("\n========================================\n")
                .append("Publication Error Report\n")
                .append("========================================\n")
                .append("Time                   : ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n")
                .append("Error Message          : ").append(error.getMessage()).append("\n")
                .append("Cause                  : ")
                .append(error.getCause().getCause() != null ? error.getCause().getCause().toString() : "N/A").append("\n")
                .append("Listener               : ")
                .append(error.getListener() != null ? error.getListener().toString() : "N/A").append("\n")
                .append("Handler Context        : ")
                .append(error.getHandler() != null ? error.getHandler().toString() : "N/A").append("\n")
                .append("Stack Trace:\n");

        if (error.getCause().getCause() != null) {
            for (StackTraceElement element : error.getCause().getCause().getStackTrace()) {
                errorMessage.append("\t- ").append(element.toString()).append("\n");
            }
        }

        errorMessage.append("\n");

        LOGGER.error(errorMessage.toString());
    }

    public static PublicationErrorHandler getInstance() {
        return Singleton.getInstance(PublicationErrorHandler.class);
    }
}
