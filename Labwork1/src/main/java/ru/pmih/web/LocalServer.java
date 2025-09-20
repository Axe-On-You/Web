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

import java.util.Map;

public class LocalServer {
    private static final Logger logger = LoggerFactory.getLogger(LocalServer.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.staticFiles.add("/static", Location.CLASSPATH);
        }).start(8080);

        logger.info("Сервер запущен. Откройте http://localhost:8080/index.html в браузере.");
        app.get("/cgi-bin/labwork1.jar", LocalServer::handleRequest);
    }

    private static void handleRequest(Context ctx) {
        long startTime = System.nanoTime();
        try {
            String queryString = ctx.queryString();
            if (queryString == null || queryString.isEmpty()) {
                throw new ValidationException("Запрос не содержит параметров.");
            }

            Params params = new Params(queryString);
            boolean hitResult = AreaCalculator.hit(params.getX(), params.getY(), params.getR());
            long executionTime = System.nanoTime() - startTime;

            HistoryEntry entry = new HistoryEntry(
                    params.getX(),
                    params.getY(),
                    params.getR(),
                    executionTime,
                    hitResult
            );

            ctx.status(200).json(entry);

        } catch (ValidationException e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            ctx.status(400).json(error);
        } catch (Exception e) {
            logger.error("Произошла внутренняя ошибка сервера", e);
            Map<String, String> error = Map.of("error", "Произошла внутренняя ошибка сервера.");
            ctx.status(500).json(error);
        }
    }
}