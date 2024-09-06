package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
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
        getById(itemId);
        log.debug("Finished checking contains user with userId {} and item with itemId {} in method update",
                userId,
                itemId);
        final Item item = ItemMapper.toItem(itemId, userId, updateItemDto);

        if (updateItemDto.getName() != null
                && updateItemDto.getDescription() != null
                && updateItemDto.getAvailable() != null) {
            return ItemMapper.toItemDto(itemRepository.updateItem(item));
        } else if (updateItemDto.getName() != null
                && updateItemDto.getDescription() == null
                && updateItemDto.getAvailable() == null) {
            return ItemMapper.toItemDto(itemRepository.updateItemName(item));
        } else if (updateItemDto.getName() == null
                && updateItemDto.getDescription() != null
                && updateItemDto.getAvailable() == null) {
            return ItemMapper.toItemDto(itemRepository.updateItemDescription(item));
        } else if (updateItemDto.getName() == null
                && updateItemDto.getDescription() == null
                && updateItemDto.getAvailable() != null) {
            return ItemMapper.toItemDto(itemRepository.updateItemAvailable(item));
        } else {
            log.warn("Name, description and available must exist");
            throw new ConditionsNotMetException("Name, description and available must exist");
        }
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        log.debug("Started checking contains user with userId {} in method getAllByUserId", userId);
        userService.getById(userId);
        log.debug("Finished checking contains user with userId {} in method getAllByUserId", userId);
        return ItemMapper.toItemDto(itemRepository.findAllByUserId(userId));
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Item with id {} not found ", id);
            return new NotFoundException(String.format("Item with id = %d not found ", id));
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
