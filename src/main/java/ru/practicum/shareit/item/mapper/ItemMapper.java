package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static List<ItemDto> toItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public static Item toItem(Long userId, NewItemDto newItemDto) {
        Item item = new Item();

        item.setUserId(userId);
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        return item;
    }

    public static Item toItem(Long itemId, Long userId, UpdateItemDto updateItemDto) {
        Item item = new Item();

        item.setId(itemId);
        item.setUserId(userId);
        item.setName(updateItemDto.getName());
        item.setDescription(updateItemDto.getDescription());
        item.setAvailable(updateItemDto.getAvailable());
        return item;
    }
}