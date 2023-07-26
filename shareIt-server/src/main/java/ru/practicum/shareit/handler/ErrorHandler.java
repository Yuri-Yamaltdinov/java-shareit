package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("400 — Validation Error");
        return new ErrorResponse(
                String.format(Objects.requireNonNull(e.getMessage()))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("400 — Missing Request Header");
        return new ErrorResponse(String.format("Отсутствует заголовок: " + Objects.requireNonNull(e.getMessage())));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("400 — Method Argument Not Valid");
        return new ErrorResponse(String.format("Неверный аргумент метода: " + Objects.requireNonNull(e.getMessage())));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundEntity(final EntityNotFoundException e) {
        log.error("404 — Cущность {} не найдена: EntityNotFoundException", e.getEntityName());
        return new ErrorResponse(
                String.format("Не найдена сущность класса \"%s\".", e.getEntityName() + "\n" +
                        e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("500 — Произошла непредвиденная ошибка.");
        return new ErrorResponse(
                String.format("Непредвиденная ошибка: " + e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        log.error("409 — Конфликт сущностей.");
        return new ErrorResponse(String.format("Конфликт сущностей: " + e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessException(AccessException e) {
        log.error("403 — Доступ запрещен.");
        return new ErrorResponse(
                String.format("Доступ запрещен: " + e.getMessage())
        );
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse() {
            this.error = "Error message is empty";
        }

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

    }
}

