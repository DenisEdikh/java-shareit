package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    void delete(Long userId);

    List<User> findAll();

    Optional<User> findById(Long id);
}
