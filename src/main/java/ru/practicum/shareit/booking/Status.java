package ru.practicum.shareit.booking;

import lombok.Getter;

@Getter
public enum Status {
    APPROVED(1),
    WAITING(2),
    REJECTED(3);

    private final int value;

    Status(int value) {
        this.value = value;
    }
}
