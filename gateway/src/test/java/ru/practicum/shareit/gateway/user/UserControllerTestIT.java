package ru.practicum.shareit.gateway.user;

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
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UpdateUserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTestIT {
    private static final String API_PREFIX = "/users";

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void createUser_WhenNewUserDtoValid_ThenReturnOk() {
        NewUserDto newUserDtoValid = new NewUserDto("name", "email@email.com");

        when(userClient.create(any(NewUserDto.class)))
                .thenReturn(new ResponseEntity<>(newUserDtoValid, HttpStatus.OK));

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDtoValid)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(newUserDtoValid.getName())));
        verify(userClient, times(1)).create(any(NewUserDto.class));
    }

    @Test
    @SneakyThrows
    void createUser_WhenNewUserDtoNotValid_ThenReturnOk() {
        NewUserDto newUserDtoNotValid = new NewUserDto("", "email@email.com");

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDtoNotValid)))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).create(any(NewUserDto.class));
    }

    @Test
    @SneakyThrows
    void getAll_WhenValid_ThenReturnOk() {
        NewUserDto newUserDtoValid = new NewUserDto("name", "email@email.com");

        when(userClient.getAll()).thenReturn(new ResponseEntity<>(List.of(newUserDtoValid), HttpStatus.OK));

        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is(newUserDtoValid.getName())));
        verify(userClient, times(1)).getAll();
    }

    @Test
    @SneakyThrows
    void getUserById_WhenValid_ThenReturnOk() {
        NewUserDto newUserDtoValid = new NewUserDto("name", "email@email.com");

        when(userClient.getById(anyLong())).thenReturn(new ResponseEntity<>(newUserDtoValid, HttpStatus.OK));

        mvc.perform(get(API_PREFIX + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(newUserDtoValid.getName())));
        verify(userClient, times(1)).getById(anyLong());
    }

    @Test
    @SneakyThrows
    void updateUser_WhenUpdateUserDtoValid_ThenReturnOk() {
        UpdateUserDto updateUserDtoValid = new UpdateUserDto("name", "email@email.com");

        when(userClient.update(anyLong(), any(UpdateUserDto.class)))
                .thenReturn(new ResponseEntity<>(updateUserDtoValid, HttpStatus.OK));

        mvc.perform(patch(API_PREFIX + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDtoValid)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(updateUserDtoValid.getName())));
        verify(userClient, times(1)).update(anyLong(), any(UpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void updateUser_WhenUpdateUserDtoNotValid_ThenReturnOk() {
        UpdateUserDto updateUserDtoNotValid = new UpdateUserDto("name", "email.email.com");

        when(userClient.update(anyLong(), any(UpdateUserDto.class)))
                .thenReturn(new ResponseEntity<>(updateUserDtoNotValid, HttpStatus.OK));

        mvc.perform(patch(API_PREFIX + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDtoNotValid)))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).update(anyLong(), any(UpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void deleteUser_WhenValid_ThenReturnOk() {
        mvc.perform(delete(API_PREFIX + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userClient, times(1)).delete(anyLong());
    }
}