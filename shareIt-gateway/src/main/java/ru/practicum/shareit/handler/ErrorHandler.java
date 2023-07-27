package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(ValidationException.class,
                e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(MethodArgumentNotValidException.class,
                e.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeader(MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(MissingRequestHeaderException.class,
                e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(MissingServletRequestParameterException.class,
                e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleHibernateViolationException(ConstraintViolationException e) {
        log.warn(e.getLocalizedMessage());
        return new ErrorResponse(ConstraintViolationException.class,
                e.getMessage());
    }


    private static class ErrorResponse {
        String error;
        String errorClass;

        public ErrorResponse(Class<?> entityClass, String message) {
            this.errorClass = entityClass.getSimpleName();
            this.error = message;
        }

        public String getErrorClass() {
            return errorClass;
        }

        public String getError() {
            return error;
        }
    }
}
