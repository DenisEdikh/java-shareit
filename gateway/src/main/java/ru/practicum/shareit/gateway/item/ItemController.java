package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody NewItemDto newItemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started creating new item");
        final ResponseEntity<Object> item = itemClient.create(ownerId, newItemDto);
        log.info("Finished creating new item");
        return item;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateItemDto updateItemDto,
                                         @PathVariable(value = "itemId") Long itemId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started updating item with id {}", itemId);
        final ResponseEntity<Object> item = itemClient.update(itemId, ownerId, updateItemDto);
        log.info("Finished updating item with id {}", itemId);
        return item;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started getting all items");
        final ResponseEntity<Object> item = itemClient.getAllByUserId(ownerId);
        log.info("Finished getting all items");
        return item;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting item by id = {}", itemId);
        final ResponseEntity<Object> item = itemClient.getItemById(itemId, userId);
        log.info("Finished getting item by id = {}", itemId);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getBySearch(@RequestParam(value = "text") String text,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started searching item contained text: {}", text);
//        if (text.isBlank()) {
//            return ;
//        }
        final ResponseEntity<Object> item = itemClient.getBySearch(userId, text);
        log.info("Finished searching item contained text: {}", text);
        return item;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating comment with itemId = {}", itemId);
        final ResponseEntity<Object> comment = itemClient.createComment(itemId, userId, newCommentDto);
        log.info("Generated creating comment with itemId = {}", itemId);
        return comment;
    }
}