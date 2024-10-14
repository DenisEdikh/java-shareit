package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String API_PREFIX = "/requests";
    private LocalDateTime ldt;
    ItemDto itemDto;
    ItemRequestWithItemDto itemRequestWithItemDto;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService irs;

    @BeforeEach
    void setup() {
        ldt = LocalDateTime.of(2020, Month.AUGUST, 10, 10, 10, 10);
        itemDto = new ItemDto(1L, "name", "description", false);
        itemRequestWithItemDto = new ItemRequestWithItemDto(1L, "description", ldt, Set.of(itemDto));
    }

    @Test
    @SneakyThrows
    void createItemRequest() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("description");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", ldt);

        when(irs.createItemRequest(anyLong(), any(NewItemRequestDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
        verify(irs, times(1)).createItemRequest(anyLong(), any(NewItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void getItemRequestsByUserId() {
        when(irs.getItemRequestsByUserId(anyLong())).thenReturn(List.of(itemRequestWithItemDto));

        mvc.perform(get(API_PREFIX)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestWithItemDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
        verify(irs, times(1)).getItemRequestsByUserId(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOther() {
        when(irs.getItemRequestsByOther(anyLong())).thenReturn(List.of(itemRequestWithItemDto));

        mvc.perform(get(API_PREFIX + "/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestWithItemDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
        verify(irs, times(1)).getItemRequestsByOther(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemRequestsById() {
        when(irs.getItemRequestsById(anyLong())).thenReturn(itemRequestWithItemDto);

        mvc.perform(get(API_PREFIX + "/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestWithItemDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(1)));
        verify(irs, times(1)).getItemRequestsById(anyLong());
    }
}