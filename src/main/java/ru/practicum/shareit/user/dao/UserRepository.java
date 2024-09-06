package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User updateUser(User user);

    User updateUserName(User user);

    User updateUserEmail(User user);

    void delete(Long userId);

    List<User> findAll();

    Optional<User> findById(Long id);
}
