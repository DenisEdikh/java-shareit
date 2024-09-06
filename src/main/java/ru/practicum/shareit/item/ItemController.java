package ru.practicum.shareit.item;

import jakarta.validation.Valid;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody NewItemDto newItemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating new item");
        final ItemDto itemDto = itemService.create(userId, newItemDto);
        log.info("Finished creating new item");
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting all items");
        final List<ItemDto> itemsDto = itemService.getAllByUserId(userId);
        log.info("Finished getting all items");
        return itemsDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable(value = "itemId") Long itemId) {
        log.info("Started getting item by id = {}", itemId);
        final ItemDto itemDto = itemService.getById(itemId);
        log.info("Finished getting item by id = {}", itemId);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody UpdateItemDto updateItemDto,
                          @PathVariable(value = "itemId") Long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started updating item with id {}", itemId);
        final ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);
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