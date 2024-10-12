package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody NewBookingDto newBookingDto,
                             @RequestHeader(value = "X-Sharer-User-Id") Long bookerId) {
        log.info("Started creating new booking");
        final BookingDto bookingDto = bookingService.create(bookerId, newBookingDto);
        log.info("Finished creating new booking");
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @PathVariable(value = "bookingId") Long bookingId,
                             @RequestParam(value = "approved") Boolean approved) {
        log.info("Started updating booking");
        final BookingDto bookingDto = bookingService.update(bookingId, userId, approved);
        log.info("Finished updating booking");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable(value = "bookingId") Long bookingId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting booking by id");
        final BookingDto bookingDto = bookingService.getById(bookingId, userId);
        log.info("Finished getting booking by id");
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllByState(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                          @RequestParam(value = "state", defaultValue = "ALL") State state) {
        log.info("Started getting all booking by state");
        final List<BookingDto> bookingDtos = bookingService.getAllByState(bookerId, state);
        log.info("Finished getting all booking by state");
        return bookingDtos;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(value = "state", defaultValue = "ALL") State state) {
        log.info("Started getting all booking by owner");
        final List<BookingDto> bookingDtos = bookingService.getAllByOwner(ownerId, state);
        log.info("Finished getting all booking by owner");
        return bookingDtos;
    }
}
