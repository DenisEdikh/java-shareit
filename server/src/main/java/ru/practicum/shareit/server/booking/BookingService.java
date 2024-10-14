package ru.practicum.shareit.server.booking;

import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, NewBookingDto newBookingDto);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByState(Long bookerId, State state);

    List<BookingDto> getAllByOwner(Long ownerId, State state);
}