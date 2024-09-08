package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Started creating new user");
        final UserDto userDto = userService.create(newUserDto);
        log.info("Finished creating new user");
        return userDto;
    }


    @GetMapping
    public List<UserDto> getAll() {
        log.info("Started getting all users");
        final List<UserDto> users = userService.getAll();
        log.info("Finished getting all users");
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(value = "userId") Long id) {
        log.info("Started getting user by id = {}", id);
        final UserDto userDto = userService.getById(id);
        log.info("Finished getting user by id = {}", id);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UpdateUserDto updateUserDto,
                          @PathVariable(value = "userId") Long id) {
        log.info("Started updating user by id = {}", id);
        final UserDto userDto = userService.update(id, updateUserDto);
        log.info("Finished updating user by id = {}", id);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(value = "userId") Long id) {
        log.info("Started deleting user by id = {}", id);
        userService.delete(id);
        log.info("Finished deleting user by id = {}", id);
    }
}