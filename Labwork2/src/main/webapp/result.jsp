<%--
  Created by IntelliJ IDEA.
  User: pmih0
  Date: 26.09.2025
  Time: 17:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.pmih.web.model.Point" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Результат проверки</title>
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
        <td class="content-cell" colspan="2" style="padding-right: 0;">
            <main>
                <section class="results-section" style="box-shadow: none; padding: 0;">
                    <h2>Результат проверки</h2>

                    <%
                        Point point = (Point) request.getAttribute("resultPoint");
                        String error = (String) request.getAttribute("error");
                        if (error != null) {
                    %>
                    <p class="error-message"><%= error %></p>
                    <%
                    } else if (point != null) {
                    %>
                    <div class="table-container" style="max-height: none;">
                        <table id="results-table">
                            <thead>
                            <tr>
                                <th>Параметр</th>
                                <th>Значение</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="<%= point.hit() ? "hit-row" : "miss-row" %>" style="font-weight: bold;">
                                <td>Результат</td>
                                <td><%= point.hit() ? "Попадание" : "Промах" %></td>
                            </tr>
                            <tr>
                                <td>X</td>
                                <td><%= point.x() %></td>
                            </tr>
                            <tr>
                                <td>Y</td>
                                <td><%= point.y() %></td>
                            </tr>
                            <tr>
                                <td>R</td>
                                <td><%= point.r() %></td>
                            </tr>
                            <tr>
                                <td>Время запроса</td>
                                <td><%= point.getFormattedTimestamp() %></td>
                            </tr>
                            <tr>
                                <td>Время выполнения скрипта (нс)</td>
                                <td><%= point.executionTime() %></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <%
                        }
                    %>
                    <div class="form-actions">
                        <a href="controller" class="back-link">
                            <button type="button">Вернуться на главную</button>
                        </a>
                    </div>
                </section>
            </main>
        </td>
    </tr>
</table>

</body>
</html>