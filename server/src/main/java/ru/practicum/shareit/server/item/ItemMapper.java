package ru.practicum.shareit.server.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingMapper;
import ru.practicum.shareit.server.comment.Comment;
import ru.practicum.shareit.server.comment.CommentMapper;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemAllDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(toItemDto(item));
        }
        return itemDtos;
    }

    public static ItemAllDto toItemAllDto(Item item,
                                          List<Comment> comments,
                                          Booking lastBooking,
                                          Booking nextBooking) {
        ItemAllDto itemDtoTime = new ItemAllDto();
        itemDtoTime.setId(item.getId());
        itemDtoTime.setName(item.getName());
        itemDtoTime.setDescription(item.getDescription());
        itemDtoTime.setAvailable(item.getAvailable());
        itemDtoTime.addComment(CommentMapper.toCommentDto(comments));
        itemDtoTime.setLastBooking(BookingMapper.toBookingForAllItemDto(lastBooking));
        itemDtoTime.setNextBooking(BookingMapper.toBookingForAllItemDto(nextBooking));
        return itemDtoTime;
    }

    public static Item toItem(User owner, NewItemDto newItemDto, ItemRequest itemRequest) {
        Item item = new Item();

        item.setOwner(owner);
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        item.setItemRequest(itemRequest);
        return item;
    }
}