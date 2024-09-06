package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    UserDto update(Long id, UpdateUserDto updateUserDto);

    void delete(Long id);

    List<UserDto> getAll();

    UserDto getById(Long id);
}
