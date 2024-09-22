package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComment;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.user.User;

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

    public static ItemDtoComment toItemDtoComment(Item item, List<Comment> comments) {
        ItemDtoComment itemDtoComment = new ItemDtoComment();

        itemDtoComment.setId(item.getId());
        itemDtoComment.setName(item.getName());
        itemDtoComment.setDescription(item.getDescription());
        itemDtoComment.setAvailable(item.getAvailable());
        itemDtoComment.addComment(CommentMapper.toCommentDto(comments));
        return itemDtoComment;
    }


    public static ItemDtoTime toItemDtoTime(Item item,
                                            List<Comment> comments,
                                            Booking lastBooking,
                                            Booking nextBooking) {
        ItemDtoTime itemDtoTime = new ItemDtoTime();
        itemDtoTime.setId(item.getId());
        itemDtoTime.setName(item.getName());
        itemDtoTime.setDescription(item.getDescription());
        itemDtoTime.setAvailable(item.getAvailable());
        itemDtoTime.addComment(CommentMapper.toCommentDto(comments));
        itemDtoTime.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        itemDtoTime.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        return itemDtoTime;
    }

    public static Item toItem(User owner, NewItemDto newItemDto) {
        Item item = new Item();

        item.setOwner(owner);
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        return item;
    }
}