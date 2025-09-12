package ru.pmih.web;

class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}