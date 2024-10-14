package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.NewCommentDto;
import ru.practicum.shareit.server.item.dto.ItemAllDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody NewItemDto newItemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started creating new item");
        final ItemDto itemDto = itemService.create(ownerId, newItemDto);
        log.info("Finished creating new item");
        return itemDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody NewCommentDto newCommentDto,
                                    @PathVariable(name = "itemId") Long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating comment with itemId = {}", itemId);
        final CommentDto commentDto = itemService.createComment(itemId, userId, newCommentDto);
        log.info("Generated creating comment with itemId = {}", itemId);
        return commentDto;
    }

    @GetMapping
    public List<ItemAllDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started getting all items");
        final List<ItemAllDto> itemsDto = itemService.getAllByUserId(ownerId);
        log.info("Finished getting all items");
        return itemsDto;
    }

    @GetMapping("/{itemId}")
    public ItemAllDto getItemById(@PathVariable(value = "itemId") Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting item by id = {}", itemId);
        final ItemAllDto itemDto = itemService.getById(itemId, userId);
        log.info("Finished getting item by id = {}", itemId);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody UpdateItemDto updateItemDto,
                          @PathVariable(value = "itemId") Long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started updating item with id {}", itemId);
        final ItemDto itemDto = itemService.update(itemId, ownerId, updateItemDto);
        log.info("Finished updating item with id {}", itemId);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestParam(value = "text") String text,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started searching item contained text: {}", text);
        final List<ItemDto> itemsDto = itemService.getBySearch(userId, text);
        log.info("Finished searching item contained text: {}", text);
        return itemsDto;
    }
}