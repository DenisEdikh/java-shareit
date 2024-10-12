package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UpdateUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String API_PREFIX = "/users";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void create() {
        NewUserDto newUserDto = new NewUserDto("name", "email@email.com");
        UserDto userDto = new UserDto(1L, "name", "email@email.com");

        when(userService.create(any(NewUserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).create(any(NewUserDto.class));
    }

    @Test
    @SneakyThrows
    void getAll() {
        UserDto userDto = new UserDto(1L, "name", "email@email.com");
        UserDto userDto2 = new UserDto(2L, "name2", "email2@email.com");

        when(userService.getAll()).thenReturn(List.of(userDto, userDto2));

        mvc.perform(get(API_PREFIX))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));

        verify(userService, times(1)).getAll();
    }

    @Test
    @SneakyThrows
    void getUserById() {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "name", "email@email.com");

        when(userService.getById(userId)).thenReturn(userDto);

        mvc.perform(get(API_PREFIX + "/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1)).getById(anyLong());
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto("name", "email@email.com");
        UserDto userDto = new UserDto(userId, "name", "email@email.com");

        when(userService.update(anyLong(), any(UpdateUserDto.class))).thenReturn(userDto);

        mvc.perform(patch(API_PREFIX + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1)).update(anyLong(), any(UpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        Long userId = 1L;

        doNothing().when(userService).delete(anyLong());

        mvc.perform(delete(API_PREFIX + "/{id}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
    }
}