package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.common.SortType;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        log.debug("Started checking contains user with userId {} in method createItemRequest", userId);
        final User user = checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method createItemRequest", userId);
        final ItemRequest itemRequest = ItemRequestMapper.toItemRequest(newItemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemDto> getItemRequestsByUserId(Long userId) {
        log.debug("Started checking contains user with userId {} in method getItemRequestsByUserId", userId);
        final User user = checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method getItemRequestsByUserId", userId);
        //TODO сделать запросы с Fetch JOIN
        //map: key-itemRequestId, value-ItemRequest
        final Map<Long, ItemRequest> requestMap = itemRequestRepository
                .findByRequesterId(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        // map: key-itemRequestId, value-Item
        final Map<Long, List<Item>> itemMap = itemRepository
                .findAllByItemRequestIdIn(requestMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));
        return makeItemRequestWithItem(requestMap, itemMap, SortType.DESC);
    }

    @Override
    public List<ItemRequestWithItemDto> getItemRequestsByOther(Long userId) {
        //TODO сделать запросы с Fetch JOIN
        log.debug("Started checking contains user with userId {} in method getItemRequestsByOther", userId);
        final User user = checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method getItemRequestsByOther", userId);
        // map: key-itemRequestId, value-ItemRequest
        final Map<Long, ItemRequest> requestMap = itemRequestRepository
                .findByRequesterIdNot(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        // map: key-itemRequestId, value-Item
        final Map<Long, List<Item>> itemMap = itemRepository
                .findAllByItemRequestIdIn(requestMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));
        return makeItemRequestWithItem(requestMap, itemMap, SortType.DESC);
    }

    @Override
    public ItemRequestWithItemDto getItemRequestsById(Long requestId) {
        final ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("ItemRequest with id {} not found", requestId);
            return new NotFoundException(String.format("ItemRequest with id = %d not found", requestId));
        });
        final List<Item> items = itemRepository.findAllByItemRequestId(requestId);
        return ItemRequestMapper.toAllItemRequestDto(itemRequest, items);
    }

    private List<ItemRequestWithItemDto> makeItemRequestWithItem(Map<Long, ItemRequest> requestMap,
                                                                 Map<Long, List<Item>> itemMap,
                                                                 SortType sortType) {
        return requestMap.values()
                .stream()
                .map(itemRequest -> {
                    List<Item> items = itemMap.getOrDefault(itemRequest.getId(), List.of());
                    return ItemRequestMapper.toAllItemRequestDto(itemRequest, items);
                })
                .sorted((ItemRequestWithItemDto item1, ItemRequestWithItemDto item2) -> {
                    if (SortType.DESC == sortType) {
                        return item2.getCreated().compareTo(item1.getCreated());
                    } else if (SortType.ASC == sortType) {
                        return item1.getCreated().compareTo(item2.getCreated());
                    } else {
                        return -1;
                    }
                })
                .toList();
    }

    private User checkUserIsContained(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException(String.format("User with id = %d not found", userId));
        });
    }
}
