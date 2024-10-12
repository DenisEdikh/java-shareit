package ru.practicum.shareit.server.handler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.ConditionsNotMetException;
import ru.practicum.shareit.server.exception.InternalServerException;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.UserController;
import ru.practicum.shareit.server.user.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ErrorHandler.class, UserController.class})
class ErrorHandlerTest {
    private static final String API_PREFIX = "/users";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void invalidRequest() {
        when(userService.getAll()).thenThrow(new InvalidRequestException("exception"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("exception")))
                .andExpect(jsonPath("$.description", is("Неподдерживаемый тип запроса")));
    }

    @Test
    @SneakyThrows
    void unprocessableEntity() {
        when(userService.getAll()).thenThrow(new ConditionsNotMetException("exception"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Ошибка валидации")))
                .andExpect(jsonPath("$.description", is("exception")));
    }

    @Test
    @SneakyThrows
    void notFound() {
        when(userService.getAll()).thenThrow(new NotFoundException("exception"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Искомый объект не найден")))
                .andExpect(jsonPath("$.description", is("exception")));
    }

    @Test
    @SneakyThrows
    void internalServerError() {
        when(userService.getAll()).thenThrow(new InternalServerException("exception"));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Внутренняя ошибка")))
                .andExpect(jsonPath("$.description", is("exception")));
    }
}