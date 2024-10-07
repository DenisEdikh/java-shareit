package ru.practicum.shareit.gateway.booking.dto;

import java.util.Arrays;
import java.util.Optional;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNSUPPORTED_STATUS;

    public static Optional<State> from(String state) {
        return Arrays.stream(State.values())
                .filter(s -> s.name().equalsIgnoreCase(state))
                .findFirst();
    }
}
