package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.booking.dto.BookingForAllItemDto;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.NewCommentDto;
import ru.practicum.shareit.server.item.dto.ItemAllDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.dto.UpdateItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String API_PREFIX = "/items";
    private LocalDateTime ldt1;
    private LocalDateTime ldt2;
    private ItemAllDto itemAllDto;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @BeforeEach
    void setup() {
        ldt1 = LocalDateTime.of(2020, Month.AUGUST, 10, 10, 10, 10);
        ldt2 = LocalDateTime.of(2020, Month.AUGUST, 10, 10, 11, 10);

        UserDto userDto = new UserDto(1L, "name", "email@email.com");
        BookingForAllItemDto lastBooking = new BookingForAllItemDto(1L, ldt1, ldt2, userDto, Status.APPROVED);
        BookingForAllItemDto nextBooking = new BookingForAllItemDto(
                2L,
                ldt1.plusDays(1),
                ldt2.plusDays(1),
                userDto,
                Status.APPROVED
        );
        CommentDto commentDto = new CommentDto(1L, "text", "authorName", ldt1);
        itemAllDto = new ItemAllDto(
                1L,
                "name",
                "description",
                Boolean.TRUE,
                lastBooking,
                nextBooking,
                Set.of(commentDto)
        );
    }

    @Test
    @SneakyThrows
    void createItem() {
        NewItemDto newItemDto = new NewItemDto("name", "description", Boolean.TRUE, 1L);
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE);

        when(itemService.create(anyLong(), any(NewItemDto.class))).thenReturn(itemDto);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).create(anyLong(), any(NewItemDto.class));
    }

    @Test
    @SneakyThrows
    void createComment() {
        NewCommentDto newCommentDto = new NewCommentDto("text");
        CommentDto commentDto = new CommentDto(1L, "text", "authorName", ldt1);

        when(itemService.createComment(anyLong(), anyLong(), any(NewCommentDto.class))).thenReturn(commentDto);

        mvc.perform(post(API_PREFIX + "/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
        verify(itemService, times(1)).createComment(
                anyLong(),
                anyLong(),
                any(NewCommentDto.class)
        );
    }

    @Test
    @SneakyThrows
    void getAllItems() {
        when(itemService.getAllByUserId(anyLong())).thenReturn(List.of(itemAllDto));

        mvc.perform(get(API_PREFIX)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemAllDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemAllDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemAllDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemAllDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id",
                        is(itemAllDto.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start",
                        is(itemAllDto.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].nextBooking.end",
                        is(itemAllDto.getNextBooking().getEnd().toString())))
                .andExpect(jsonPath("$[0].nextBooking.booker",
                        is(itemAllDto.getNextBooking().getBooker()),
                        UserDto.class))
                .andExpect(jsonPath("$[0].comments", hasSize(1)));
        verify(itemService, times(1)).getAllByUserId(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemById() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemAllDto);

        mvc.perform(get(API_PREFIX + "/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemAllDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemAllDto.getName())))
                .andExpect(jsonPath("$.description", is(itemAllDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemAllDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(itemAllDto.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.lastBooking.start",
                        is(itemAllDto.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$.nextBooking.end",
                        is(itemAllDto.getNextBooking().getEnd().toString())))
                .andExpect(jsonPath("$.nextBooking.booker",
                        is(itemAllDto.getNextBooking().getBooker()),
                        UserDto.class))
                .andExpect(jsonPath("$.comments", hasSize(1)));
        verify(itemService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void update() {
        UpdateItemDto updateItemDto = new UpdateItemDto("newName", "newDescription", Boolean.TRUE);
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE);

        when(itemService.update(anyLong(), anyLong(), any(UpdateItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch(API_PREFIX + "/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void getBySearch() {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE);

        when(itemService.getBySearch(anyLong(), anyString())).thenReturn(List.of(itemDto));

        mvc.perform(get(API_PREFIX + "/search")
                        .queryParam("text", "findByName")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
        verify(itemService, times(1)).getBySearch(anyLong(), anyString());
    }
}