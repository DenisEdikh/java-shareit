package ru.practicum.shareit.gateway.item;

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
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(ItemController.class)
class ItemControllerTestIT {
    private static final String API_PREFIX = "/items";

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void createItem_WhenNewItemDtoValid_ThenReturnOk() {
        NewItemDto newItemDtoValid = new NewItemDto("name", "description", Boolean.TRUE, 1L);

        when(itemClient.create(anyLong(), any(NewItemDto.class)))
                .thenReturn(new ResponseEntity<>(newItemDtoValid, HttpStatus.OK));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemDtoValid)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requestId", is(newItemDtoValid.getRequestId()), Long.class));
        verify(itemClient, times(1)).create(anyLong(), any(NewItemDto.class));
    }

    @Test
    @SneakyThrows
    void createItem_WhenNewItemDtoNotValid_ThenReturnBadRequest() {
        NewItemDto newItemDtoNotValid = new NewItemDto(null, "description", Boolean.TRUE, 1L);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemDtoNotValid)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(anyLong(), any(NewItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_WhenUpdateItemDtoValid_ThenReturnOk() {
        UpdateItemDto updateItemDtoValid = new UpdateItemDto("name", "description", Boolean.TRUE);

        when(itemClient.update(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(new ResponseEntity<>(updateItemDtoValid, HttpStatus.OK));

        mvc.perform(patch(API_PREFIX + "/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(updateItemDtoValid)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(updateItemDtoValid.getName())));
        verify(itemClient, times(1)).update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void getAllItems_WhenValid_ThenReturnOk() {
        NewItemDto newItemDtoValid = new NewItemDto("name", "description", Boolean.TRUE, 1L);

        when(itemClient.getAllByUserId(anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(newItemDtoValid), HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is(newItemDtoValid.getName())));
        verify(itemClient, times(1)).getAllByUserId(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemById_WhenValid_ThenReturnOk() {
        NewItemDto newItemDtoValid = new NewItemDto("name", "description", Boolean.TRUE, 1L);

        when(itemClient.getAllByUserId(anyLong())).thenReturn(new ResponseEntity<>(newItemDtoValid, HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(newItemDtoValid.getName())));
        verify(itemClient, times(1)).getAllByUserId(anyLong());
    }

    @Test
    @SneakyThrows
    void getBySearch_WhenSearchValid_ThenReturnOk() {
        NewItemDto newItemDtoValid = new NewItemDto("name", "description", Boolean.TRUE, 1L);

        when(itemClient.getBySearch(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(newItemDtoValid, HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("text", "search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(newItemDtoValid.getName())));
        verify(itemClient, times(1)).getBySearch(anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    void createComment_WhenNewCommentDtoValid_ThenReturnOk() {
        NewCommentDto newCommentDtoValid = new NewCommentDto("text");

        when(itemClient.createComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(new ResponseEntity<>(newCommentDtoValid, HttpStatus.OK));

        mvc.perform(post(API_PREFIX + "/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newCommentDtoValid)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text", is(newCommentDtoValid.getText())));
        verify(itemClient, times(1))
                .createComment(anyLong(), anyLong(), any(NewCommentDto.class));
    }

    @Test
    @SneakyThrows
    void createComment_WhenNewCommentDtoNotValid_ThenReturnOk() {
        NewCommentDto newCommentDtoNotValid = new NewCommentDto("");

        when(itemClient.createComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(new ResponseEntity<>(newCommentDtoNotValid, HttpStatus.OK));

        mvc.perform(post(API_PREFIX + "/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newCommentDtoNotValid)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).createComment(anyLong(), anyLong(), any(NewCommentDto.class));
    }
}