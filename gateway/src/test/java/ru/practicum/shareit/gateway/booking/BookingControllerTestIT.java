package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTestIT {
    private static final String API_PREFIX = "/bookings";
    private final NewBookingDto newBookingDto = new NewBookingDto(
            LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 10, 10),
            LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 11, 10),
            1L
    );

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void createBooking_WhenNewBookingDtoValid_ThenReturnOk() {
        when(bookingClient.create(anyLong(), any(NewBookingDto.class)))
                .thenReturn(new ResponseEntity<>(newBookingDto, HttpStatus.OK));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId", is(newBookingDto.getItemId()), Long.class));
        verify(bookingClient, times(1)).create(anyLong(), any(NewBookingDto.class));
    }

    @Test
    @SneakyThrows
    void createBooking_WhenNewBookingDtoNotValid_ThenReturnBadRequest() {
        NewBookingDto badBookingDto = new NewBookingDto(
                LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 10, 10),
                LocalDateTime.of(2024, Month.NOVEMBER, 10, 10, 11, 10),
                null
        );

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(badBookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any(NewBookingDto.class));
    }

    @Test
    @SneakyThrows
    void createBooking_WhenDateNotValid_ThenReturnBadRequest() {
        NewBookingDto newBookingDto = new NewBookingDto(
                LocalDateTime.of(2020, Month.AUGUST, 10, 10, 10, 10),
                LocalDateTime.of(2020, Month.AUGUST, 10, 10, 10, 10),
                1L);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any(NewBookingDto.class));
    }

    @Test
    @SneakyThrows
    void updateBooking_WhenNewBookingDtoValid_ThenReturnOk() {
        when(bookingClient.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(newBookingDto, HttpStatus.OK));

        mvc.perform(patch(API_PREFIX + "/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("approved", String.valueOf(Boolean.TRUE))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(bookingClient, times(1)).update(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getById_WhenIdValid_ThenReturnOk() {
        when(bookingClient.getById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(newBookingDto, HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(bookingClient, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllByState_WhenStateValid_ThenReturnOk() {
        when(bookingClient.getAllByState(anyLong(), any(State.class)))
                .thenReturn(new ResponseEntity<>(List.of(newBookingDto), HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", State.ALL.name())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].itemId", is(newBookingDto.getItemId()), Long.class));
        verify(bookingClient, times(1)).getAllByState(anyLong(), any(State.class));
    }

    @Test
    @SneakyThrows
    void getAllByState_WhenStateNotValid_ThenReturnBadRequest() {
        when(bookingClient.getAllByState(anyLong(), any(State.class)))
                .thenReturn(new ResponseEntity<>(List.of(newBookingDto), HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "InvalidState")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).getAllByState(anyLong(), any(State.class));
    }

    @Test
    @SneakyThrows
    void getAllByOwner_WhenStateValid_ThenReturnOk() {
        when(bookingClient.getAllByOwner(anyLong(), any(State.class)))
                .thenReturn(new ResponseEntity<>(List.of(newBookingDto), HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].itemId", is(newBookingDto.getItemId()), Long.class));
        verify(bookingClient, times(1)).getAllByOwner(anyLong(), any(State.class));
    }

    @Test
    @SneakyThrows
    void getAllByOwner_WhenStateNotValid_ThenReturnBadRequest() {
        when(bookingClient.getAllByOwner(anyLong(), any(State.class)))
                .thenReturn(new ResponseEntity<>(newBookingDto, HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "InvalidState")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).getAllByOwner(anyLong(), any(State.class));
    }
}