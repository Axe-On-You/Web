package ru.pmih.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Точка входа. Только создаёт компоненты и запускает обработчик FastCGI.
 */
public class Main {

    private static final int HISTORY_LIMIT = 1000;

    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.time.Instant.class, new InstantAdapter())
                .create();

        HistoryManager historyManager = new HistoryManager(HISTORY_LIMIT);
        CgiHandler handler = new CgiHandler(historyManager, gson);
        handler.run();
    }
}