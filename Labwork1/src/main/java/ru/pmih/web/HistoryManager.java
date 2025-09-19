package ru.pmih.web;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

/**
 * Отвечает за хранение и управление историей запросов.
 * Потокобезопасность обеспечивается CopyOnWriteArrayList.
 */
public class HistoryManager {

    private final CopyOnWriteArrayList<HistoryEntry> history = new CopyOnWriteArrayList<>();
    private final int limit;

    public HistoryManager(int limit) {
        this.limit = limit;
    }

    public void add(HistoryEntry entry) {
        history.add(entry);
        trimIfNeeded();
    }

    public List<HistoryEntry> snapshot() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }

    private void trimIfNeeded() {
        int overflow = history.size() - limit;
        if (overflow > 0) {
            // Удаляем самые старые
            for (int i = 0; i < overflow; i++) {
                if (!history.isEmpty()) {
                    history.remove(0);
                } else {
                    break;
                }
            }
        }
    }
}