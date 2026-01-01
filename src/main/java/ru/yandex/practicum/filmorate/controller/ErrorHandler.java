package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    // 400 — ошибка валидации
    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(InvalidFormatException e) {
        return Map.of(
                "error", "Ошибка валидации",
                "message", e.getMessage()
        );
    }

    // 404 — объект не найден
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        return Map.of(
                "error", "Объект не найден",
                "message", e.getMessage()
        );
    }

    // 500 — любое другое исключение
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception e) {
        return Map.of(
                "error", "Внутренняя ошибка сервера",
                "message", e.getMessage()
        );
    }
}
