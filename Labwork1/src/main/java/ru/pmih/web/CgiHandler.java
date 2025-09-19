package ru.pmih.web;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;

import java.util.List;
import java.util.Properties;
import java.util.Locale;

/**
 * Ядро FastCGI-приложения. Управляет жизненным циклом приёма запросов, делегирует:
 *  - историю HistoryManager
 *  - вычисления AreaCalculator
 *  - низкоуровневые операции CgiUtils
 */
public class CgiHandler {

    private static final String CLEAR_TOKEN = "clear=true";

    private final FCGIInterface fcgiInterface;
    private final HistoryManager historyManager;
    private final Gson gson;
    private final boolean debugSystemProps;

    public CgiHandler(HistoryManager historyManager, Gson gson) {
        this(historyManager, gson, isDebugEnabled());
    }

    public CgiHandler(HistoryManager historyManager, Gson gson, boolean debugSystemProps) {
        this.fcgiInterface = new FCGIInterface();
        this.historyManager = historyManager;
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

            String method = CgiUtils.getProp("REQUEST_METHOD", "GET");
            String contentType = CgiUtils.getProp("CONTENT_TYPE", "");
            String queryString = CgiUtils.getProp("QUERY_STRING", "");
            String body = "";

            if ("POST".equalsIgnoreCase(method)) {
                int contentLength = CgiUtils.parseIntSafe(CgiUtils.getProp("CONTENT_LENGTH", "0"), 0);
                if (contentLength > 0 && contentType.toLowerCase(Locale.ROOT)
                        .contains("application/x-www-form-urlencoded")) {
                    body = CgiUtils.readRequestBody(contentLength);
                }
            }

            String rawParams = !body.isEmpty() ? body : queryString;

            // Нет параметров — вернуть историю
            if (rawParams == null || rawParams.isEmpty()) {
                respondHistory();
                return;
            }

            // Очистка истории
            if (rawParams.contains(CLEAR_TOKEN)) {
                historyManager.clear();
                respondHistory();
                return;
            }

            processBusiness(rawParams);

        } catch (ValidationException ve) {
            CgiUtils.sendJson(400, gson.toJson(new ErrorResponse(ve.getMessage())));
        } catch (IllegalArgumentException iae) {
            CgiUtils.sendJson(400, gson.toJson(new ErrorResponse("Некорректные параметры запроса.")));
        } catch (Exception ex) {
            System.err.println("Критическая ошибка запроса: " + ex.getClass().getName() + ": " + ex.getMessage());
            CgiUtils.sendJson(500, gson.toJson(new  ErrorResponse("Внутренняя ошибка обработки запроса.")));
        } finally {
            CgiUtils.flush();
        }
    }

    private void processBusiness(String rawParams) throws Exception {
        long start = System.nanoTime();
        Params params = new Params(rawParams);
        boolean result = AreaCalculator.hit(params.getX(), params.getY(), params.getR());
        long execTime = System.nanoTime() - start;

        HistoryEntry entry = new HistoryEntry(
                params.getX(),
                params.getY(),
                params.getR(),
                execTime,
                result
        );
        historyManager.add(entry);
        respondHistory();
    }

    private void respondHistory() {
        List<HistoryEntry> snapshot = historyManager.snapshot();
        CgiUtils.sendJson(200, gson.toJson(snapshot));
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