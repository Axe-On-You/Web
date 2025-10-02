package ru.pmih.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.pmih.web.model.Point;
import ru.pmih.web.model.PointCollection;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "AreaCheckServlet", urlPatterns = "/check")
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.nanoTime();

        String xStr = req.getParameter("x").replace(',', '.');
        String yStr = req.getParameter("y").replace(',', '.');
        String rStr = req.getParameter("r").replace(',', '.');

        try {
            double x = Double.parseDouble(xStr);
            double y = Double.parseDouble(yStr);
            double r = Double.parseDouble(rStr);

            boolean hit = checkHit(x, y, r);
            long executionTime = System.nanoTime() - startTime;
            Point point = new Point(x, y, r, hit, LocalDateTime.now(), executionTime);

            HttpSession session = req.getSession();
            PointCollection points = (PointCollection) session.getAttribute("points");
            if (points == null) {
                points = new PointCollection();
                session.setAttribute("points", points);
            }
            points.addPoint(point);

            req.setAttribute("resultPoint", point);

        } catch (NumberFormatException | NullPointerException e) {
            req.setAttribute("error", "Invalid number format.");
        } catch (Exception e) {
            req.setAttribute("error", "An unexpected error occurred.");
        }

        getServletContext().getRequestDispatcher("/result.jsp").forward(req, resp);
    }

    private boolean checkHit(double x, double y, double r) {
        // 1-я четверть: прямоугольный треугольник
        if (x >= 0 && y >= 0) {
            return (x <= r) && (y <= r / 2) && (y <= -0.5 * x + r / 2);
        }
        // 2-я четверть: четверть круга
        if (x <= 0 && y >= 0) {
            return (x * x + y * y <= (r / 2) * (r / 2));
        }
        // 3-я четверть: прямоугольник
        if (x <= 0 && y <= 0) {
            return (x >= -r) && (y >= -r / 2);
        }
        // 4-я четверть: пусто
        return false;
    }
}