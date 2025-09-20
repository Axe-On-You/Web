package ru.pmih.web;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Отвечает за хранение и управление историей запросов.
 * Использует LinkedList для эффективного добавления в конец и удаления из начала.
 */
public class HistoryManager {
    private final LinkedList<HistoryEntry> history = new LinkedList<>();
    private final int limit;

    public HistoryManager(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит должен быть положительным числом.");
        }
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

    /**
     * Упрощенный и более эффективный метод обрезки.
     * Если размер превышает лимит, просто удаляем первый (самый старый) элемент.
     */
    private void trimIfNeeded() {
        if (history.size() > limit) {
            history.removeFirst();
        }
    }
}