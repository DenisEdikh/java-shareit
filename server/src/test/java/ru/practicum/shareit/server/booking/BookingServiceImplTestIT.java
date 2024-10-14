package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTestIT {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User owner;
    private User booker;
    private User booker2;
    private Item item;
    private Booking booking;
    private NewBookingDto newBookingDto;
    private final LocalDateTime start1 = LocalDateTime.now().plusHours(5);
    private final LocalDateTime end1 = LocalDateTime.now().plusHours(10);

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@email.com");
        booker2 = userRepository.save(booker2);

        item = new Item();
        item.setName("name2");
        item.setDescription("description");
        item.setOwner(owner);
        item.setAvailable(Boolean.TRUE);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(start1);
        booking.setEnd(end1);
        booking.setItem(item);
        booking.setBooker(booker2);
        bookingRepository.save(booking);

        newBookingDto = new NewBookingDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId()
        );

    }

    @Test
    void createBookingWhenAllValid() {
        BookingDto bookingDto = bookingService.create(booker.getId(), newBookingDto);

        assertNotNull(bookingDto);
        assertNotNull(bookingDto.getId());
        assertEquals(bookingDto.getStart(), newBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), newBookingDto.getEnd());
        assertEquals(bookingDto.getEnd(), newBookingDto.getEnd());
        assertEquals(bookingDto.getStatus(), Status.WAITING);
        assertEquals(bookingDto.getBooker().getName(), booker.getName());
        assertEquals(bookingDto.getItem().getName(), item.getName());
    }

    @Test
    void createBookingWhenBookerIsMissing() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(Long.MAX_VALUE, newBookingDto));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void createBookingWhenItemIsMissing() {
        newBookingDto = new NewBookingDto(
                LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 10, 10),
                LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 15, 10),
                Long.MAX_VALUE
        );

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(booker.getId(), newBookingDto));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Item with id = %d not found ", newBookingDto.getItemId()));
    }

    @Test
    void createBookingWhenItemAvailableIsFalse() {
        item.setAvailable(Boolean.FALSE);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> bookingService.create(booker.getId(), newBookingDto));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Item with id = %d not available", item.getId()));
    }

    @Test
    void createBookingWhenOverlappingTime() {
        newBookingDto.setStart(start1);
        newBookingDto.setEnd(start1.plusHours(1));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> bookingService.create(booker.getId(), newBookingDto));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Item with id = %d is busy", item.getId()));
    }

    @Test
    void updateWhenAllValid() {
        BookingDto bookingDto = bookingService.update(booking.getId(), owner.getId(), Boolean.FALSE);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), Status.REJECTED);
    }

    @Test
    void updateWhenUserIsNotOwner() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> bookingService.update(booking.getId(), booker2.getId(), Boolean.FALSE));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Only owner can change status");
    }

    @Test
    void updateWhenStatusNotValid() {
        booking.setStatus(Status.APPROVED);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> bookingService.update(booking.getId(), booker2.getId(), Boolean.FALSE));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Status must be WAITING");
    }

    @Test
    void updateWhenBookingIsMissing() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.update(Long.MAX_VALUE, booker2.getId(), Boolean.FALSE));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Booking with id = %d not found ", Long.MAX_VALUE));
    }


    @Test
    void getByIdWhenAllValid() {
        BookingDto bookingDto = bookingService.getById(booking.getId(), booker2.getId());

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
        assertEquals(bookingDto.getBooker().getName(), booker2.getName());
        assertEquals(bookingDto.getItem().getName(), item.getName());
    }

    @Test
    void getByIdWhenUserIsMissing() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(Long.MAX_VALUE, booker2.getId()));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Booking with id = %d not found ", Long.MAX_VALUE));
    }

    @Test
    void getByIdWhenUserIsPresentButNotIsBooker() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(booking.getId(), booker.getId()));

        assertNotNull(exception);
        assertEquals(exception.getMessage(), String.format("Booking with id = %d not found ", booking.getId()));
    }

    @Test
    void getAllByStateWhenAllValidAndStateCurrent() {
        List<BookingDto> bookings = bookingService.getAllByState(booker2.getId(), State.CURRENT);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByStateWhenAllValidAndStatePast() {
        List<BookingDto> bookings = bookingService.getAllByState(booker2.getId(), State.PAST);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByStateWhenAllValidAndStateFuture() {
        List<BookingDto> bookings = bookingService.getAllByState(booker2.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByStateWhenAllValidAndStateWaiting() {
        List<BookingDto> bookings = bookingService.getAllByState(booker2.getId(), State.WAITING);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByStateWhenAllValidAndStateRejected() {
        List<BookingDto> bookings = bookingService.getAllByState(booker2.getId(), State.REJECTED);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValid() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByOwnerWhenOwnerDoesNotHasBookings() {
        List<BookingDto> bookings = bookingService.getAllByOwner(booker2.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValidAndStateCurrent() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.CURRENT);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValidAndStatePast() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.PAST);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValidAndStateFuture() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValidAndStateWaiting() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.WAITING);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByOwnerWhenAllValidAndStateRejected() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), State.REJECTED);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }
}