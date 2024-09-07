package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    void delete(Long itemId);

    List<Item> findAllByUserId(Long userId);

    List<Item> findAll();

    Optional<Item> findById(Long id);
}
