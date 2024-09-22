package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, NewBookingDto newBookingDto) {
        log.debug("Started checking contains booker with bookerId {} and Item in with itemId {} method create",
                bookerId,
                newBookingDto.getItemId());
        final User booker = userService.checkUserById(bookerId);
        final Item item = itemService.checkItemById(newBookingDto.getItemId());
        checkTime(newBookingDto);
        checkAvailable(item);
        log.debug("Finished checking contains booker with bookerId {} and Item in with itemId {} method create",
                bookerId,
                newBookingDto.getItemId());
        final Booking booking = bookingRepository.save(BookingMapper.toBooking(item, booker, newBookingDto));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        final Booking booking = bookingRepository.getReferenceById(bookingId);
        final Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Only owner can change status");
            throw new InvalidRequestException("Only owner can change status");
        }
        item.setAvailable(approved ? Boolean.FALSE : Boolean.TRUE);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        final Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        BooleanExpression byId = QBooking.booking.id.eq(bookingId);
        BooleanExpression byOwner = QBooking.booking.item.owner.id.eq(userId);
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(userId);
        final Optional<Booking> booking = bookingRepository.findOne((byOwner.or(byBooker)).and(byId));
        return BookingMapper.toBookingDto(booking.orElseThrow(() -> {
            log.warn("Booking with id {} not found ", bookingId);
            return new NotFoundException(String.format("Booking with id = %d not found ", bookingId));
        }));
    }

    @Override
    public List<BookingDto> getAllByState(Long bookerId, State state) {
        log.debug("Started checking contains user with bookerId {} in method getAllByState", bookerId);
        userService.checkUserById(bookerId);
        log.debug("Finished checking contains user with bookerId {} in method getAllByState", bookerId);
        LocalDateTime ldt = LocalDateTime.now();
        return switch (state) {
            case ALL -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdOrderByStartDesc(bookerId));
            case WAITING -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING));
            case REJECTED -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED));
            case CURRENT -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, ldt, ldt));
            case PAST -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndEndBeforeOrderByStartDesc(bookerId, ldt));
            case FUTURE -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStartAfterOrderByStartDesc(bookerId, ldt));
        };
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, State state) {
        log.debug("Started checking contains user with bookerId {} in method getAllByOwner", ownerId);
        userService.checkUserById(ownerId);
        log.debug("Finished checking contains user with bookerId {} in method getAllByOwner", ownerId);
        LocalDateTime ldt = LocalDateTime.now();
        return switch (state) {
            case ALL -> BookingMapper.toBookingDto(bookingRepository.findBookingByItemOwnerIdOrderByStartDesc(ownerId));
            case WAITING -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING));
            case REJECTED -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED));
            case CURRENT -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, ldt, ldt));
            case PAST -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, ldt));
            case FUTURE -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, ldt));
        };
    }

    private void checkAvailable(Item item) {
        if (!item.getAvailable()) {
            log.warn("Item with id {} not available", item.getId());
            throw new InvalidRequestException(String.format("Item with id = %d not available", item.getId()));
        }
    }

    private void checkTime(NewBookingDto newBookingDto) {
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd())
                || newBookingDto.getEnd().equals(newBookingDto.getStart())
                || newBookingDto.getEnd().isBefore(LocalDateTime.now())
                || newBookingDto.getStart().isBefore(LocalDateTime.now())) {
            log.warn("Booking has invalid time");
            throw new InvalidRequestException("Booking has invalid time");
        }
    }
}
