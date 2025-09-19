package ru.pmih.web;

import java.util.Objects;

/**
 * Класс-обертка для стандартизации сообщений об ошибках,
 * отправляемых клиенту в формате JSON.
 *
 * @param error Текст ошибки. Имя поля "error" будет использовано как ключ в JSON-объекте.
 */
public record ErrorResponse(String error) {
    /**
     * Конструктор для создания объекта ошибки.
     *
     * @param error Сообщение об ошибке. Если null, будет использовано стандартное сообщение.
     */
    public ErrorResponse(String error) {
        this.error = Objects.requireNonNullElse(error, "Произошла неизвестная ошибка");
    }
}
