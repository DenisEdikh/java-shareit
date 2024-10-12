package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UpdateUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTestIT {
    private NewUserDto newUserDto = new NewUserDto("name", "email@email.com");
    private final UserService userService;

    @Test
    void create() {
        UserDto userDto = userService.create(newUserDto);

        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals(userDto.getName(), newUserDto.getName());
        assertEquals(userDto.getEmail(), newUserDto.getEmail());
    }

    @Test
    void updateNameOfUser() {
        UserDto userDto = userService.create(newUserDto);
        UpdateUserDto updateUserDto = new UpdateUserDto("newName", null);
        UserDto updatedUser = userService.update(userDto.getId(), updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getName(), updateUserDto.getName());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
    }

    @Test
    void updateEmailOfUser() {
        UserDto userDto = userService.create(newUserDto);
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "newEmail@email.com");
        UserDto updatedUser = userService.update(userDto.getId(), updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getName(), userDto.getName());
        assertEquals(updatedUser.getEmail(), updateUserDto.getEmail());
    }

    @Test
    void delete() {
        UserDto userDto = userService.create(newUserDto);
        userService.delete(userDto.getId());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getById(userDto.getId()));
        assertEquals(notFoundException.getMessage(), String.format("User with id = %d not found", userDto.getId()));
    }

    @Test
    void getAll() {
        UserDto userDto = userService.create(newUserDto);
        UserDto userDto1 = userService.create(new NewUserDto("name2", "email2@email.com"));

        List<UserDto> users = userService.getAll();
        assertNotNull(users);
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getName(), userDto.getName());
        assertEquals(users.get(0).getEmail(), userDto.getEmail());
        assertEquals(users.get(1).getName(), userDto1.getName());
        assertEquals(users.get(1).getEmail(), userDto1.getEmail());
    }

    @Test
    void getById() {
        UserDto userDto = userService.create(newUserDto);

        UserDto savedUserDto = userService.getById(userDto.getId());
        assertNotNull(savedUserDto);
        assertEquals(savedUserDto.getName(), userDto.getName());
        assertEquals(savedUserDto.getEmail(), userDto.getEmail());
    }
}