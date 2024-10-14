package ru.practicum.shareit.gateway.request;

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
import ru.practicum.shareit.gateway.request.dto.NewItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTestIT {
    private static final String API_PREFIX = "/requests";

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void createItemRequest_WhenNewItemRequestDtoValid_ThenReturnOk() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("smart drill");

        when(itemRequestClient.createItemRequest(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(newItemRequestDto, HttpStatus.OK));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description", is(newItemRequestDto.getDescription())));
        verify(itemRequestClient, times(1))
                .createItemRequest(anyLong(), any(NewItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void createItemRequest_WhenNewItemRequestDtoNotValid_ThenReturnOk() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("  ");

        when(itemRequestClient.createItemRequest(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(newItemRequestDto, HttpStatus.OK));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isBadRequest());
        verify(itemRequestClient, never()).createItemRequest(anyLong(), any(NewItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void getItemRequestsByUserId_WhenValid_ThenReturnOk() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("smart drill");

        when(itemRequestClient.getItemRequestsByUserId(anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(newItemRequestDto), HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description", is(newItemRequestDto.getDescription())));
        verify(itemRequestClient, times(1)).getItemRequestsByUserId(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOther() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("smart drill");

        when(itemRequestClient.getItemRequestsByOther(anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(newItemRequestDto), HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description", is(newItemRequestDto.getDescription())));
        verify(itemRequestClient, times(1)).getItemRequestsByOther(anyLong());
    }

    @Test
    @SneakyThrows
    void getItemRequestsById() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("smart drill");

        when(itemRequestClient.getItemRequestsById(anyLong()))
                .thenReturn(new ResponseEntity<>(newItemRequestDto, HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/{requestId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description", is(newItemRequestDto.getDescription())));
        verify(itemRequestClient, times(1)).getItemRequestsById(anyLong());
    }
}