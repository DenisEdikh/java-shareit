package ru.practicum.shareit.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
