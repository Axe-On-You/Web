<%@ page contentType="text/html;charset=UTF-8" %>
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
    <tr class="header-row">
        <td colspan="2">
            <header>
                <h1>Михайлов Петр Сергеевич</h1>
                <h2>Группа: P3211, Вариант: 86954</h2>
            </header>
        </td>
    </tr>
    <tr>
        <td class="content-cell">
            <main>
                <div class="graph-container">
                    <canvas id="graph" width="400" height="400"></canvas>
                </div>
                <div id="error-message" class="error-message"></div>
                <form id="point-form" action="controller" method="get">
                    <fieldset>
                        <legend>Выберите параметры точки</legend>
                        <div class="form-group">
                            <label>Значение X:</label>
                            <div class="radio-group">
                                <% for (int i = -5; i <= 3; i++) { %>
                                <span><input type="radio" name="x" value="<%= i %>"><%= i %></span>
                                <% } %>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="y-input">Значение Y:</label>
                            <input type="text" id="y-input" name="y" placeholder="Число от -3 до 5" maxlength="17">
                        </div>
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
                </form>
            </main>
        </td>

        <td class="results-cell">
            <section class="results-section">
                <h2>История проверок</h2>

                <%
                    List<Point> pointsOnPage = (List<Point>) request.getAttribute("pointsOnPage");
                    Integer totalPages = (Integer) request.getAttribute("totalPages");

                    Integer currentPageObj = (Integer) request.getAttribute("currentPage");
                    Integer pageSizeObj = (Integer) request.getAttribute("pageSize");

                    int currentPage = (currentPageObj != null) ? currentPageObj : 1;
                    int pageSize = (pageSizeObj != null) ? pageSizeObj : 10;

                    if (totalPages == null) totalPages = 1;
                %>

                <div class="pagination-controls">
                    <form action="controller" method="get">
                        <label for="pageSize">Точек на странице:</label>
                        <input type="number" name="pageSize" id="pageSize" value="<%= pageSize %>" min="1" max="99999"
                               maxlength="5" oninput="this.value=this.value.slice(0,this.maxLength)" style="width: 70px;">
                        <input type="hidden" name="page" value="1">
                        <button type="submit">Применить</button>
                    </form>
                </div>

                <div class="table-container">
                    <table id="results-table">
                        <thead>
                        <tr>
                            <th>X</th><th>Y</th><th>R</th><th>Результат</th><th>Время</th><th>Время работы (нс)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (pointsOnPage != null && !pointsOnPage.isEmpty()) {
                                for (Point p : pointsOnPage) {
                        %>
                        <tr class="<%= p.hit() ? "hit-row" : "miss-row" %>">
                            <td><%= p.x() %></td><td><%= p.y() %></td><td><%= p.r() %></td>
                            <td><%= p.hit() ? "Попадание" : "Промах" %></td>
                            <td><%= p.getFormattedTimestamp() %></td><td><%= p.executionTime() %></td>
                        </tr>
                        <%
                            }
                        } else {
                        %>
                        <tr><td colspan="6">История проверок пуста.</td></tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>

                <div class="pagination-nav">
                    <%
                        if (totalPages > 1) {
                            if (currentPage > 1) {
                    %>
                    <a href="controller?page=1&pageSize=<%= pageSize %>" title="На первую страницу">&laquo;</a>
                    <%
                        }
                        int startPage = Math.max(1, currentPage - 2);
                        int endPage = Math.min(totalPages, currentPage + 2);
                        if (currentPage <= 3) {
                            endPage = Math.min(totalPages, 5);
                        }
                        if (currentPage >= totalPages - 2) {
                            startPage = Math.max(1, totalPages - 4);
                        }
                        if (startPage > 1) {
                    %>
                    <span class="pagination-ellipsis">...</span>
                    <%
                        }
                        for (int i = startPage; i <= endPage; i++) {
                            if (i == currentPage) {
                    %>
                    <span class="current-page"><%= i %></span>
                    <%
                    } else {
                    %>
                    <a href="controller?page=<%= i %>&pageSize=<%= pageSize %>"><%= i %></a>
                    <%
                            }
                        }
                        if (endPage < totalPages) {
                    %>
                    <span class="pagination-ellipsis">...</span>
                    <%
                        }
                        if (currentPage < totalPages) {
                    %>
                    <a href="controller?page=<%= totalPages %>&pageSize=<%= pageSize %>" title="На последнюю страницу">&raquo;</a>
                    <%
                            }
                        }
                    %>
                </div>
                <% if (totalPages > 1) { %>
                <div class="pagination-search">
                    <form action="controller" method="get">
                        <label for="page-search">Перейти к странице:</label>
                        <input type="number" id="page-search" name="page" min="1" max="<%= totalPages %>" required style="width: 60px;"
                               maxlength="<%= String.valueOf(totalPages).length() %>" oninput="this.value=this.value.slice(0,this.maxLength)">
                        <input type="hidden" name="pageSize" value="<%= pageSize %>">
                        <button type="submit">Перейти</button>
                    </form>
                </div>
                <% } %>
            </section>
        </td>
    </tr>
</table>

<script src="js/validation.js"></script>
<script src="js/graph.js"></script>
</body>
</html>