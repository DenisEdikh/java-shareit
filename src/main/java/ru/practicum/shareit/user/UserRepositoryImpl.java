package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long lastUsedId = 0L;

    private Long generatedId() {
        return ++lastUsedId;
    }

    @Override
    public User create(User user) {
        final Long id = generatedId();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}