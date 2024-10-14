package ru.practicum.shareit.server.request;

import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestWithItemDto> getItemRequestsByUserId(Long userId);

    List<ItemRequestWithItemDto> getItemRequestsByOther(Long userId);

    ItemRequestWithItemDto getItemRequestsById(Long requestId);
}
