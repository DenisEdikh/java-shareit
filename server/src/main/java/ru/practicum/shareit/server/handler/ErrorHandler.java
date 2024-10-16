package ru.practicum.shareit.server.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.server.exception.ConditionsNotMetException;
import ru.practicum.shareit.server.exception.InternalServerException;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequest(InvalidRequestException e) {
        return new ErrorResponse(e.getMessage(), "Неподдерживаемый тип запроса");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse unprocessableEntity(ConditionsNotMetException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(NotFoundException e) {
        return new ErrorResponse("Искомый объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerError(InternalServerException e) {
        return new ErrorResponse("Внутренняя ошибка", e.getMessage());
    }
}