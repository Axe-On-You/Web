package ru.pmih.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointCollection implements Serializable {
    private final List<Point> points = new ArrayList<>();

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        if (point != null) {
            points.add(point);
        }
    }

    public void clear() {
        points.clear();
    }

    public List<Point> getPointsForPage(int page, int pageSize) {
        List<Point> reversed = new ArrayList<>(points);
        Collections.reverse(reversed);

        int fromIndex = (page - 1) * pageSize;
        if (reversed.isEmpty() || fromIndex >= reversed.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + pageSize, reversed.size());
        return reversed.subList(fromIndex, toIndex);
    }

    public int getTotalPages(int pageSize) {
        if (pageSize <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) points.size() / pageSize);
    }
}