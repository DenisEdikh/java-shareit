package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingForAllItemDto;
import ru.practicum.shareit.server.comment.dto.CommentDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemAllDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForAllItemDto lastBooking;
    private BookingForAllItemDto nextBooking;
    private Set<CommentDto> comments;

    public void addComment(List<CommentDto> comments) {
        if (this.comments == null) {
            this.comments = new HashSet<>();
        }
        this.comments.addAll(comments);
    }
}
