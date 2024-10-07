package ru.practicum.shareit.server.user;

import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UpdateUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    UserDto update(Long userId, UpdateUserDto updateUserDto);

    void delete(Long userId);

    List<UserDto> getAll();

    UserDto getById(Long userId);
}
