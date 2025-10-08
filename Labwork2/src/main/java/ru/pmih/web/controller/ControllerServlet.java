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
import java.util.List;

@WebServlet(name = "ControllerServlet", urlPatterns = "/controller")
public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String x = req.getParameter("x");
        String y = req.getParameter("y");
        String r = req.getParameter("r");

        if (x != null && y != null && r != null) {
            getServletContext().getRequestDispatcher("/check").forward(req, resp);
        } else {
            HttpSession session = req.getSession();
            int pageSize = 10;
            String pageSizeParam = req.getParameter("pageSize");

            if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                try {
                    double psDouble = Double.parseDouble(pageSizeParam);
                    pageSize = (int) psDouble;
                    if (pageSize < 1) pageSize = 1;
                    session.setAttribute("pageSize", pageSize);
                } catch (NumberFormatException e) { /* игнорируем некорректный ввод */ }
            } else {
                Object pageSizeInSession = session.getAttribute("pageSize");
                if (pageSizeInSession != null) {
                    pageSize = (Integer) pageSizeInSession;
                }
            }
            int currentPage = 1;
            try {
                currentPage = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) { /* оставляем 1 */ }

            PointCollection points = (PointCollection) session.getAttribute("points");

            if (points != null) {
                int totalPages = points.getTotalPages(pageSize);
                if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
                if (currentPage < 1) currentPage = 1;

                List<Point> pointsOnPage = points.getPointsForPage(currentPage, pageSize);

                req.setAttribute("pointsOnPage", pointsOnPage);
                req.setAttribute("totalPages", totalPages);
            }

            req.setAttribute("currentPage", currentPage);
            req.setAttribute("pageSize", pageSize);

            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}