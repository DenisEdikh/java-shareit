package ru.practicum.shareit.gateway.exception;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException() {
        super("Invalid type of state");
    }
}
