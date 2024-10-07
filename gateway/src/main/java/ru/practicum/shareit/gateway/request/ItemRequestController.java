package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.gateway.request.dto.NewItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody NewItemRequestDto newItemRequestDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating new request by user with id = {}", userId);
        final ResponseEntity<Object> itemRequest = itemRequestClient.createItemRequest(userId, newItemRequestDto);
        log.info("Finished creating new request by user with id = {}", userId);
        return itemRequest;
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting all request with item by user with id = {}", userId);
        final ResponseEntity<Object> itemRequests = itemRequestClient.getItemRequestsByUserId(userId);
        log.info("Finished getting all request with item by user with id = {}", userId);
        return itemRequests;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsByOther(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting all request with item by user with id not {}", userId);
        final ResponseEntity<Object> itemRequests = itemRequestClient.getItemRequestsByOther(userId);
        log.info("Finished getting all request with item by user with id not {}", userId);
        return itemRequests;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestsById(@PathVariable(name = "requestId") Long requestId) {
        log.info("Started getting all request with item by id = {}", requestId);
        final ResponseEntity<Object> itemRequest = itemRequestClient.getItemRequestsById(requestId);
        log.info("Finished getting all request with item by id = {}", requestId);
        return itemRequest;
    }
}
