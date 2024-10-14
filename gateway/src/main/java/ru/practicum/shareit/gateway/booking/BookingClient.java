package ru.practicum.shareit.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> create(Long bookerId, NewBookingDto newBooking) {
        return post("", bookerId, newBooking);
    }

    public ResponseEntity<Object> update(Long bookingId, Long userId, Boolean approved) {
        Map<String, Object> param = Map.of("approved", approved);
        return patch("/%d?approved={approved}".formatted(bookingId), userId, param);
    }

    public ResponseEntity<Object> getById(Long bookingId, Long userId) {
        return get("/%d".formatted(bookingId), userId);
    }

    public ResponseEntity<Object> getAllByState(Long bookerId, State state) {
        Map<String, Object> param = Map.of("state", state.name());
        return get("?state={state}", bookerId, param);
    }

    public ResponseEntity<Object> getAllByOwner(Long bookerId, State state) {
        Map<String, Object> param = Map.of("state", state.name());
        return get("/owner?state={state}", bookerId, param);
    }
}
