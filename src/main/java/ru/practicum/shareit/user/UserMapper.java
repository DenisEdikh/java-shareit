package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static List<UserDto> toUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    public static User toUser(NewUserDto newUserDto) {
        User user = new User();

        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        return user;
    }

    public static User toUser(Long userId, UpdateUserDto updateUserDto) {
        User user = new User();

        user.setId(userId);
        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        return user;
    }
}