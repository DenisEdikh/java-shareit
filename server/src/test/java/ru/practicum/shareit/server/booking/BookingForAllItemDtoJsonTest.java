package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.booking.dto.BookingForAllItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingForAllItemDtoJsonTest {
    private final JacksonTester<BookingForAllItemDto> tester;

    @Test
    @SneakyThrows
    void testSerialize() {
        BookingForAllItemDto booking = new BookingForAllItemDto(
                1L,
                LocalDateTime.of(2025, Month.OCTOBER, 10, 10, 10, 10),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 10, 10),
                new UserDto(1L, "name", "email@email.com"),
                Status.WAITING
        );

        JsonContent<BookingForAllItemDto> result = tester.write(booking);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(booking.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(booking.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(booking.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(booking.getStatus().name());
    }
}