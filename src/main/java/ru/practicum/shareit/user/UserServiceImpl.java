package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        log.debug("Started checking email user in method create");
        final User user = UserMapper.toUser(newUserDto);
        log.debug("Finished checking email user in method create");
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UpdateUserDto updateUserDto) {
        log.debug("Started checking contains user with userId {} in method update", userId);
        final User user = checkUserById(userId);
        log.debug("Finished checking contains user with userId {} in method update", userId);
        if (Objects.nonNull(updateUserDto.getName()) && !updateUserDto.getName().isBlank()) {
            user.setName(updateUserDto.getName());
        }
        if (Objects.nonNull(updateUserDto.getEmail()) && !updateUserDto.getEmail().isBlank()) {
            user.setEmail(updateUserDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        log.debug("Started checking contains user with id {}", userId);
        getById(userId);
        log.debug("Finished checking contains user with id {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(checkUserById(userId));
    }

    private User checkUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException(String.format("User with id = %d not found", userId));
        });
    }
}