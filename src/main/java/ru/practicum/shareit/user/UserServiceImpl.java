package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

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
        final User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException(String.format("User with id = %d not found", userId));
        });
        checkEmail(UserMapper.toUser(userId, updateUserDto));
        log.debug("Finished checking contains user with userId {} in method update", userId);

        if (Objects.nonNull(updateUserDto.getName()) && !updateUserDto.getName().isBlank()) {
            user.setName(updateUserDto.getName());
        }
        if (Objects.nonNull(updateUserDto.getEmail()) && !updateUserDto.getEmail().isBlank()) {
            user.setEmail(updateUserDto.getEmail());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        log.debug("Started checking contains user with id {}", userId);
        getById(userId);
        log.debug("Finished checking contains user with id {}", userId);
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException(String.format("User with id = %d not found", userId));
        }));
    }

    private void checkEmail(User user) {
        if (userRepository.findAll().stream()
                .anyMatch(u -> Objects.equals(u.getEmail(), user.getEmail()))) {
            throw new ConditionsNotMetException("Email must not match");
        }
    }
}