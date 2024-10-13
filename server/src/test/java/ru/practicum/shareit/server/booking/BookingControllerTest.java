package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String API_PREFIX = "/bookings";
    private LocalDateTime ldt1;
    private LocalDateTime ldt2;
    UserDto userDto;
    ItemDto itemDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        ldt1 = LocalDateTime.of(2020, Month.AUGUST, 10, 10, 10, 10);
        ldt2 = LocalDateTime.of(2020, Month.AUGUST, 10, 10, 11, 10);
        userDto = new UserDto(1L, "name", "email@email.com");
        itemDto = new ItemDto(1L, "name", "description", false);
    }

    @Test
    @SneakyThrows
    void createBooking() {
        NewBookingDto newBookingDto = new NewBookingDto(ldt1, ldt2, 1L);
        BookingDto bookingDto = new BookingDto(1L, ldt1, ldt2, itemDto, userDto, Status.WAITING);

        when(bookingService.create(anyLong(), any(NewBookingDto.class))).thenReturn(bookingDto);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class));
        verify(bookingService, times(1)).create(anyLong(), any(NewBookingDto.class));
    }

    @Test
    @SneakyThrows
    void update() {
        BookingDto bookingDto = new BookingDto(1L, ldt1, ldt2, itemDto, userDto, Status.APPROVED);

        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch(API_PREFIX + "/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("approved", String.valueOf(Boolean.TRUE))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class));
        verify(bookingService, times(1)).update(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getById() {
        BookingDto bookingDto = new BookingDto(1L, ldt1, ldt2, itemDto, userDto, Status.APPROVED);

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get(API_PREFIX + "/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class));
        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllByState() {
        BookingDto bookingDto = new BookingDto(1L, ldt1, ldt2, itemDto, userDto, Status.APPROVED);

        when(bookingService.getAllByState(anyLong(), any(State.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get(API_PREFIX)
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("state", State.ALL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class));
        verify(bookingService, times(1)).getAllByState(anyLong(), any(State.class));
    }

    @Test
    @SneakyThrows
    void getAllByOwner() {
        BookingDto bookingDto = new BookingDto(1L, ldt1, ldt2, itemDto, userDto, Status.APPROVED);

        when(bookingService.getAllByOwner(anyLong(), any(State.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get(API_PREFIX + "/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("state", State.ALL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class));
        verify(bookingService, times(1)).getAllByOwner(anyLong(), any(State.class));
    }
}