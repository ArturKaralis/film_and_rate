package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(DateValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectDate(DateValidationException exception) {
        log.warn("400 - Некорректная дата обрабатываемого объекта", exception);
        return Map.of(exception.getDescription(), exception.getMessage());
    }

    @ExceptionHandler(IncorrectPathDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectPathData(IncorrectPathDataException exception) {
        log.error("400 - Обнаружены некорректные данные в запросе", exception);
        return Map.of(exception.getDescription(), exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNullPointerException(NullPointerException ex) {
        log.error("404 - Что-то пошло не так", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(ObjectNotFoundException exception) {
        log.error("404 - Искомый объект не найден", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalErrors(Exception exception) {
        log.error("500 - Внутренняя ошибка сервера", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(EmptyObjectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyObject(EmptyObjectException exception) {
        log.error("404 - Передаваемый объект пуст", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        log.error("400 - передано некорректное значение рейтинга", ex);
    return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("400 - ошибка валидации данных", ex);
        return new ErrorResponse(ex.getFieldError().toString());
    }

}
