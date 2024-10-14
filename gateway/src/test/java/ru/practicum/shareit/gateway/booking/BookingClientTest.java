package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(BookingClient.class)
class BookingClientTest {
    private final String serverUrl = "http://localhost:9090/bookings";
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    String body;
    NewBookingDto newBookingDto;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        newBookingDto = new NewBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L);
        body = objectMapper.writeValueAsString(newBookingDto);
    }

    @Test
    @SneakyThrows
    void create() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = bookingClient.create(1L, newBookingDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        Long bookingId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d?approved=%b".formatted(bookingId, true)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = bookingClient.update(bookingId, userId, Boolean.TRUE);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getById() {
        Long userId = 1L;
        Long bookingId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(bookingId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = bookingClient.getById(bookingId, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getAllByState() {
        Long userId = 1L;
        Long bookingId = 1L;

        mockServer.expect(requestTo(serverUrl + "?state=%s".formatted(State.ALL.name())))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = bookingClient.getAllByState(bookingId, State.ALL);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getAllByOwner() {
        Long userId = 1L;
        Long bookingId = 1L;

        mockServer.expect(requestTo(serverUrl + "/owner?state=%s".formatted(State.ALL.name())))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = bookingClient.getAllByOwner(bookingId, State.ALL);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }
}