package ru.pmih.web;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Набор низкоуровневых утилит для работы с CGI/FastCGI окружением.
 */
public final class CgiUtils {

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

    private CgiUtils() {}

    public static String getProp(String name, String def) {
        String v = System.getProperty(name);
        return v != null ? v : def;
    }

    public static int parseIntSafe(String s, int def) {
        if (s == null) return def;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static String readRequestBody(int length) throws Exception {
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

    public static void sendJson(int statusCode, String json) {
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        String template = (statusCode == 200) ? CGI_200_OK_RESPONSE : CGI_400_BAD_REQUEST;
        String response = String.format(Locale.ROOT, template, jsonBytes.length, json);
        System.out.print(response);
    }

    public static void flush() {
        System.out.flush();
        System.err.flush();
    }
}