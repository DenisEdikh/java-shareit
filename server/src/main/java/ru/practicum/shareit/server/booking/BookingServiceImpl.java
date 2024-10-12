package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, NewBookingDto newBookingDto) {
        log.debug("Started checking contains booker with bookerId {} and Item in with itemId {} method create",
                bookerId,
                newBookingDto.getItemId());
        final User booker = checkUserIsContained(bookerId);
        final Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Item with id {} not found ", newBookingDto.getItemId());
            return new NotFoundException(String.format("Item with id = %d not found ", newBookingDto.getItemId()));
        });
        checkAvailable(item);
        isOverlappingTime(newBookingDto, item);
        log.debug("Finished checking contains booker with bookerId {} and Item in with itemId {} method create",
                bookerId,
                newBookingDto.getItemId());
        final Booking booking = bookingRepository.save(BookingMapper.toBooking(item, booker, newBookingDto));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        //todo сделать join fetch
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Booking with id {} not found ", bookingId);
            return new NotFoundException(String.format("Booking with id = %d not found ", bookingId));
        });
        if (booking.getStatus() != Status.WAITING) {
            log.warn("Status must be WAITING");
            throw new InvalidRequestException("Status must be WAITING");
        }
        final Item item = booking.getItem();
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Only owner can change status");
            throw new InvalidRequestException("Only owner can change status");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        final Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        log.debug("Started checking contains user with userId {} in method getById", userId);
        checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method getById", userId);
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
        checkUserIsContained(bookerId);
        log.debug("Finished checking contains user with bookerId {} in method getAllByState", bookerId);
        LocalDateTime ldt = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (state) {
            case ALL -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerId(bookerId, sort));
            case WAITING -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStatus(bookerId, Status.WAITING, sort));
            case REJECTED -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStatus(bookerId, Status.REJECTED, sort));
            case CURRENT -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStartBeforeAndEndAfter(bookerId, ldt, ldt, sort));
            case PAST -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndEndBefore(bookerId, ldt, sort));
            case FUTURE -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByBookerIdAndStartAfter(bookerId, ldt, sort));
        };
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, State state) {
        log.debug("Started checking contains user with bookerId {} in method getAllByOwner", ownerId);
        checkUserIsContained(ownerId);
        log.debug("Finished checking contains user with bookerId {} in method getAllByOwner", ownerId);
        LocalDateTime ldt = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (state) {
            case ALL -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerId(ownerId, sort));
            case WAITING -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStatus(ownerId, Status.WAITING, sort));
            case REJECTED -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStatus(ownerId, Status.REJECTED, sort));
            case CURRENT -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, ldt, ldt, sort));
            case PAST -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndEndBefore(ownerId, ldt, sort));
            case FUTURE -> BookingMapper.toBookingDto(bookingRepository
                    .findBookingByItemOwnerIdAndStartAfter(ownerId, ldt, sort));
        };
    }

    private void checkAvailable(Item item) {
        if (!item.getAvailable()) {
            log.warn("Item with id {} not available", item.getId());
            throw new InvalidRequestException(String.format("Item with id = %d not available", item.getId()));
        }
    }

    private void isOverlappingTime(NewBookingDto newBookingDto, Item item) {
        final List<Booking> bookings = bookingRepository.findBookingByItemId(item.getId());
        if (bookings.stream()
                .anyMatch(booking -> (newBookingDto.getStart().compareTo(booking.getEnd()) <= 0
                        && newBookingDto.getEnd().compareTo(booking.getStart()) >= 0))) {
            log.warn("Item with id {} is busy", item.getId());
            throw new InvalidRequestException(String.format("Item with id = %d is busy", item.getId()));
        }
    }

    private User checkUserIsContained(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", userId);
                    return new NotFoundException(String.format("User with id = %d not found", userId));
                });
    }
}
