package ru.pmih.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Точка входа. Только создаёт компоненты и запускает обработчик FastCGI.
 */
public class Main {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.time.Instant.class, new InstantAdapter())
                .create();

        CgiHandler handler = new CgiHandler(gson);
        handler.run();
    }
}