package ru.practicum.shareit.booking;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.practicum.shareit.exception.InvalidStatusException;

import java.util.stream.Stream;

@Converter
public class StatusConverter implements AttributeConverter<Status, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Status state) {
        return state.getValue();
    }

    @Override
    public Status convertToEntityAttribute(Integer value) {
        return Stream.of(Status.values())
                .filter(state -> state.getValue() == value)
                .findFirst()
                .orElseThrow(InvalidStatusException::new);
    }
}

