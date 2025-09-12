package ru.pmih.web;

import java.time.Instant;

public class HistoryEntry {
    private final double x;
    private final double y;
    private final double r;
    private final Instant currentTime;
    private final long executionTime;
    private final boolean result;

    public HistoryEntry(double x, double y, double r, long executionTime, boolean result) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.currentTime = Instant.now();
        this.executionTime = executionTime;
        this.result = result;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public Instant getCurrentTime() { return currentTime; }
    public long getExecutionTime() { return executionTime; }
    public boolean isResult() { return result; }
}