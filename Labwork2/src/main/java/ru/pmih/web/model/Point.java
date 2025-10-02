package ru.pmih.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Point(
        double x,
        double y,
        double r,
        boolean hit,
        LocalDateTime timestamp,
        long executionTime
) implements Serializable {
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}