package ru.pmih.web;

import java.time.Instant;

public record HistoryEntry(
        double x,
        double y,
        double r,
        Instant currentTime,
        long executionTime,
        boolean result
) {

    public HistoryEntry(double x, double y, double r, long executionTime, boolean result) {
        this(x, y, r, Instant.now(), executionTime, result);
    }

    public HistoryEntry {
        if (currentTime == null) {
            currentTime = Instant.now();
        }
    }

    public double getX() { return x(); }
    public double getY() { return y(); }
    public double getR() { return r(); }
    public Instant getCurrentTime() { return currentTime(); }
    public long getExecutionTime() { return executionTime(); }
    public boolean isResult() { return result(); }
}