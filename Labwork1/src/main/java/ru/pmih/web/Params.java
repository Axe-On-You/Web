package ru.pmih.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

class Params {
    private final double x;
    private final double y;
    private final double r;

    public Params(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Missing query string");
        }
        var params = splitQuery(query);
        validateParams(params);
        this.x = Double.parseDouble(params.get("x"));
        this.y = Double.parseDouble(params.get("y").replace(',', '.'));
        this.r = Double.parseDouble(params.get("r").replace(',', '.'));
    }

    private static Map<String, String> splitQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("=", 2)) // не рвем значения, содержащие '='
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8),
                        (a, b) -> b,
                        HashMap::new
                ));
    }

    private static void validateParams(Map<String, String> params) throws ValidationException {
        // X: оставляем требование целого диапазона [-4;4]
        String xStr = params.get("x");
        if (xStr == null || xStr.isEmpty()) {
            throw new ValidationException("Параметр 'x' отсутствует.");
        }
        try {
            int xVal = Integer.parseInt(xStr.trim());
            if (xVal < -4 || xVal > 4) {
                throw new ValidationException("Недопустимое значение для 'x': " + xVal);
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("'x' должен быть целым числом.");
        }

        // Y: double в диапазоне [-5;5]
        String yStr = params.get("y");
        if (yStr == null || yStr.isEmpty()) {
            throw new ValidationException("Параметр 'y' отсутствует.");
        }
        try {
            double yVal = Double.parseDouble(yStr.replace(',', '.').trim());
            if (yVal < -5 || yVal > 5) {
                throw new ValidationException("Недопустимое значение для 'y': " + yVal + ". 'y' должен быть в диапазоне [-5; 5]");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("'y' должен быть числом.");
        }

        // R: только из набора {1, 1.5, 2, 2.5, 3}
        String rStr = params.get("r");
        if (rStr == null || rStr.isEmpty()) {
            throw new ValidationException("Параметр 'r' отсутствует.");
        }
        try {
            double rVal = Double.parseDouble(rStr.replace(',', '.').trim());
            Set<Double> allowed = Set.of(1.0, 1.5, 2.0, 2.5, 3.0);
            if (!allowed.contains(rVal)) {
                throw new ValidationException("Недопустимое значение для 'r'. Допустимы: 1, 1.5, 2, 2.5, 3");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("'r' должен быть числом.");
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
}