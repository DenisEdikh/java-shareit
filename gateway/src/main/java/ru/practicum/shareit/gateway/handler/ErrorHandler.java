package ru.practicum.shareit.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.gateway.exception.InvalidRequestException;
import ru.practicum.shareit.gateway.exception.InvalidStateException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({InvalidStateException.class, InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequest(RuntimeException e) {
        return new ErrorResponse("Неподдерживаемый тип запроса", e.getMessage());
    }
}