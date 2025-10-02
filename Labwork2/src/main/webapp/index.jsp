<%--
  Created by IntelliJ IDEA.
  User: pmih0
  Date: 26.09.2025
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.pmih.web.model.PointCollection" %>
<%@ page import="ru.pmih.web.model.Point" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа №2</title>
    <link rel="stylesheet" href="css/reset.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<table class="main-table">
    <!-- Шапка страницы -->
    <tr class="header-row">
        <td colspan="2">
            <header>
                <h1>Михайлов Петр Сергеевич</h1>
                <h2>Группа: P3211, Вариант: 86954</h2>
            </header>
        </td>
    </tr>

    <!-- Основной контент -->
    <tr>
        <!-- Левая колонка с формой и графиком -->
        <td class="content-cell">
            <main>
                <!-- График Canvas -->
                <div class="graph-container">
                    <canvas id="graph" width="400" height="400"></canvas>
                </div>

                <!-- Форма для ввода данных -->
                <form id="point-form" action="controller" method="get">
                    <fieldset>
                        <legend>Выберите параметры точки</legend>

                        <!-- Выбор X -->
                        <div class="form-group">
                            <label>Значение X:</label>
                            <div class="radio-group">
                                <% for (int i = -5; i <= 3; i++) { %>
                                <span><input type="radio" name="x" value="<%= i %>"><%= i %></span>
                                <% } %>
                            </div>
                        </div>

                        <!-- Ввод Y -->
                        <div class="form-group">
                            <label for="y-input">Значение Y:</label>
                            <input type="text" id="y-input" name="y" placeholder="Число от -3 до 5" maxlength="17">
                        </div>

                        <!-- Выбор R -->
                        <div class="form-group">
                            <label>Значение R:</label>
                            <div class="radio-group">
                                <% for (int i = 1; i <= 5; i++) { %>
                                <span><input type="radio" name="r" value="<%= i %>"><%= i %></span>
                                <% } %>
                            </div>
                        </div>
                    </fieldset>

                    <div class="form-actions">
                        <button type="submit">Проверить</button>
                        <button type="reset">Очистить</button>
                    </div>
                    <div id="error-message" class="error-message"></div>
                </form>
            </main>
        </td>

        <!-- Правая колонка с результатами -->
        <td class="results-cell">
            <section class="results-section">
                <h2>История проверок</h2>
                <div class="table-container">
                    <table id="results-table">
                        <thead>
                        <tr>
                            <th>X</th>
                            <th>Y</th>
                            <th>R</th>
                            <th>Результат</th>
                            <th>Время</th>
                            <th>Время работы (нс)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            PointCollection points = (PointCollection) session.getAttribute("points");
                            if (points != null) {
                                List<Point> pointList = points.getPoints();
                                for (int i = pointList.size() - 1; i >= 0; i--) {
                                    Point p = pointList.get(i);
                        %>
                        <tr class="<%= p.hit() ? "hit-row" : "miss-row" %>">
                            <td><%= p.x() %></td>
                            <td><%= p.y() %></td>
                            <td><%= p.r() %></td>
                            <td><%= p.hit() ? "Попадание" : "Промах" %></td>
                            <td><%= p.getFormattedTimestamp() %></td>
                            <td><%= p.executionTime() %></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </section>
        </td>
    </tr>
</table>

<script src="js/validation.js"></script>
<script src="js/graph.js"></script>
</body>
</html>