package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long lastUsedId = 0L;

    private Long generateId() {
        return ++lastUsedId;
    }

    @Override
    public Item create(Item item) {
        final Long id = generateId();
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .toList();
    }

    @Override
    public List<Item> findAll() {
        return List.copyOf(items.values());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }
}
