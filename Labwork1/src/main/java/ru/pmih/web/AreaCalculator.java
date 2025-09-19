package ru.pmih.web;

/**
 * Геометрия:
 *  I четверть: x>=0, y>=0, треугольник x + y <= r
 *  II четверть: x<=0, y>=0, прямоугольник -r<=x<=0, 0<=y<=r
 *  III четверть: пусто
 *  IV четверть: x>=0, y<=0, четверть круга x^2 + y^2 <= r^2
 */
public final class AreaCalculator {

    private AreaCalculator() {}

    public static boolean hit(double x, double y, double r) {
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