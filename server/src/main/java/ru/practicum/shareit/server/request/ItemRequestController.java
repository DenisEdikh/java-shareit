package ru.practicum.shareit.server.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody NewItemRequestDto newItemRequestDto,
                                            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating new request by user with id = {}", userId);
        final ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(userId, newItemRequestDto);
        log.info("Finished creating new request by user with id = {}", userId);
        return itemRequestDto;
    }

    @GetMapping
    public List<ItemRequestWithItemDto> getItemRequestsByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting all request with item by user with id = {}", userId);
        final List<ItemRequestWithItemDto> itemRequests = itemRequestService.getItemRequestsByUserId(userId);
        log.info("Finished getting all request with item by user with id = {}", userId);
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDto> getItemRequestsByOther(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting all request with item by user with id not {}", userId);
        final List<ItemRequestWithItemDto> itemRequests = itemRequestService.getItemRequestsByOther(userId);
        log.info("Finished getting all request with item by user with id not {}", userId);
        return itemRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto getItemRequestsById(@PathVariable(name = "requestId") Long requestId) {
        log.info("Started getting all request with item by id = {}", requestId);
        final ItemRequestWithItemDto itemRequest = itemRequestService.getItemRequestsById(requestId);
        log.info("Finished getting all request with item by id = {}", requestId);
        return itemRequest;
    }
}
