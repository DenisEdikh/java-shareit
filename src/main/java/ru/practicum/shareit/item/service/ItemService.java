package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemDto newItemDto);

    ItemDto update(Long itemId, Long userId, UpdateItemDto updateItemDto);

    List<ItemDto> getAllByUserId(Long userId);

    ItemDto getById(Long itemId);

    List<ItemDto> getBySearch(Long userId, String text);
}
