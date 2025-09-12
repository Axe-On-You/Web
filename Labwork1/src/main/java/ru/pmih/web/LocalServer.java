package ru.pmih.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LocalServer {
    private static final int MAX_HISTORY_SIZE = 20;
    private static final List<HistoryEntry> history = Collections.synchronizedList(new ArrayList<>());
    private static final Logger logger = LoggerFactory.getLogger(LocalServer.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.staticFiles.add("/static", Location.CLASSPATH);
        }).start(8080);

        logger.info("Сервер запущен. Откройте http://localhost:8080 в браузере.");
        app.get("/cgi-bin/labwork1.jar", LocalServer::handleRequest);
    }

    private static void handleRequest(Context ctx) {
        if (ctx.queryString() == null || Objects.requireNonNull(ctx.queryString()).isEmpty()) {
            ctx.status(200).json(history);
            return;
        }
        long startTime = System.nanoTime();
        try {
            Params params = new Params(ctx.queryString());
            boolean hitResult = calculate(params.getX(), params.getY(), params.getR());
            long executionTime = System.nanoTime() - startTime;
            HistoryEntry entry = new HistoryEntry(params.getX(), params.getY(), params.getR(), executionTime, hitResult);

            synchronized (history) {
                history.add(0, entry);
                if (history.size() > MAX_HISTORY_SIZE) {
                    history.remove(MAX_HISTORY_SIZE);
                }
            }
            ctx.status(200).json(history);

        } catch (ValidationException e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            ctx.status(400).json(error);
        } catch (Exception e) {
            logger.error("Произошла внутренняя ошибка сервера", e);
            Map<String, String> error = Map.of("error", "Произошла внутренняя ошибка сервера.");
            ctx.status(500).json(error);
        }
    }

    private static boolean calculate(double x, double y, double r) {
        if (x >= 0 && y >= 0) {
            return (x <= r) && (y <= r) && (x + y <= r);
        }
        if (x <= 0 && y >= 0) {
            return (x >= -r) && (y <= r);
        }
        if (x <= 0 && y <= 0) {
            return false;
        }
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r);
        }
        return false;
    }
}