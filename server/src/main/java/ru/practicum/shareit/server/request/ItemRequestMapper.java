package ru.practicum.shareit.server.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {
    public static ItemRequest toItemRequest(NewItemRequestDto newItemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(newItemRequestDto.getDescription());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public static ItemRequestWithItemDto toAllItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestWithItemDto allItemRequestDto = new ItemRequestWithItemDto();

        allItemRequestDto.setId(itemRequest.getId());
        allItemRequestDto.setDescription(itemRequest.getDescription());
        allItemRequestDto.setCreated(itemRequest.getCreated());
        allItemRequestDto.addItems(ItemMapper.toItemDto(items));
        return allItemRequestDto;
    }
}
