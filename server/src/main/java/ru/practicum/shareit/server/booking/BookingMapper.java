package ru.practicum.shareit.server.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingForAllItemDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserMapper;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingForAllItemDto toBookingForAllItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingForAllItemDto bookingForAllItemDto = new BookingForAllItemDto();

        bookingForAllItemDto.setId(booking.getId());
        bookingForAllItemDto.setStart(booking.getStart());
        bookingForAllItemDto.setEnd(booking.getEnd());
        bookingForAllItemDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingForAllItemDto.setStatus(booking.getStatus());
        return bookingForAllItemDto;
    }

    public static List<BookingDto> toBookingDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    public static Booking toBooking(Item item, User user, NewBookingDto newBookingDto) {
        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(newBookingDto.getStart());
        booking.setEnd(newBookingDto.getEnd());
        return booking;
    }
}
