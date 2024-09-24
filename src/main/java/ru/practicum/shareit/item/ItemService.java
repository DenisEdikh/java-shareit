package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemDto newItemDto);

    ItemDto update(Long itemId, Long userId, UpdateItemDto updateItemDto);

    List<ItemDto> getAllByUserId(Long userId);

    ItemDtoTime getById(Long itemId);

    List<ItemDto> getBySearch(Long userId, String text);

    CommentDto createComment(Long itemId, Long userId, NewCommentDto newCommentDto);

    List<Comment> getCommentsByItemId(Long itemId);
}
