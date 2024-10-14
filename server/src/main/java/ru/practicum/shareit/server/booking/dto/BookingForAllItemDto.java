package ru.practicum.shareit.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingForAllItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private Status status;
}
