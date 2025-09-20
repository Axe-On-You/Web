package ru.pmih.web;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;

import java.util.Properties;

/**
 * Ядро FastCGI-приложения. Управляет жизненным циклом приёма запросов, делегирует:
 *  - историю HistoryManager
 *  - вычисления AreaCalculator
 *  - низкоуровневые операции CgiUtils
 */
public class CgiHandler {

    private final FCGIInterface fcgiInterface;
    private final Gson gson;
    private final boolean debugSystemProps;

    public CgiHandler(Gson gson) {
        this(gson, isDebugEnabled());
    }

    public CgiHandler(Gson gson, boolean debugSystemProps) {
        this.fcgiInterface = new FCGIInterface();
        this.gson = gson;
        this.debugSystemProps = debugSystemProps;
    }

    public void run() {
        try {
            while (fcgiInterface.FCGIaccept() >= 0) {
                handleSingleRequest();
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка процесса: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void handleSingleRequest() {
        try {
            if (debugSystemProps) {
                dumpSystemProperties();
            }

            String queryString = CgiUtils.getProp("QUERY_STRING", "");

            // Если параметров нет, просто возвращаем ошибку, так как теперь любой запрос должен их содержать.
            if (queryString == null || queryString.isEmpty()) {
                throw new ValidationException("Запрос не содержит параметров.");
            }

            long start = System.nanoTime();
            Params params = new Params(queryString);
            boolean result = AreaCalculator.hit(params.getX(), params.getY(), params.getR());
            long execTime = System.nanoTime() - start;

            HistoryEntry entry = new HistoryEntry(
                    params.getX(),
                    params.getY(),
                    params.getR(),
                    execTime,
                    result
            );

            CgiUtils.sendJson(200, gson.toJson(entry));

        } catch (ValidationException ve) {
            CgiUtils.sendJson(400, gson.toJson(new ErrorResponse(ve.getMessage())));
        } catch (IllegalArgumentException iae) {
            CgiUtils.sendJson(400, gson.toJson(new ErrorResponse("Некорректные параметры запроса.")));
        } catch (Exception ex) {
            System.err.println("Критическая ошибка запроса: " + ex.getClass().getName() + ": " + ex.getMessage());
            CgiUtils.sendJson(500, gson.toJson(new ErrorResponse("Внутренняя ошибка обработки запроса.")));
        } finally {
            CgiUtils.flush();
        }
    }

    private void dumpSystemProperties() {
        System.err.println("--- DEBUG: System Properties ---");
        Properties props = System.getProperties();
        for (String name : props.stringPropertyNames()) {
            System.err.println(name + " = " + props.getProperty(name));
        }
        System.err.println("--- END DEBUG ---");
    }

    private static boolean isDebugEnabled() {
        String flag = System.getenv("DEBUG_FCGI");
        if (flag == null) {
            flag = System.getProperty("DEBUG_FCGI");
        }
        return flag != null && !flag.isBlank()
                && (flag.equalsIgnoreCase("true") || flag.equals("1") || flag.equalsIgnoreCase("yes"));
    }
}