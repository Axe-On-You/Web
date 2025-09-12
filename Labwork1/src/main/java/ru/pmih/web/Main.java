package ru.pmih.web;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.Instant.class, new InstantAdapter())
            .create();

    private static final List<HistoryEntry> HISTORY = new CopyOnWriteArrayList<>();
    private static final int HISTORY_LIMIT = 1000;

    private static final String CGI_200_OK_RESPONSE = """
            Status: 200 OK
            Content-Type: application/json
            Content-Length: %d

            %s""";

    private static final String CGI_400_BAD_REQUEST = """
            Status: 400 Bad Request
            Content-Type: application/json
            Content-Length: %d

            %s""";

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        try {
            while (fcgi.FCGIaccept() >= 0) {
                try {
                    System.err.println("--- DEBUG: System Properties ---");
                    Properties props = System.getProperties();
                    for (Map.Entry<Object, Object> entry : props.entrySet()) {
                        System.err.println(entry.getKey() + " = " + entry.getValue());
                    }
                    System.err.println("--- END DEBUG ---");

                    String method = prop("REQUEST_METHOD", "GET");
                    String contentType = prop("CONTENT_TYPE", "");
                    String queryString = prop("QUERY_STRING", "");
                    String body = "";

                    if ("POST".equalsIgnoreCase(method)) {
                        int contentLength = parseIntSafe(prop("CONTENT_LENGTH", "0"));
                        if (contentLength > 0 && contentType.toLowerCase(Locale.ROOT)
                                .contains("application/x-www-form-urlencoded")) {
                            body = readRequestBody(contentLength);
                        }
                    }

                    String rawParams = !body.isEmpty() ? body : queryString;

                    if (rawParams == null || rawParams.isEmpty()) {
                        sendResponse(200, gson.toJson(snapshotHistory()));
                        flushStreams();
                        continue;
                    }

                    if (rawParams.contains("clear=true")) {
                        HISTORY.clear();
                        sendResponse(200, gson.toJson(snapshotHistory()));
                        flushStreams();
                        continue;
                    }

                    long startTime = System.nanoTime();
                    Params params = new Params(rawParams);
                    boolean hitResult = calculate(params.getX(), params.getY(), params.getR());
                    long executionTime = System.nanoTime() - startTime;

                    HistoryEntry entry = new HistoryEntry(
                            params.getX(),
                            params.getY(),
                            params.getR(),
                            executionTime,
                            hitResult
                    );

                    appendToHistory(entry);
                    sendResponse(200, gson.toJson(snapshotHistory()));
                    flushStreams();

                } catch (IllegalArgumentException iae) {
                    sendResponse(400, gson.toJson(List.of()));
                    flushStreams();
                } catch (Exception ex) {
                    System.err.println("Критическая ошибка запроса: " + ex.getClass().getName() + ": " + ex.getMessage());
                    sendResponse(400, gson.toJson(List.of()));
                    flushStreams();
                }
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка процесса: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private static String prop(String name, String def) {
        String v = System.getProperty(name);
        return v != null ? v : def;
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static String readRequestBody(int length) throws Exception {
        InputStream in = System.in;
        byte[] buf = new byte[length];
        int off = 0;
        while (off < length) {
            int n = in.read(buf, off, length - off);
            if (n < 0) break;
            off += n;
        }
        return new String(buf, 0, off, StandardCharsets.UTF_8);
    }

    private static void appendToHistory(HistoryEntry entry) {
        HISTORY.add(entry);
        if (HISTORY.size() > HISTORY_LIMIT) {
            int toRemove = HISTORY.size() - HISTORY_LIMIT;
            for (int i = 0; i < toRemove; i++) {
                if (!HISTORY.isEmpty()) {
                    HISTORY.remove(0);
                }
            }
        }
    }

    private static List<HistoryEntry> snapshotHistory() {
        return new ArrayList<>(HISTORY);
    }

    private static void sendResponse(int statusCode, String jsonBody) {
        byte[] jsonBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        String responseTemplate = (statusCode == 200) ? CGI_200_OK_RESPONSE : CGI_400_BAD_REQUEST;
        String response = String.format(responseTemplate, jsonBytes.length, jsonBody);
        System.out.print(response);
    }

    private static void flushStreams() {
        System.out.flush();
        System.err.flush();
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