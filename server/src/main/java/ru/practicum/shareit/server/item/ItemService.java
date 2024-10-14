package ru.practicum.shareit.server.item;

import ru.practicum.shareit.server.comment.Comment;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.NewCommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemAllDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemDto newItemDto);

    ItemDto update(Long itemId, Long userId, UpdateItemDto updateItemDto);

    List<ItemAllDto> getAllByUserId(Long userId);

    ItemAllDto getById(Long itemId, Long userId);

    List<ItemDto> getBySearch(Long userId, String text);

    CommentDto createComment(Long itemId, Long userId, NewCommentDto newCommentDto);

    List<Comment> getCommentsByItemId(Long itemId);
}
