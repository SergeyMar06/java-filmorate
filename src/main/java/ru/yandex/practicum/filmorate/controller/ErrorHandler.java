package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logInfo(e);
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    // 400 — ошибка валидации
    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(InvalidFormatException e) {
        logInfo(e);
        return Map.of(
                "error", "Ошибка валидации",
                "message", e.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logInfo(e);

        log.error("Ошибка целостности данных", e);

        return Map.of(
                "error", "Ошибка",
                "message", resolveErrorMessage(e.getMessage())
        );
    }

    private String resolveErrorMessage(String message) {
        String defaultMessage = "Похожий объект уже существует";

        if (message == null) {
            return defaultMessage;
        }

        if (message.contains("genre")) {
            return "Genre не найден";
        }

        if (message.contains("mpa")) {
            return "Mpa не найден";
        }

        return defaultMessage;
    }

    // 404 — объект не найден
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        logInfo(e);
        return Map.of(
                "error", "Объект не найден",
                "message", e.getMessage()
        );
    }

    // 500 — любое другое исключение
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception e) {
        logInfo(e);
        return Map.of(
                "error", "Внутренняя ошибка сервера",
                "message", e.getMessage()
        );
    }

    // 400 — BadRequestException
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(BadRequestException e) {
        logInfo(e);
        return Map.of(
                "error", "Ошибка запроса",
                "message", e.getMessage()
        );
    }

    private void logInfo(Exception e) {
        log.debug("Handled exception {}", e.getClass().getSimpleName());
    }
}