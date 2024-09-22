package ru.practicum.shareit.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException() {
        super("Invalid status number");
    }
}
