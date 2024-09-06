package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserDto newUserDto) {
        log.debug("Started checking email user in method create");
        final User user = UserMapper.toUser(newUserDto);
        checkEmail(user);
        log.debug("Finished checking email user in method create");
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long userId, UpdateUserDto updateUserDto) {
        log.debug("Started checking contains user with userId {} in method update", userId);
        final User user = UserMapper.toUser(userId, updateUserDto);
        getById(userId);
        checkEmail(user);
        log.debug("Finished checking contains user with userId {} in method update", userId);
        if (updateUserDto.getName() != null && updateUserDto.getEmail() != null) {
            return UserMapper.toUserDto(userRepository.updateUser(user));
        } else if (updateUserDto.getName() == null && updateUserDto.getEmail() != null) {
            return UserMapper.toUserDto(userRepository.updateUserEmail(user));
        } else if (updateUserDto.getName() != null && updateUserDto.getEmail() == null) {
            return UserMapper.toUserDto(userRepository.updateUserName(user));
        } else {
            log.warn("Name and email must exist");
            throw new ConditionsNotMetException("Name and email must exist");
        }
    }

    @Override
    public void delete(Long id) {
        log.debug("Started checking contains user with id {}", id);
        getById(id);
        log.debug("Finished checking contains user with id {}", id);
        userRepository.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            return new NotFoundException(String.format("User with id = %d not found", id));
        }));
    }

    private void checkEmail(User user) {
        if (userRepository.findAll().stream()
                .anyMatch(u -> Objects.equals(u.getEmail(), user.getEmail()))) {
            throw new ConditionsNotMetException("Email must not match");
        }
    }
}