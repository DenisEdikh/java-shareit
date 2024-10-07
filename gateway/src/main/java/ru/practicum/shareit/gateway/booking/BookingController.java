package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;
import ru.practicum.shareit.gateway.exception.InvalidRequestException;
import ru.practicum.shareit.gateway.exception.InvalidStateException;

import java.time.LocalDateTime;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewBookingDto newBookingDto,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long bookerId) {
        log.info("Started creating new booking");
        LocalDateTime now = LocalDateTime.now();
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd())
                || newBookingDto.getEnd().equals(newBookingDto.getStart())
                || newBookingDto.getEnd().isBefore(now)
                || newBookingDto.getStart().isBefore(now)) {
            log.warn("Booking has invalid time");
            throw new InvalidRequestException("Booking has invalid time");
        }
        final ResponseEntity<Object> booking = bookingClient.create(bookerId, newBookingDto);
        log.info("Finished creating new booking");
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable(value = "bookingId") Long bookingId,
                                         @RequestParam(value = "approved") Boolean approved) {
        log.info("Started updating booking");
        final ResponseEntity<Object> booking = bookingClient.update(bookingId, userId, approved);
        log.info("Finished updating booking");
        return booking;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable(value = "bookingId") Long bookingId,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting booking by id");
        final ResponseEntity<Object> booking = bookingClient.getById(bookingId, userId);
        log.info("Finished getting booking by id");
        return booking;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByState(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state/*,
                                          @RequestParam(value = "from") Integer from,
                                          @RequestParam(value = "size") Integer size*/) {
        log.info("Started getting all booking by state");
        State confirmedState = State.from(state).orElseThrow(InvalidStateException::new);
        final ResponseEntity<Object> booking = bookingClient.getAllByState(bookerId, confirmedState);
        log.info("Finished getting all booking by state");
        return booking;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Started getting all booking by owner");
        State confirmedState = State.from(state).orElseThrow(InvalidStateException::new);
        final ResponseEntity<Object> booking = bookingClient.getAllByOwner(ownerId, confirmedState);
        log.info("Finished getting all booking by owner");
        return booking;
    }
}
