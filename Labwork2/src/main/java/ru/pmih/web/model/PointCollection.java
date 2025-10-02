package ru.pmih.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PointCollection implements Serializable {
    private final List<Point> points = new ArrayList<>();

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }
}