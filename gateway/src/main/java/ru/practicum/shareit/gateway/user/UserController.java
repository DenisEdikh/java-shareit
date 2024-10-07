package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UpdateUserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Started creating new user");
        final ResponseEntity<Object> user = userClient.create(newUserDto);
        log.info("Finished creating new user");
        return user;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Started getting all users");
        final ResponseEntity<Object> users = userClient.getAll();
        log.info("Finished getting all users");
        return users;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "userId") Long id) {
        log.info("Started getting user by id = {}", id);
        final ResponseEntity<Object> user = userClient.getById(id);
        log.info("Finished getting user by id = {}", id);
        return user;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateUserDto updateUserDto,
                                         @PathVariable(value = "userId") Long id) {
        log.info("Started updating user by id = {}", id);
        final ResponseEntity<Object> user = userClient.update(id, updateUserDto);
        log.info("Finished updating user by id = {}", id);
        return user;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") Long id) {
        log.info("Started deleting user by id = {}", id);
        final ResponseEntity<Object> user = userClient.delete(id);
        log.info("Finished deleting user by id = {}", id);
        return user;
    }
}