package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, NewItemDto newItemDto) {
        log.debug("Started checking contains user with userId {} in method update", userId);
        userService.getById(userId);
        log.debug("Finished checking contains user with userId {} in method update", userId);
        final Item item = ItemMapper.toItem(userId, newItemDto);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, UpdateItemDto updateItemDto) {
        log.debug("Started checking contains user with userId {} and item with itemId {} in method update",
                userId,
                itemId);
        userService.getById(userId);
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found ", itemId);
            return new NotFoundException(String.format("Item with id = %d not found ", itemId));
        });
        log.debug("Finished checking contains user with userId {} and item with itemId {} in method update",
                userId,
                itemId);
        if (Objects.nonNull(updateItemDto.getName()) && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }
        if (Objects.nonNull(updateItemDto.getDescription()) && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (Objects.nonNull(updateItemDto.getAvailable())) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        log.debug("Started checking contains user with userId {} in method getAllByUserId", userId);
        userService.getById(userId); //сделать отдельный метод проверки
        log.debug("Finished checking contains user with userId {} in method getAllByUserId", userId);
        return ItemMapper.toItemDto(itemRepository.findAllByUserId(userId));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found ", itemId);
            return new NotFoundException(String.format("Item with id = %d not found ", itemId));
        }));
    }

    @Override
    public List<ItemDto> getBySearch(Long userId, String text) {
        log.debug("Started checking contains user with userId {} in method getBySearch", userId);
        userService.getById(userId);
        log.debug("Finished checking contains user with userId {} in method getBySearch", userId);

        if (Objects.equals(text, "")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.toItemDto(itemRepository.findAll().stream()
                    .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                            && item.getAvailable())
                    .toList());
        }
    }
}
